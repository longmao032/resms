package com.guang.resms.module.team.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.module.team.entity.TeamMember;
import com.guang.resms.module.team.entity.vo.TeamMemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    List<TeamMemberVO> selectMembersByTeamId(@Param("teamId") Integer teamId);

    /**
     * 根据用户ID查询团队ID (用于销售顾问)
     * 
     * @param userId 用户ID
     * @return 团队ID
     */
    Integer selectTeamIdByUserId(@Param("userId") Integer userId);

    /**
     * 根据经理ID查询其团队所有成员的用户ID
     * 
     * @param managerId 经理ID
     * @return 团队成员用户ID列表 (包含经理自己)
     */
    @Select("SELECT tm.user_id FROM tb_team_member tm " +
            "JOIN tb_team t ON tm.team_id = t.id " +
            "WHERE t.manager_id = #{managerId}")
    List<Integer> selectUserIdsByManagerId(@Param("managerId") Integer managerId);

    /**
     * 根据用户ID查询其所属团队的经理ID
     * 
     * @param userId 用户ID (销售顾问)
     * @return 经理ID (如果用户不在任何团队则返回null)
     */
    @Select("SELECT t.manager_id FROM tb_team t " +
            "JOIN tb_team_member tm ON t.id = tm.team_id " +
            "WHERE tm.user_id = #{userId} LIMIT 1")
    Integer selectManagerIdByUserId(@Param("userId") Integer userId);
}
