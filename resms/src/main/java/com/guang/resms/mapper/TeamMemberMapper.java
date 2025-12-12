package com.guang.resms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.entity.TeamMember;
import com.guang.resms.entity.vo.TeamMemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    List<TeamMemberVO> selectMembersByTeamId(@Param("teamId") Integer teamId);
}