package com.guang.resms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.entity.Project;
import com.guang.resms.entity.dto.ProjectQueryDTO;
import com.guang.resms.entity.dto.QueryDTO;
import com.guang.resms.entity.vo.ProjectHouseStatVO;
import com.guang.resms.mapper.ProjectMapper;
import com.guang.resms.service.ProjectService;
import com.guang.resms.utils.PinyinUtils;
import com.guang.resms.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public HashMap<String, ArrayList<String>> getAllCity() {
        // 1. 创建查询条件，只查询城市字段并去重
        QueryWrapper<Project> queryWrapper = new QueryWrapper<Project>()
                .select("city")
                .groupBy("city");

        // 2. 查询所有不重复的城市
        List<String> cityList = projectMapper.selectList(queryWrapper).stream()
                .map(Project::getCity)
                .filter(city -> city != null && !city.trim().isEmpty())
                .toList();

        // 3. 使用PinyinUtils工具类按拼音首字母分组
        var groupedMap = PinyinUtils.groupByInitial(cityList);

        // 4. 转换为要求的返回类型
        HashMap<String, ArrayList<String>> resultMap = new HashMap<>();

        // 遍历分组后的Map，将List转换为ArrayList
        groupedMap.forEach((key, valueList) -> {
            resultMap.put(key, new ArrayList<>(valueList));
        });

        return resultMap;
    }

    @Override
    public ArrayList<String> getDistinctDistrictsByCity(String city) {
        // 1. 创建查询条件，只查询区县字段并去重
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<Project>()
                .select(Project::getDistrict)
                .groupBy(Project::getDistrict)
                .eq(Project::getCity, city);

        // 2. 查询所有不重复的区县
        List<String> districtList = projectMapper.selectList(queryWrapper).stream()
                .map(Project::getDistrict)
                .filter(district -> district != null && !district.trim().isEmpty())
                .toList();

        // 3. 转换为要求的返回类型
        return new ArrayList<>(districtList);
    }

    @Override
    public IPage<ProjectHouseStatVO> getProjectHouseStatPage(QueryDTO queryDTO) {
        Page<ProjectHouseStatVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return projectMapper.selectProjectHouseStatPage(page, queryDTO);
    }

    @Override
    public List<Project> getAllProjects() {
        // 查询所有项目，只返回 id 和 projectName 字段
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<Project>()
                .select(Project::getId, Project::getProjectName)
                .orderBy(true, true, Project::getId);
        return projectMapper.selectList(queryWrapper);
    }

    @Override
    public Map<String, Object> getProjectPage(ProjectQueryDTO queryDTO) {
        // 参数验证
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
            queryDTO.setPageSize(10);
        }

        // 构建查询条件
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();

        // 项目名称模糊查询
        if (StringUtils.isNotBlank(queryDTO.getProjectName())) {
            queryWrapper.like(Project::getProjectName, queryDTO.getProjectName());
        }

        // 城市
        if (StringUtils.isNotBlank(queryDTO.getCity())) {
            queryWrapper.eq(Project::getCity, queryDTO.getCity());
        }

        // 区县
        if (StringUtils.isNotBlank(queryDTO.getDistrict())) {
            queryWrapper.eq(Project::getDistrict, queryDTO.getDistrict());
        }

        // 开发商
        if (StringUtils.isNotBlank(queryDTO.getDeveloper())) {
            queryWrapper.like(Project::getDeveloper, queryDTO.getDeveloper());
        }

        // 状态
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Project::getStatus, queryDTO.getStatus());
        }

        // 价格范围
        if (queryDTO.getMinPrice() != null) {
            queryWrapper.ge(Project::getPriceAvg, queryDTO.getMinPrice());
        }
        if (queryDTO.getMaxPrice() != null) {
            queryWrapper.le(Project::getPriceAvg, queryDTO.getMaxPrice());
        }

        // 物业类型
        if (StringUtils.isNotBlank(queryDTO.getPropertyType())) {
            queryWrapper.eq(Project::getPropertyType, queryDTO.getPropertyType());
        }

        // 排序
        if (StringUtils.isNotBlank(queryDTO.getSortField())) {
            boolean isAsc = "ASC".equalsIgnoreCase(queryDTO.getSortOrder());
            switch (queryDTO.getSortField()) {
                case "priceAvg":
                    queryWrapper.orderBy(true, isAsc, Project::getPriceAvg);
                    break;
                case "openingTime":
                    queryWrapper.orderBy(true, isAsc, Project::getOpeningTime);
                    break;
                case "createTime":
                    queryWrapper.orderBy(true, isAsc, Project::getCreateTime);
                    break;
                default:
                    queryWrapper.orderByDesc(Project::getCreateTime);
            }
        } else {
            // 默认按创建时间降序
            queryWrapper.orderByDesc(Project::getCreateTime);
        }

        // 分页查询
        Page<Project> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<Project> result = projectMapper.selectPage(page, queryWrapper);

        // 构建返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", result.getTotal());
        resultMap.put("list", result.getRecords());
        resultMap.put("pageNum", result.getCurrent());
        resultMap.put("pageSize", result.getSize());
        resultMap.put("pages", result.getPages());

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Project addProject(Project project, MultipartFile coverImage) {
        // 自动生成项目编号
        String projectNo = generateProjectNo();
        project.setProjectNo(projectNo);
        
        // 处理封面图片上传
        if (coverImage != null && !coverImage.isEmpty()) {
            // 验证图片文件
            if (!FileUploadUtils.isValidImage(coverImage)) {
                throw new RuntimeException("图片格式不正确或文件过大，最大允许" + FileUploadUtils.getMaxFileSizeFormatted());
            }
            
            try {
                // 使用项目编号作为文件名
                String coverUrl = FileUploadUtils.saveFile(coverImage, "project", projectNo);
                project.setCoverUrl(coverUrl);
            } catch (Exception e) {
                throw new RuntimeException("图片上传失败：" + e.getMessage(), e);
            }
        }
        
        // 保存项目
        projectMapper.insert(project);
        return project;
    }
    
    /**
     * 生成唯一的项目编号
     * 格式：XM + 年月日 + 4位序列号
     * 例如：XM202412070001
     * @return 项目编号
     */
    private String generateProjectNo() {
        // 获取当前日期（格式：yyyyMMdd）
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "XM" + datePart;
        
        // 查询今天已有的最大编号
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.likeRight(Project::getProjectNo, prefix)
                   .orderByDesc(Project::getProjectNo)
                   .last("LIMIT 1");
        
        Project lastProject = projectMapper.selectOne(queryWrapper);
        
        int sequenceNum = 1;
        if (lastProject != null && lastProject.getProjectNo() != null) {
            // 提取最后4位序列号并加一
            String lastNo = lastProject.getProjectNo();
            if (lastNo.length() >= 4) {
                try {
                    String lastSeq = lastNo.substring(lastNo.length() - 4);
                    sequenceNum = Integer.parseInt(lastSeq) + 1;
                } catch (NumberFormatException e) {
                    // 如果解析失败，使用默认值1
                    sequenceNum = 1;
                }
            }
        }
        
        // 生成完整的项目编号（序列号填充4位）
        return String.format("%s%04d", prefix, sequenceNum);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Project updateProject(Project project, MultipartFile coverImage) {
        // 验证项目ID是否存在
        if (project.getId() == null) {
            throw new RuntimeException("项目ID不能为空");
        }
        
        Project existingProject = projectMapper.selectById(project.getId());
        if (existingProject == null) {
            throw new RuntimeException("项目不存在");
        }
        
        // 处理封面图片上传
        if (coverImage != null && !coverImage.isEmpty()) {
            // 验证图片文件
            if (!FileUploadUtils.isValidImage(coverImage)) {
                throw new RuntimeException("图片格式不正确或文件过大，最大允许" + FileUploadUtils.getMaxFileSizeFormatted());
            }
            
            try {
                // 删除旧图片（如果存在）
                if (existingProject.getCoverUrl() != null && !existingProject.getCoverUrl().isEmpty()) {
                    FileUploadUtils.deleteFile(existingProject.getCoverUrl());
                }
                
                // 上传新图片（使用项目编号作为文件名）
                String coverUrl = FileUploadUtils.saveFile(coverImage, "project", existingProject.getProjectNo());
                project.setCoverUrl(coverUrl);
            } catch (Exception e) {
                throw new RuntimeException("图片上传失败：" + e.getMessage(), e);
            }
        }
        
        // 更新项目
        projectMapper.updateById(project);
        return projectMapper.selectById(project.getId());
    }
}
