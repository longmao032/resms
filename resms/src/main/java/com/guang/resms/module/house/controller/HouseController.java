package com.guang.resms.module.house.controller;

import com.guang.resms.module.house.entity.dto.HouseDTO;
import com.guang.resms.module.house.entity.dto.HouseQueryDTO;
import com.guang.resms.module.house.entity.vo.HouseVO;
import com.guang.resms.module.house.service.HouseService;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 房源管理控制器
 *
 * @author guang
 */
@Slf4j
@RestController
@RequestMapping("/house")
public class HouseController {

    private final HouseService houseService;

    @Value("${file.upload.path:uploads/house}")
    private String uploadPath;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    /**
     * 分页查询房源列表
     */
    @GetMapping("/list")
    public ResponseResult<Map<String, Object>> list(HouseQueryDTO queryDTO) {
        try {
            Map<String, Object> result = houseService.pageList(queryDTO);
            return ResponseResult.success("查询成功", result);
        } catch (Exception e) {
            log.error("查询房源列表失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取房源详情
     */
    @GetMapping("/{id}")
    public ResponseResult<HouseVO> getById(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "房源ID不能为空");
            }

            HouseVO houseVO = houseService.getHouseById(id);
            return ResponseResult.success("查询成功", houseVO);

        } catch (Exception e) {
            log.error("获取房源详情失败：id={}", id, e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "获取详情失败：" + e.getMessage());
        }
    }

    /**
     * 新增房源
     */
    @PostMapping("/add")
    public ResponseResult<Void> add(@Valid @RequestBody HouseDTO houseDTO) {
        try {
            boolean success = houseService.addHouse(houseDTO);
            if (success) {
                return ResponseResult.success("新增成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "新增失败");
            }

        } catch (Exception e) {
            log.error("新增房源失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "新增失败：" + e.getMessage());
        }
    }

    /**
     * 更新房源
     */
    @PutMapping("/update")
    public ResponseResult<Void> update(@Valid @RequestBody HouseDTO houseDTO) {
        try {
            boolean success = houseService.updateHouse(houseDTO);
            if (success) {
                return ResponseResult.success("更新成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "更新失败");
            }

        } catch (Exception e) {
            log.error("更新房源失败：id={}", houseDTO.getId(), e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除房源
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "房源ID不能为空");
            }

            boolean success = houseService.deleteHouse(id);
            if (success) {
                return ResponseResult.success("删除成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "删除失败");
            }

        } catch (Exception e) {
            log.error("删除房源失败：id={}", id, e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除房源
     */
    @DeleteMapping("/batch")
    public ResponseResult<Integer> batchDelete(@RequestBody List<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "房源ID列表不能为空");
            }

            int count = houseService.batchDeleteHouse(ids);
            return ResponseResult.success("成功删除 " + count + " 条记录", count);

        } catch (Exception e) {
            log.error("批量删除房源失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 更新房源状态
     */
    @PutMapping("/status/{id}/{status}")
    public ResponseResult<Void> updateStatus(@PathVariable Integer id, 
                                            @PathVariable Integer status) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "房源ID不能为空");
            }
            if (status == null || status < 0 || status > 4) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "状态值无效");
            }

            boolean success = houseService.updateStatus(id, status);
            if (success) {
                return ResponseResult.success("状态更新成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "状态更新失败");
            }

        } catch (Exception e) {
            log.error("更新房源状态失败：id={}, status={}", id, status, e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 批量更新房源状态
     */
    @PutMapping("/status/batch")
    public ResponseResult<Integer> batchUpdateStatus(@RequestParam List<Integer> ids, 
                                                     @RequestParam Integer status) {
        try {
            if (ids == null || ids.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "房源ID列表不能为空");
            }
            if (status == null || status < 0 || status > 4) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "状态值无效");
            }

            int count = houseService.batchUpdateStatus(ids, status);
            return ResponseResult.success("成功更新 " + count + " 条记录", count);

        } catch (Exception e) {
            log.error("批量更新房源状态失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "批量更新失败：" + e.getMessage());
        }
    }

    /**
     * 分配房源给销售
     */
    @PutMapping("/assign/{id}/{salesId}")
    public ResponseResult<Void> assignSales(@PathVariable Integer id, 
                                           @PathVariable Integer salesId) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "房源ID不能为空");
            }
            if (salesId == null || salesId <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "销售ID不能为空");
            }

            boolean success = houseService.assignSales(id, salesId);
            if (success) {
                return ResponseResult.success("分配成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "分配失败");
            }

        } catch (Exception e) {
            log.error("分配房源失败：id={}, salesId={}", id, salesId, e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "分配失败：" + e.getMessage());
        }
    }

    /**
     * 批量分配房源给销售
     */
    @PutMapping("/assign/batch")
    public ResponseResult<Integer> batchAssignSales(@RequestParam List<Integer> ids, 
                                                    @RequestParam Integer salesId) {
        try {
            if (ids == null || ids.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "房源ID列表不能为空");
            }
            if (salesId == null || salesId <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "销售ID不能为空");
            }

            int count = houseService.batchAssignSales(ids, salesId);
            return ResponseResult.success("成功分配 " + count + " 条记录", count);

        } catch (Exception e) {
            log.error("批量分配房源失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "批量分配失败：" + e.getMessage());
        }
    }

    /**
     * 审核房源
     */
    @PutMapping("/audit/{id}")
    public ResponseResult<Void> audit(@PathVariable Integer id, 
                                     @RequestParam Boolean approved, 
                                     @RequestParam(required = false) String reason) {
        try {
            if (id == null || id <= 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "房源ID不能为空");
            }
            if (approved == null) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "审核结果不能为空");
            }

            boolean success = houseService.auditHouse(id, approved, reason);
            if (success) {
                return ResponseResult.success("审核成功");
            } else {
                return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "审核失败");
            }

        } catch (Exception e) {
            log.error("审核房源失败：id={}, approved={}", id, approved, e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "审核失败：" + e.getMessage());
        }
    }

    /**
     * 上传房源图片
     */
    @PostMapping("/upload")
    public ResponseResult<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            Integer roleType = SecurityUtils.getRoleType();
            if (roleType == null) {
                throw new ServiceException(HttpEnums.UNAUTHORIZED.getCode(), "未登录");
            }
            if (roleType == 4) {
                throw new ServiceException("无权限上传房源图片");
            }

            if (files == null || files.length == 0) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "请选择要上传的图片");
            }

            // 验证图片数量（最多10张）
            if (files.length > 10) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "最多只能上传10张图片");
            }

            List<String> imageUrls = new ArrayList<>();
            
            // 创建临时目录（以时间戳命名）
            String tempDir = "temp_" + System.currentTimeMillis();
            String tempDirPath = uploadPath + File.separator + tempDir;
            File dir = new File(tempDirPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), "创建上传目录失败");
                }
            }

            // 保存图片
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                // 验证文件类型
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    continue;
                }

                // 验证文件大小（最大5MB）
                if (file.getSize() > 5 * 1024 * 1024) {
                    return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "图片大小不能超过5MB");
                }

                // 生成唯一文件名
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String filename = UUID.randomUUID().toString() + extension;

                // 保存文件
                Path filePath = Paths.get(tempDirPath, filename);
                Files.copy(file.getInputStream(), filePath);

                // 返回相对路径
                String relativePath = tempDir + "/" + filename;
                imageUrls.add(relativePath);
            }

            if (imageUrls.isEmpty()) {
                return ResponseResult.fail(HttpEnums.BAD_REQUEST.getCode(), "未上传有效图片");
            }

            return ResponseResult.success("上传成功", imageUrls);

        } catch (IOException e) {
            log.error("上传图片失败", e);
            return ResponseResult.fail(HttpEnums.INTERNAL_SERVER_ERROR.getCode(), 
                                      "上传失败：" + e.getMessage());
        } catch (ServiceException e) {
            return ResponseResult.fail(e.getCode() != null ? e.getCode() : HttpEnums.BAD_REQUEST.getCode(), e.getMessage());
        }
    }
}
