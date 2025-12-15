package com.guang.resms.module.house.controller;

import com.guang.resms.module.house.entity.SecondHouseCommunity;
import com.guang.resms.module.house.entity.dto.CommunityQueryDTO;
import com.guang.resms.module.house.service.CommunityService;
import com.guang.resms.common.exception.HttpEnums;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.exception.ServiceException;
import com.guang.resms.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 小区管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    /**
     * 分页查询小区列表
     */
    @PostMapping("/page")
    public ResponseResult<Map<String, Object>> getCommunityPage(@RequestBody CommunityQueryDTO queryDTO) {
        Map<String, Object> result = communityService.getCommunityPage(queryDTO);
        return ResponseResult.success(result);
    }

    /**
     * 新增小区
     */
    @PostMapping("/add")
    public ResponseResult<SecondHouseCommunity> addCommunity(@RequestBody SecondHouseCommunity community) {
        assertWriteAllowed();
        SecondHouseCommunity savedCommunity = communityService.addCommunity(community);
        return ResponseResult.success(savedCommunity);
    }

    /**
     * 根据ID查询小区详情
     */
    @GetMapping("/{id}")
    public ResponseResult<SecondHouseCommunity> getCommunityById(@PathVariable Integer id) {
        SecondHouseCommunity community = communityService.getById(id);
        if (community == null) {
            return ResponseResult.fail("小区不存在");
        }
        return ResponseResult.success(community);
    }

    /**
     * 更新小区
     */
    @PostMapping("/update")
    public ResponseResult<SecondHouseCommunity> updateCommunity(@RequestBody SecondHouseCommunity community) {
        assertWriteAllowed();
        SecondHouseCommunity updatedCommunity = communityService.updateCommunity(community);
        return ResponseResult.success(updatedCommunity);
    }

    /**
     * 删除小区
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteCommunity(@PathVariable Integer id) {
        assertAdminOnly();
        boolean success = communityService.removeById(id);
        if (success) {
            return ResponseResult.success(HttpEnums.SUCCESS);
        }
        return ResponseResult.fail("删除失败");
    }

    /**
     * 审核小区
     * 
     * @param id     小区ID
     * @param status 审核状态：0=通过，2=驳回
     * @param reason 审核原因（驳回时使用）
     */
    @PostMapping("/audit/{id}")
    public ResponseResult<Void> auditCommunity(
            @PathVariable Integer id,
            @RequestParam Integer status,
            @RequestParam(required = false) String reason) {
        assertAdminOnly();
        communityService.auditCommunity(id, status, reason);
        return ResponseResult.success(HttpEnums.SUCCESS);
    }

    /**
     * 上传小区封面图片
     * 
     * @param file          图片文件
     * @param communityName 小区名称（用于命名）
     * @return 图片相对路径
     */
    @PostMapping("/uploadCover")
    public ResponseResult<String> uploadCover(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "communityName", required = false, defaultValue = "community") String communityName) {
        assertWriteAllowed();
        try {
            // 验证文件
            if (!com.guang.resms.common.utils.FileUploadUtils.isValidImage(file)) {
                return ResponseResult.fail("文件无效，请上传5MB以内的图片文件（jpg/png/gif/webp）");
            }

            // 生成文件名：小区名称简化 + 时间戳 + 随机数
            String timestamp = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                    .format(java.time.LocalDateTime.now());
            String random = String.format("%04d", new java.util.Random().nextInt(10000));
            // 简化名称，只取前10个字符，替换特殊字符
            String simpleName = communityName.length() > 10 ? communityName.substring(0, 10) : communityName;
            simpleName = simpleName.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "");
            String fileName = "COMM_" + simpleName + "_" + timestamp + "_" + random;

            // 保存到 community 目录
            String relativePath = com.guang.resms.common.utils.FileUploadUtils.saveFile(file, "community", fileName);

            return ResponseResult.success("上传成功", relativePath);
        } catch (Exception e) {
            return ResponseResult.fail("上传失败: " + e.getMessage());
        }
    }

    private void assertWriteAllowed() {
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType == null || (roleType != 1 && roleType != 3)) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }
    }

    private void assertAdminOnly() {
        if (!SecurityUtils.isAdmin()) {
            throw new ServiceException(HttpEnums.FORBIDDEN.getCode(), "权限不足");
        }
    }
}