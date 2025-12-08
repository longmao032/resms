package com.guang.resms.controller;

import com.guang.resms.entity.SecondHouseCommunity;
import com.guang.resms.entity.dto.CommunityQueryDTO;
import com.guang.resms.service.CommunityService;
import com.guang.resms.utils.exception.HttpEnums;
import com.guang.resms.utils.exception.ResponseResult;
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
        SecondHouseCommunity updatedCommunity = communityService.updateCommunity(community);
        return ResponseResult.success(updatedCommunity);
    }

    /**
     * 删除小区
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteCommunity(@PathVariable Integer id) {
        boolean success = communityService.removeById(id);
        if (success) {
            return ResponseResult.success(HttpEnums.SUCCESS);
        }
        return ResponseResult.fail("删除失败");
    }
}