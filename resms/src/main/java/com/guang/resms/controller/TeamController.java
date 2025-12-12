package com.guang.resms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.entity.dto.TeamDTO;
import com.guang.resms.entity.dto.TeamPerformanceQueryDTO;
import com.guang.resms.entity.dto.TeamQueryDTO;
import com.guang.resms.entity.vo.TeamPerformanceVO;
import com.guang.resms.entity.vo.TeamVO;
import com.guang.resms.service.TeamService;
import com.guang.resms.common.exception.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // 分页列表
    @GetMapping("/list")
    public ResponseResult<IPage<TeamVO>> list(TeamQueryDTO queryDTO) {
        return ResponseResult.success(teamService.getTeamPage(queryDTO));
    }

    @GetMapping("/performance")
    public ResponseResult<List<TeamPerformanceVO>> getTeamPerformance(TeamPerformanceQueryDTO query) {
        return ResponseResult.success(teamService.getTeamPerformance(query));
    }

    // 获取详情
    @GetMapping("/{id}")
    public ResponseResult<TeamVO> getDetail(@PathVariable Integer id) {
        return ResponseResult.success(teamService.getTeamDetail(id));
    }

    // 新增团队
    @PostMapping
    public ResponseResult<Boolean> add(@RequestBody @Validated TeamDTO teamDTO) {
        return ResponseResult.success(teamService.addTeam(teamDTO));
    }

    // 更新团队
    @PutMapping
    public ResponseResult<Boolean> update(@RequestBody @Validated TeamDTO teamDTO) {
        return ResponseResult.success(teamService.updateTeam(teamDTO));
    }

    // 解散团队
    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> delete(@PathVariable Integer id) {
        return ResponseResult.success(teamService.deleteTeam(id));
    }
}