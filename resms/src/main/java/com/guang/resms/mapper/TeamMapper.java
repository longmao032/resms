package com.guang.resms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.Team;
import com.guang.resms.entity.dto.TeamPerformanceQueryDTO;
import com.guang.resms.entity.dto.TeamQueryDTO;
import com.guang.resms.entity.vo.TeamPerformanceVO;
import com.guang.resms.entity.vo.TeamVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeamMapper extends BaseMapper<Team> {

        /**
         * 分页查询团队列表（关联查询经理姓名和成员数量）
         */
        IPage<TeamVO> selectTeamPage(Page<TeamVO> page, @Param("query") TeamQueryDTO query);

        /**
         * 查询团队详情
         */
        TeamVO selectTeamDetailById(@Param("id") Integer id);

        /**
         * 统计团队业绩
         * 
         * @param queryDTO 查询条件（支持按时间范围、团队名称筛选）
         */
        List<TeamPerformanceVO> selectTeamPerformance(@Param("query") TeamPerformanceQueryDTO query);
}