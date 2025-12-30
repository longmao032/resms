package com.guang.resms.module.house.service.impl;

import com.guang.resms.module.house.entity.House;
import com.guang.resms.module.house.entity.HouseImage;
import com.guang.resms.module.house.entity.NewHouseInfo;
import com.guang.resms.module.house.entity.SecondHouseInfo;
import com.guang.resms.module.house.entity.dto.HouseDTO;
import com.guang.resms.module.house.entity.dto.HouseQueryDTO;
import com.guang.resms.module.house.entity.vo.HouseVO;
import com.guang.resms.module.house.entity.vo.NewHouseInfoVO;
import com.guang.resms.module.house.entity.vo.ProjectVO;
import com.guang.resms.module.house.entity.vo.SecondHouseInfoVO;
import com.guang.resms.module.house.mapper.HouseImageMapper;
import com.guang.resms.module.house.mapper.HouseMapper;
import com.guang.resms.module.house.mapper.NewHouseInfoMapper;
import com.guang.resms.module.house.mapper.SecondHouseInfoMapper;
import com.guang.resms.module.house.service.HouseService;
import com.guang.resms.module.chat.service.NotificationService;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.FileUploadUtils;
import com.guang.resms.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.guang.resms.module.transaction.entity.Transaction;
import com.guang.resms.module.transaction.mapper.TransactionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 房源服务实现类
 *
 * @author guang
 */
@Slf4j
@Service
public class HouseServiceImpl implements HouseService {

    private final HouseMapper houseMapper;

    @Autowired
    private NewHouseInfoMapper newHouseInfoMapper;

    @Autowired
    private SecondHouseInfoMapper secondHouseInfoMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HouseImageMapper houseImageMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Value("${file.upload.path:uploads/house}")
    private String uploadPath;

    public HouseServiceImpl(HouseMapper houseMapper) {
        this.houseMapper = houseMapper;
    }

    @Override
    public Map<String, Object> pageList(HouseQueryDTO queryDTO) {
        // 参数验证：页码默认从1开始
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(10);
        }

        // ========== 数据权限过滤 ==========
        Integer currentUserId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();

        if (currentUserId == null || roleType == null) {
            throw new ServiceException("未登录或登录已过期，请重新登录");
        }

        if (roleType != null) {
            if (roleType == 2) {
                // 销售顾问: 只看自己负责的房源
                queryDTO.setSalesId(currentUserId);
            }
            // 管理员和销售经理可以看全部或按条件筛选
            // 如果前端传了salesId条件,保持使用前端传的值
        }
        queryDTO.setCurrentUserId(currentUserId);

        // 保存原始页码用于返回
        int currentPage = queryDTO.getPageNum();
        int pageSize = queryDTO.getPageSize();

        // 计算分页偏移量：(pageNum - 1) * pageSize
        int offset = (currentPage - 1) * pageSize;

        // 查询总数
        Long total = houseMapper.selectHouseCount(queryDTO);

        // 为 Mapper 设置 OFFSET 和 LIMIT 参数
        queryDTO.setPageNum(offset); // 传给 Mapper 的是 offset
        queryDTO.setPageSize(pageSize);

        // 查询列表
        List<HouseVO> list = houseMapper.selectHouseListWithDetails(queryDTO);

        // 设置状态文本和类型文本，并加载图片列表
        list.forEach(house -> {
            setTextFields(house);
            // 查询并设置房源图片列表
            List<String> images = houseMapper.selectHouseImages(house.getId());
            house.setImages(images);
        });

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", list);
        result.put("pageNum", currentPage); // 返回原始页码
        result.put("pageSize", pageSize);
        result.put("pages", (total + pageSize - 1) / pageSize); // 总页数

        return result;
    }

    @Override
    public HouseVO getHouseById(Integer id) {
        if (id == null || id <= 0) {
            throw new ServiceException("房源ID不能为空");
        }

        Integer currentUserId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();
        if (currentUserId == null || roleType == null) {
            throw new ServiceException("未登录或登录已过期，请重新登录");
        }

        HouseVO houseVO = houseMapper.selectHouseDetailById(id);
        if (houseVO == null) {
            throw new ServiceException("房源不存在");
        }

        if (roleType == 2) {
            if (houseVO.getSalesId() == null || !currentUserId.equals(houseVO.getSalesId())) {
                throw new ServiceException("无权限查看该房源");
            }
        }

        // 设置文本字段
        setTextFields(houseVO);

        // 查询房源图片列表
        List<String> images = houseMapper.selectHouseImages(id);
        houseVO.setImages(images);

        // 根据房源类型查询扩展信息
        if (houseVO.getHouseType() != null) {
            if (houseVO.getHouseType() == 2) {
                // 新房信息
                NewHouseInfoVO newHouseInfo = houseMapper.selectNewHouseInfo(id);
                if (newHouseInfo != null) {
                    houseVO.setNewHouseInfo(newHouseInfo);
                }
            } else if (houseVO.getHouseType() == 1) {
                // 二手房信息
                SecondHouseInfoVO secondHouseInfo = houseMapper.selectSecondHouseInfo(id);
                if (secondHouseInfo != null) {
                    // 设置状态文本
                    secondHouseInfo.setMortgageStatusText(getMortgageStatusText(secondHouseInfo.getMortgageStatus()));
                    secondHouseInfo
                            .setCertificateStatusText(getCertificateStatusText(secondHouseInfo.getCertificateStatus()));
                    houseVO.setSecondHouseInfo(secondHouseInfo);
                }
            }
        }

        // 查询项目详细信息
        if (houseVO.getProjectId() != null) {
            ProjectVO projectInfo = houseMapper.selectProjectInfo(houseVO.getProjectId());
            if (projectInfo != null) {
                houseVO.setProjectInfo(projectInfo);
            }
        }

        return houseVO;
    }

    // ... (省略addHouse前面的部分)

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addHouse(HouseDTO houseDTO) {
        Integer currentUserId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();
        if (currentUserId == null || roleType == null) {
            throw new ServiceException("未登录或登录已过期，请重新登录");
        }
        if (roleType == 4) {
            throw new ServiceException("无权限新增房源");
        }

        // 数据验证
        validateHouseData(houseDTO, false);

        // 生成房源编号
        String houseNo = houseMapper.generateHouseNo();
        if (houseNo == null || houseNo.isEmpty()) {
            throw new ServiceException("生成房源编号失败");
        }

        // 转换为实体类
        House house = new House();
        BeanUtils.copyProperties(houseDTO, house);
        house.setHouseNo(houseNo);

        // 销售顾问(roleType=2)新增房源: 强制status=0(待审核), salesId=当前用户
        if (roleType != null && roleType == 2) {
            house.setStatus(0); // 强制待审核
            house.setSalesId(currentUserId); // 强制绑定自己
        } else {
            // 管理员/经理: 默认待审核,允许指定salesId
            if (house.getStatus() == null) {
                house.setStatus(0); // 默认状态：待审核
            }
            if (house.getSalesId() == null) {
                house.setSalesId(currentUserId); // 未指定则默认为当前用户
            }
        }

        // 保存房源基本信息
        int result = houseMapper.insert(house);
        if (result <= 0) {
            throw new ServiceException("保存房源信息失败");
        }

        // 获取新增的房源ID
        Integer houseId = house.getId();

        // 保存扩展信息
        saveExtendInfo(houseDTO, houseId);

        // 保存图片信息
        saveImages(houseDTO, houseId, houseNo);

        // 发送自动通知给管理员
        String houseTitle = String.format("%s (%s)", houseNo, houseDTO.getLayout());
        notificationService.notifyHouseCreated(houseId, houseTitle, house.getSalesId());

        log.info("新增房源：{} - {}", houseNo, houseDTO.getLayout());
        return true;
    }

    /**
     * 保存扩展信息（新房或二手房）
     */
    private void saveExtendInfo(HouseDTO houseDTO, Integer houseId) {
        Integer houseType = houseDTO.getHouseType();

        if (houseType == 2 && houseDTO.getNewHouseInfo() != null) {
            // 保存新房扩展信息
            NewHouseInfo newHouseInfo = new NewHouseInfo();
            HouseDTO.NewHouseInfoDTO dto = houseDTO.getNewHouseInfo();

            newHouseInfo.setHouseId(houseId);
            newHouseInfo.setPreSaleLicenseNo(dto.getPreSaleLicenseNo());
            newHouseInfo.setRecordPrice(dto.getRecordPrice());
            newHouseInfo.setPropertyRightYears(dto.getPropertyRightYears());
            newHouseInfo.setDeliveryStandard(dto.getDeliveryStandard());
            newHouseInfo.setElevatorRatio(dto.getElevatorRatio());
            newHouseInfo.setActualAreaRate(dto.getActualAreaRate());

            // 处理预计交房时间
            if (dto.getEstimatedDeliveryTime() != null && !dto.getEstimatedDeliveryTime().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    newHouseInfo.setEstimatedDeliveryTime(sdf.parse(dto.getEstimatedDeliveryTime()));
                } catch (Exception e) {
                    log.warn("预计交房时间格式错误: {}", dto.getEstimatedDeliveryTime());
                }
            }

            int insertResult = newHouseInfoMapper.insert(newHouseInfo);
            if (insertResult <= 0) {
                throw new ServiceException("保存新房扩展信息失败");
            }
            log.info("保存新房扩展信息成功，houseId={}", houseId);

        } else if (houseType == 1 && houseDTO.getSecondHouseInfo() != null) {
            // 保存二手房扩展信息
            SecondHouseInfo secondHouseInfo = new SecondHouseInfo();
            HouseDTO.SecondHouseInfoDTO dto = houseDTO.getSecondHouseInfo();

            secondHouseInfo.setHouseId(houseId);

            // 处理建筑年代
            if (dto.getBuildYear() != null && !dto.getBuildYear().isEmpty()) {
                try {
                    secondHouseInfo.setBuildYear(Integer.parseInt(dto.getBuildYear()));
                } catch (NumberFormatException e) {
                    log.warn("建筑年代格式错误: {}", dto.getBuildYear());
                }
            }

            // 处理装修时间
            if (dto.getDecorationTime() != null && !dto.getDecorationTime().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date decorationDate = sdf.parse(dto.getDecorationTime());
                    secondHouseInfo.setDecorationTime(decorationDate.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime());
                } catch (Exception e) {
                    log.warn("装修时间格式错误: {}", dto.getDecorationTime());
                }
            }

            secondHouseInfo.setIsOverTwo(dto.getIsOverTwo());
            secondHouseInfo.setIsOverFive(dto.getIsOverFive());
            secondHouseInfo.setIsOnlyHouse(dto.getIsOnlyHouse());
            secondHouseInfo.setMortgageStatus(dto.getMortgageStatus());
            secondHouseInfo.setHouseUsage(dto.getHouseUsage());
            secondHouseInfo.setDescription(dto.getDescription());

            int insertResult = secondHouseInfoMapper.insert(secondHouseInfo);
            if (insertResult <= 0) {
                throw new ServiceException("保存二手房扩展信息失败");
            }
            log.info("保存二手房扩展信息成功，houseId={}", houseId);
        }
    }

    /**
     * 更新扩展信息（新房或二手房）
     */
    private void updateExtendInfo(HouseDTO houseDTO, Integer houseId) {
        Integer houseType = houseDTO.getHouseType();

        if (houseType == 2 && houseDTO.getNewHouseInfo() != null) {
            // 更新新房扩展信息
            HouseDTO.NewHouseInfoDTO dto = houseDTO.getNewHouseInfo();

            // 检查是否已存在记录
            NewHouseInfo existingInfo = newHouseInfoMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<NewHouseInfo>()
                            .eq(NewHouseInfo::getHouseId, houseId));

            if (existingInfo != null) {
                // 更新现有记录
                existingInfo.setPreSaleLicenseNo(dto.getPreSaleLicenseNo());
                existingInfo.setRecordPrice(dto.getRecordPrice());
                existingInfo.setPropertyRightYears(dto.getPropertyRightYears());
                existingInfo.setDeliveryStandard(dto.getDeliveryStandard());
                existingInfo.setElevatorRatio(dto.getElevatorRatio());
                existingInfo.setActualAreaRate(dto.getActualAreaRate());

                // 处理预计交房时间
                if (dto.getEstimatedDeliveryTime() != null && !dto.getEstimatedDeliveryTime().isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        existingInfo.setEstimatedDeliveryTime(sdf.parse(dto.getEstimatedDeliveryTime()));
                    } catch (Exception e) {
                        log.warn("预计交房时间格式错误: {}", dto.getEstimatedDeliveryTime());
                    }
                }

                newHouseInfoMapper.updateById(existingInfo);
                log.info("更新新房扩展信息成功，houseId={}", houseId);
            } else {
                // 不存在则新增
                saveExtendInfo(houseDTO, houseId);
            }

        } else if (houseType == 1 && houseDTO.getSecondHouseInfo() != null) {
            // 更新二手房扩展信息
            HouseDTO.SecondHouseInfoDTO dto = houseDTO.getSecondHouseInfo();

            // 检查是否已存在记录
            SecondHouseInfo existingInfo = secondHouseInfoMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SecondHouseInfo>()
                            .eq(SecondHouseInfo::getHouseId, houseId));

            if (existingInfo != null) {
                // 更新现有记录
                // 处理建筑年代
                if (dto.getBuildYear() != null && !dto.getBuildYear().isEmpty()) {
                    try {
                        existingInfo.setBuildYear(Integer.parseInt(dto.getBuildYear()));
                    } catch (NumberFormatException e) {
                        log.warn("建筑年代格式错误: {}", dto.getBuildYear());
                    }
                }

                // 处理装修时间
                if (dto.getDecorationTime() != null && !dto.getDecorationTime().isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date decorationDate = sdf.parse(dto.getDecorationTime());
                        existingInfo.setDecorationTime(decorationDate.toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime());
                    } catch (Exception e) {
                        log.warn("装修时间格式错误: {}", dto.getDecorationTime());
                    }
                }

                existingInfo.setIsOverTwo(dto.getIsOverTwo());
                existingInfo.setIsOverFive(dto.getIsOverFive());
                existingInfo.setIsOnlyHouse(dto.getIsOnlyHouse());
                existingInfo.setMortgageStatus(dto.getMortgageStatus());
                existingInfo.setHouseUsage(dto.getHouseUsage());
                existingInfo.setDescription(dto.getDescription());

                secondHouseInfoMapper.updateById(existingInfo);
                log.info("更新二手房扩展信息成功，houseId={}", houseId);
            } else {
                // 不存在则新增
                saveExtendInfo(houseDTO, houseId);
            }
        }
    }

    /**
     * 保存图片信息
     */
    private void saveImages(HouseDTO houseDTO, Integer houseId, String houseNo) {
        List<String> images = houseDTO.getImages();
        if (images == null || images.isEmpty()) {
            return;
        }

        String baseUploadPath = FileUploadUtils.getUploadBasePath();
        Integer uploadUserId = SecurityUtils.requireUserId();
        Integer roleType = SecurityUtils.getRoleType();
        Date now = new Date();

        // 创建以房源编号命名的文件夹
        String houseDirPath = baseUploadPath + File.separator + houseNo;
        File houseDir = new File(houseDirPath);
        if (!houseDir.exists()) {
            boolean created = houseDir.mkdirs();
            if (!created) {
                log.warn("创建房源图片目录失败: {}", houseDirPath);
            }
        }

        // 保存图片信息到数据库
        for (int i = 0; i < images.size(); i++) {
            String imageUrl = images.get(i);
            if (imageUrl == null || imageUrl.isEmpty()) {
                continue;
            }

            String normalized = imageUrl.replace("\\", "/");
            while (normalized.startsWith("/")) {
                normalized = normalized.substring(1);
            }

            // 兼容前端/历史数据可能带的前缀
            if (normalized.startsWith("uploads/")) {
                normalized = normalized.substring("uploads/".length());
            }

            // 如果图片还在临时目录中，移动到以 houseNo 命名的目录，避免 temp 目录堆积
            try {
                if (normalized.startsWith("temp_") && normalized.contains("/")) {
                    String fileName = normalized.substring(normalized.lastIndexOf('/') + 1);
                    Path source = Paths.get(baseUploadPath, normalized).toAbsolutePath();
                    Path targetDir = Paths.get(baseUploadPath, houseNo).toAbsolutePath();
                    if (!Files.exists(targetDir)) {
                        Files.createDirectories(targetDir);
                    }
                    Path target = targetDir.resolve(fileName);
                    if (Files.exists(source)) {
                        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                        normalized = houseNo + "/" + fileName;
                    } else if (Files.exists(target)) {
                        // 可能已经移动成功，但数据库/前端仍传了 temp 路径
                        normalized = houseNo + "/" + fileName;
                    } else {
                        log.warn("临时图片不存在，跳过落库: url={}, houseNo={}", imageUrl, houseNo);
                        continue;
                    }
                }
            } catch (Exception e) {
                log.warn("移动房源图片失败: url={}, houseNo={}, error={}", imageUrl, houseNo, e.getMessage());
                // 搬运失败时，禁止将 temp 路径写入数据库
                if (normalized.startsWith("temp_")) {
                    continue;
                }
            }

            HouseImage houseImage = new HouseImage();
            houseImage.setHouseId(houseId);
            houseImage.setImageUrl(normalized);
            // 第一张图为封面图，其他为室内图
            int imageType = (i == 0 ? 1 : 2);
            String lower = normalized.toLowerCase();
            if (imageType != 1) {
                if (lower.contains("layout") || lower.contains("户型") || lower.contains("huxing")) {
                    imageType = 3;
                } else if (lower.contains("view") || lower.contains("river") || lower.contains("mountain")
                        || lower.contains("university") || lower.contains("park")) {
                    imageType = 4;
                }
            }
            houseImage.setImageType(imageType);
            houseImage.setSortOrder(i + 1);
            houseImage.setUploadUserId(uploadUserId);

            // 管理员/销售经理上传的图片默认视为已审核通过，其余角色默认待审核
            if (roleType != null && (roleType == 1 || roleType == 3)) {
                houseImage.setAuditStatus(1);
                houseImage.setAuditUserId(uploadUserId);
                houseImage.setAuditTime(now);
            } else {
                houseImage.setAuditStatus(0);
            }

            houseImage.setCreateTime(now);
            houseImage.setUpdateTime(now);

            int insertResult = houseImageMapper.insert(houseImage);
            if (insertResult <= 0) {
                log.warn("保存图片信息失败: {}", imageUrl);
            }
        }

        log.info("保存房源图片成功，houseId={}, 图片数量={}", houseId, images.size());
    }

    /**
     * 更新图片信息
     */
    private void updateImages(HouseDTO houseDTO, Integer houseId, String houseNo) {
        List<String> newImages = houseDTO.getImages();
        if (newImages == null) {
            return;
        }

        // 删除旧图片记录
        houseImageMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HouseImage>()
                        .eq(HouseImage::getHouseId, houseId));
        log.info("删除房源旧图片记录，houseId={}", houseId);

        // 保存新图片
        if (!newImages.isEmpty()) {
            saveImages(houseDTO, houseId, houseNo);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHouse(HouseDTO houseDTO) {
        if (houseDTO.getId() == null || houseDTO.getId() <= 0) {
            throw new ServiceException("房源ID不能为空");
        }

        Integer currentUserId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();
        if (currentUserId == null || roleType == null) {
            throw new ServiceException("未登录或登录已过期，请重新登录");
        }
        if (roleType == 4) {
            throw new ServiceException("无权限修改房源");
        }

        // 检查房源是否存在
        House existHouse = houseMapper.selectById(houseDTO.getId());
        if (existHouse == null) {
            throw new ServiceException("房源不存在");
        }

        // ========== 权限控制：销售顾问仅允许修改自己负责的房源，且不允许改负责人/状态 ==========
        if (roleType != null && roleType == 2) {
            if (currentUserId == null || existHouse.getSalesId() == null
                    || !currentUserId.equals(existHouse.getSalesId())) {
                throw new ServiceException("无权限操作该房源");
            }
            // 强制禁止销售顾问修改 salesId / status（防止越权）
            houseDTO.setSalesId(existHouse.getSalesId());
            houseDTO.setStatus(existHouse.getStatus());
        }

        // 数据验证
        validateHouseData(houseDTO, true);

        // 转换为实体类
        House house = new House();
        BeanUtils.copyProperties(houseDTO, house);

        // 保持房源编号不变
        house.setHouseNo(existHouse.getHouseNo());

        // 更新基本信息
        int result = houseMapper.updateById(house);

        // 更新扩展信息（如果有变更）
        updateExtendInfo(houseDTO, houseDTO.getId());

        // 更新图片信息（如果 images 字段存在：支持清空图片）
        if (houseDTO.getImages() != null) {
            updateImages(houseDTO, houseDTO.getId(), existHouse.getHouseNo());
        }

        log.info("更新房源：{} - {}", existHouse.getHouseNo(), houseDTO.getLayout());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHouse(Integer id) {
        if (id == null || id <= 0) {
            throw new ServiceException("房源ID不能为空");
        }

        // 销售顾问不允许删除
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            throw new ServiceException("无权限删除房源");
        }
        if (roleType != null && roleType == 4) {
            throw new ServiceException("无权限删除房源");
        }

        // 检查房源是否存在
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new ServiceException("房源不存在");
        }

        // 检查房源状态（在售/已预订/已成交的房源不能删除）
        if (house.getStatus() != null && (house.getStatus() == 1 || house.getStatus() == 2 || house.getStatus() == 3)) {
            throw new ServiceException("在售、已预订或已成交的房源不能删除");
        }

        // 检查是否有关联的交易记录（未取消/未结束）
        Long activeTxCnt = transactionMapper.selectCount(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getHouseId, id)
                .ne(Transaction::getStatus, 5));
        if (activeTxCnt != null && activeTxCnt > 0) {
            throw new ServiceException("该房源存在进行中的交易，禁止删除");
        }

        // 删除
        int result = houseMapper.deleteById(id);

        log.info("删除房源：{}", house.getHouseNo());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteHouse(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("房源ID列表不能为空");
        }

        int count = 0;
        for (Integer id : ids) {
            try {
                if (deleteHouse(id)) {
                    count++;
                }
            } catch (ServiceException e) {
                log.warn("删除房源失败：{} - {}", id, e.getMessage());
            }
        }

        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Integer id, Integer status) {
        if (id == null || id <= 0) {
            throw new ServiceException("房源ID不能为空");
        }
        if (status == null || status < 0 || status > 4) {
            throw new ServiceException("状态值无效");
        }

        // 销售顾问不允许直接改状态（状态流转由审核/交易等产生）
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            throw new ServiceException("无权限修改房源状态");
        }
        if (roleType != null && roleType == 4) {
            throw new ServiceException("无权限修改房源状态");
        }

        // 检查房源是否存在
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new ServiceException("房源不存在");
        }

        // 更新状态
        house.setStatus(status);
        int result = houseMapper.updateById(house);

        log.info("更新房源状态：{} - {}", house.getHouseNo(), getStatusText(status));
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateStatus(List<Integer> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("房源ID列表不能为空");
        }
        if (status == null || status < 0 || status > 4) {
            throw new ServiceException("状态值无效");
        }

        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            throw new ServiceException("无权限修改房源状态");
        }
        if (roleType != null && roleType == 4) {
            throw new ServiceException("无权限修改房源状态");
        }

        int result = houseMapper.batchUpdateStatus(ids, status);

        log.info("批量更新房源状态：{} 条，状态={}", result, getStatusText(status));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignSales(Integer id, Integer salesId) {
        if (id == null || id <= 0) {
            throw new ServiceException("房源ID不能为空");
        }
        if (salesId == null || salesId <= 0) {
            throw new ServiceException("销售ID不能为空");
        }

        // 销售顾问不允许分配负责人
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            throw new ServiceException("无权限分配房源");
        }
        if (roleType != null && roleType == 4) {
            throw new ServiceException("无权限分配房源");
        }

        // 检查房源是否存在
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new ServiceException("房源不存在");
        }

        // TODO: 检查销售人员是否存在

        // 分配
        house.setSalesId(salesId);
        int result = houseMapper.updateById(house);

        log.info("分配房源：{} -> 销售ID={}", house.getHouseNo(), salesId);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAssignSales(List<Integer> ids, Integer salesId) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("房源ID列表不能为空");
        }
        if (salesId == null || salesId <= 0) {
            throw new ServiceException("销售ID不能为空");
        }

        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            throw new ServiceException("无权限分配房源");
        }
        if (roleType != null && roleType == 4) {
            throw new ServiceException("无权限分配房源");
        }

        // TODO: 检查销售人员是否存在

        int result = houseMapper.batchAssignSales(ids, salesId);

        log.info("批量分配房源：{} 条 -> 销售ID={}", result, salesId);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditHouse(Integer id, Boolean approved, String reason) {
        if (id == null || id <= 0) {
            throw new ServiceException("房源ID不能为空");
        }
        if (approved == null) {
            throw new ServiceException("审核结果不能为空");
        }

        // 销售顾问不允许审核
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType != null && roleType == 2) {
            throw new ServiceException("无权限审核房源");
        }
        if (roleType != null && roleType == 4) {
            throw new ServiceException("无权限审核房源");
        }

        // 检查房源是否存在
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new ServiceException("房源不存在");
        }

        // 检查当前状态是否为待审核或已驳回
        if (house.getStatus() == null || (house.getStatus() != 0 && house.getStatus() != 5)) {
            throw new ServiceException("只能审核待审核或已驳回状态的房源");
        }

        // 更新状态
        if (approved) {
            house.setStatus(1); // 审核通过 -> 在售
        } else {
            house.setStatus(5); // 审核拒绝 -> 已驳回（区别于主动下架=4）
        }
        int result = houseMapper.updateById(house);

        if (result > 0) {
            Date now = new Date();
            Integer auditUserId = SecurityUtils.requireUserId();
            Integer imageAuditStatus = approved ? 1 : 2;

            houseImageMapper.update(null,
                    new LambdaUpdateWrapper<HouseImage>()
                            .eq(HouseImage::getHouseId, id)
                            .set(HouseImage::getAuditStatus, imageAuditStatus)
                            .set(HouseImage::getAuditUserId, auditUserId)
                            .set(HouseImage::getAuditTime, now)
                            .set(HouseImage::getUpdateTime, now));
        }

        // 发送审核结果通知给销售
        String houseTitle = String.format("%s (%s)", house.getHouseNo(), house.getLayout());
        notificationService.notifyHouseAudited(
                house.getId(),
                houseTitle,
                house.getSalesId(),
                approved,
                reason);
        log.info("审核房源：{} - {} - 原因：{}", house.getHouseNo(),
                approved ? "通过" : "拒绝", reason);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resubmitAudit(Integer id) {
        if (id == null || id <= 0) {
            throw new ServiceException("房源ID不能为空");
        }

        Integer currentUserId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();
        if (currentUserId == null) {
            throw new ServiceException("未登录或登录已过期");
        }

        // 检查房源是否存在
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new ServiceException("房源不存在");
        }

        // 校验权限：销售顾问只能操作自己负责的房源
        if (roleType != null && roleType == 2) {
            if (!currentUserId.equals(house.getSalesId())) {
                throw new ServiceException("无权限操作该房源");
            }
        }

        // 只有已驳回状态的房源才能重新提交审核
        if (house.getStatus() == null || house.getStatus() != 5) {
            throw new ServiceException("只有已驳回状态的房源才能重新提交审核");
        }

        // 重置状态为待审核
        house.setStatus(0);
        int result = houseMapper.updateById(house);

        if (result > 0) {
            // 重置图片审核状态为待审核
            houseImageMapper.update(null,
                    new LambdaUpdateWrapper<HouseImage>()
                            .eq(HouseImage::getHouseId, id)
                            .set(HouseImage::getAuditStatus, 0)
                            .set(HouseImage::getUpdateTime, new Date()));

            // 发送通知给管理员
            String houseTitle = String.format("%s (%s)", house.getHouseNo(), house.getLayout());
            notificationService.notifyHouseCreated(house.getId(), houseTitle, house.getSalesId());
        }

        log.info("重新提交审核房源：{}", house.getHouseNo());
        return result > 0;
    }

    /**
     * 验证房源数据
     *
     * @param houseDTO 房源数据
     * @param isUpdate 是否为更新操作
     */
    private void validateHouseData(HouseDTO houseDTO, boolean isUpdate) {
        // 楼层验证
        if (houseDTO.getFloor() != null && houseDTO.getTotalFloor() != null) {
            if (houseDTO.getFloor() > houseDTO.getTotalFloor()) {
                throw new ServiceException("所在楼层不能大于总楼层");
            }
        }

        // 房源类型验证
        if (houseDTO.getHouseType() != null) {
            if (houseDTO.getHouseType() < 1 || houseDTO.getHouseType() > 3) {
                throw new ServiceException("房源类型无效");
            }
        }

        // 状态验证
        if (houseDTO.getStatus() != null) {
            if (houseDTO.getStatus() < 0 || houseDTO.getStatus() > 5) {
                throw new ServiceException("状态值无效");
            }
        }
    }

    /**
     * 设置文本字段
     *
     * @param houseVO 房源VO
     */
    private void setTextFields(HouseVO houseVO) {
        // 设置状态文本
        houseVO.setStatusText(getStatusText(houseVO.getStatus()));

        // 设置类型文本
        houseVO.setHouseTypeText(getHouseTypeText(houseVO.getHouseType()));
    }

    /**
     * 获取状态文本
     *
     * @param status 状态码
     * @return 状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待审核";
            case 1:
                return "在售";
            case 2:
                return "已预订";
            case 3:
                return "已成交";
            case 4:
                return "下架";
            case 5:
                return "已驳回";
            default:
                return "未知";
        }
    }

    /**
     * 获取房源类型文本
     *
     * @param houseType 类型码
     * @return 类型文本
     */
    private String getHouseTypeText(Integer houseType) {
        if (houseType == null) {
            return "未知";
        }
        switch (houseType) {
            case 1:
                return "二手房";
            case 2:
                return "新房";
            case 3:
                return "租房";
            default:
                return "未知";
        }
    }

    /**
     * 获取新房销售状态文本
     *
     * @param saleStatus 状态码
     * @return 状态文本
     */
    private String getNewHouseSaleStatusText(Integer saleStatus) {
        if (saleStatus == null) {
            return "未知";
        }
        switch (saleStatus) {
            case 0:
                return "待售";
            case 1:
                return "在售";
            case 2:
                return "售罄";
            default:
                return "未知";
        }
    }

    /**
     * 获取抵押状态文本
     *
     * @param mortgageStatus 状态码
     * @return 状态文本
     */
    private String getMortgageStatusText(Integer mortgageStatus) {
        if (mortgageStatus == null) {
            return "未知";
        }
        switch (mortgageStatus) {
            case 0:
                return "无抵押";
            case 1:
                return "已抵押";
            default:
                return "未知";
        }
    }

    /**
     * 获取房本状态文本
     *
     * @param certificateStatus 状态码
     * @return 状态文本
     */
    private String getCertificateStatusText(Integer certificateStatus) {
        if (certificateStatus == null) {
            return "未知";
        }
        switch (certificateStatus) {
            case 0:
                return "无房本";
            case 1:
                return "有房本";
            default:
                return "未知";
        }
    }
}
