package com.guang.resms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.entity.Project;
import com.guang.resms.entity.dto.ProjectQueryDTO;
import com.guang.resms.entity.dto.QueryDTO;
import com.guang.resms.entity.vo.ProjectHouseStatVO;
import com.guang.resms.service.ProjectService;
import com.guang.resms.utils.exception.HttpEnums;
import com.guang.resms.utils.exception.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * 获取所有项目列表（用于下拉选择）
     *
     * @return 项目列表
     */
    @GetMapping("/list")
    public ResponseResult<List<Project>> getProjectList() {
        List<Project> projectList = projectService.getAllProjects();
        return ResponseResult.success(projectList);
    }


    /**
     * 获取项目不重复的城市
     *
     * @return
     */
    @GetMapping("/city")
    public ResponseResult getCity() {
        HashMap<String, ArrayList<String>> allCity = projectService.getAllCity();
        return ResponseResult.success(allCity);
    }

    /**
     * 获取城市的不重复区县
     *
     * @return
     */
    @GetMapping("/districts")
    public ResponseResult getDistrict(@RequestParam(defaultValue = "南宁") String city) {
        ArrayList<String> allDistrict = projectService.getDistinctDistrictsByCity(city);
        return ResponseResult.success(allDistrict);
    }


    /**
     * 接口：分页查询符合筛选条件的项目房源统计信息
     *
     * @param pageNum      页码（默认1）
     * @param city         城市名称（可选）
     * @param district     区县名称（可选）
     * @param keyword      关键词（项目名称或地址模糊查询）
     * @param minPrice     最低价格（可选）
     * @param maxPrice     最高价格（可选）
     * @param minArea      最小面积（可选）
     * @param maxArea      最大面积（可选）
     * @param minUnitPrice 最低单价（可选）
     * @param maxUnitPrice 最高单价（可选）
     * @param layout       户型（可选）
     * @param sortField    排序字段（可选）
     * @param sortOrder    排序顺序（可选）
     * @return 分页结果
     */
    @GetMapping("/filtered")
    public ResponseResult getProjectHouseStatList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(required = true) String city,
                                                  @RequestParam(required = false) String district,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) Integer minPrice,
                                                  @RequestParam(required = false) Integer maxPrice,
                                                  @RequestParam(required = false) BigDecimal minArea,
                                                  @RequestParam(required = false) BigDecimal maxArea,
                                                  @RequestParam(required = false) BigDecimal minUnitPrice,
                                                  @RequestParam(required = false) BigDecimal maxUnitPrice,
                                                  @RequestParam(required = false) String layout,
                                                  @RequestParam(required = false) String sortField,
                                                  @RequestParam(required = false) String sortOrder) {

        QueryDTO queryDTO = new QueryDTO();
        queryDTO.setPageNum(pageNum);
        queryDTO.setCity(city);
        queryDTO.setDistrict(district);
        queryDTO.setKeyword(keyword);
        queryDTO.setMinPrice(minPrice);
        queryDTO.setMaxPrice(maxPrice);
        queryDTO.setMinArea(minArea);
        queryDTO.setMaxArea(maxArea);
        queryDTO.setMinUnitPrice(minUnitPrice);
        queryDTO.setMaxUnitPrice(maxUnitPrice);
        queryDTO.setLayout(layout);
        queryDTO.setSortField(sortField);
        queryDTO.setSortOrder(sortOrder);
        return ResponseResult.success(projectService.getProjectHouseStatPage(queryDTO));
    }

    /**
     * 分页查询项目列表
     */
    @PostMapping("/page")
    public ResponseResult<Map<String, Object>> getProjectPage(@RequestBody ProjectQueryDTO queryDTO) {
        Map<String, Object> result = projectService.getProjectPage(queryDTO);
        return ResponseResult.success(result);
    }

    /**
     * 新增项目（支持图片上传）
     */
    @PostMapping("/add")
    public ResponseResult<Project> addProject(
            @RequestPart("project") Project project,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        Project savedProject = projectService.addProject(project, coverImage);
        return ResponseResult.success(savedProject);
    }

    /**
     * 根据ID查询项目详情
     */
    @GetMapping("/{id}")
    public ResponseResult<Project> getProjectById(@PathVariable Integer id) {
        Project project = projectService.getById(id);
        if (project == null) {
            return ResponseResult.fail("项目不存在");
        }
        return ResponseResult.success(project);
    }

    /**
     * 更新项目（支持图片上传）
     */
    @PostMapping("/update")
    public ResponseResult<Project> updateProject(
            @RequestPart("project") Project project,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        Project updatedProject = projectService.updateProject(project, coverImage);
        return ResponseResult.success(updatedProject);
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteProject(@PathVariable Integer id) {
        boolean success = projectService.removeById(id);
        if (success) {
            return ResponseResult.success(HttpEnums.SUCCESS);
        }
        return ResponseResult.fail("删除失败");
    }
}