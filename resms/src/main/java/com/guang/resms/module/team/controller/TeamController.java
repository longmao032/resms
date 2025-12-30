package com.guang.resms.module.team.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.guang.resms.module.team.entity.dto.TeamDTO;
import com.guang.resms.module.team.entity.dto.TeamPerformanceQueryDTO;
import com.guang.resms.module.team.entity.dto.TeamQueryDTO;
import com.guang.resms.module.team.entity.vo.TeamPerformanceVO;
import com.guang.resms.module.team.entity.vo.TeamVO;
import com.guang.resms.module.team.service.TeamService;
import com.guang.resms.common.exception.ResponseResult;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    /**
     * 导出团队业绩
     */
    @GetMapping("/performance/export")
    public void exportTeamPerformance(
            TeamPerformanceQueryDTO query,
            HttpServletResponse response) throws IOException {
        List<TeamPerformanceVO> list = teamService.getTeamPerformance(query);

        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("团队业绩报表");

        // 创建表头
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("排名");
        header.createCell(1).setCellValue("团队名称");
        header.createCell(2).setCellValue("团队经理");
        header.createCell(3).setCellValue("成员数");
        header.createCell(4).setCellValue("业绩目标");
        header.createCell(5).setCellValue("实际业绩");
        header.createCell(6).setCellValue("完成率(%)");
        header.createCell(7).setCellValue("成交单数");
        header.createCell(8).setCellValue("佣金总额");

        // 填充数据
        for (int i = 0; i < list.size(); i++) {
            TeamPerformanceVO vo = list.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(vo.getRank() != null ? vo.getRank() : (i + 1));
            row.createCell(1).setCellValue(vo.getTeamName() != null ? vo.getTeamName() : "");
            row.createCell(2).setCellValue(vo.getManagerName() != null ? vo.getManagerName() : "");
            row.createCell(3).setCellValue(vo.getMemberCount() != null ? vo.getMemberCount() : 0);
            row.createCell(4)
                    .setCellValue(vo.getPerformanceTarget() != null ? vo.getPerformanceTarget().toPlainString() : "0");
            row.createCell(5)
                    .setCellValue(vo.getActualPerformance() != null ? vo.getActualPerformance().toPlainString() : "0");
            row.createCell(6)
                    .setCellValue(vo.getCompletionRate() != null ? vo.getCompletionRate().toPlainString() : "0");
            row.createCell(7).setCellValue(vo.getTransactionCount() != null ? vo.getTransactionCount() : 0);
            row.createCell(8)
                    .setCellValue(vo.getCommissionTotal() != null ? vo.getCommissionTotal().toPlainString() : "0");
        }

        writeWorkbook(response, "团队业绩报表.xlsx", wb);
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

    /**
     * 写入Excel到响应流
     */
    private void writeWorkbook(HttpServletResponse response, String filename, XSSFWorkbook wb) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        try (ServletOutputStream os = response.getOutputStream()) {
            wb.write(os);
            os.flush();
        } finally {
            wb.close();
        }
    }
}