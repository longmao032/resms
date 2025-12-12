package com.guang.resms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.entity.Project;
import com.guang.resms.entity.dto.ProjectQueryDTO;
import com.guang.resms.entity.dto.QueryDTO;
import com.guang.resms.entity.vo.ProjectHouseStatVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public interface ProjectService extends IService<Project> {

    /**
     * 获取所有城市
     * 
     * @return 城市列表
     */
    HashMap<String, ArrayList<String>> getAllCity();

    /**
     * 获取所有不重复的区县
     * 
     * @param city 城市
     * @return 区县列表
     */
    ArrayList<String> getDistinctDistrictsByCity(String city);

    /**
     * 获取项目房屋统计分页
     * 
     * @param queryDTO 查询参数
     * @return 项目房屋统计分页
     */
    IPage<ProjectHouseStatVO> getProjectHouseStatPage(QueryDTO queryDTO);

    /**
     * 获取所有项目列表（用于下拉选择）
     * 
     * @return 项目列表
     */
    List<Project> getAllProjects();

    /**
     * 分页查询项目列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Map<String, Object> getProjectPage(ProjectQueryDTO queryDTO);

    /**
     * 新增项目
     * 
     * @param project    项目信息
     * @param coverImage 封面图片（可选）
     * @return 保存后的项目
     */
    Project addProject(Project project, MultipartFile coverImage);

    /**
     * 更新项目
     * 
     * @param project    项目信息
     * @param coverImage 封面图片（可选）
     * @return 更新后的项目
     */
    Project updateProject(Project project, MultipartFile coverImage);

    /**
     * 审核项目
     * 
     * @param id     项目ID
     * @param status 审核状态：1=在售，2=售罄，3=待售（通过），其他值为驳回
     * @param reason 审核原因（驳回时使用）
     */
    void auditProject(Integer id, Integer status, String reason);
}