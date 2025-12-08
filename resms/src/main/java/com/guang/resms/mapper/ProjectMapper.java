package com.guang.resms.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.Project;
import com.guang.resms.entity.dto.QueryDTO;
import com.guang.resms.entity.vo.ProjectHouseStatVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProjectMapper extends BaseMapper<Project> {

    
    /**
     * 根据城市查询所有不重复的区县
     * @param city 城市名称
     * @return 区县列表
     */
    List<String> selectDistinctDistrictsByCity(@Param("city") String city);


    /**
     * 查询项目房屋统计分页
     * @param page 分页对象
     * @param params 查询参数
     * @return 项目房屋统计分页
     */
    IPage<ProjectHouseStatVO> selectProjectHouseStatPage(Page<ProjectHouseStatVO> page,@Param("params") QueryDTO params);
}