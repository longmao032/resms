package com.guang.resms.module.house.controller;

import com.guang.resms.module.user.entity.dto.QueryDTO;
import com.guang.resms.module.house.service.SecondHouseInfoService;
import com.guang.resms.common.exception.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 二手房信息控制器
 *
 * @author guang
 */
@RestController
@RequestMapping("/second-house")
public class SecondHouseController {

    @Autowired
    private SecondHouseInfoService secondHouseInfoService;

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
    public ResponseResult getSecondHouseVoList(@RequestParam(defaultValue = "1") Integer pageNum,
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
        return ResponseResult.success(secondHouseInfoService.selectSecondHouseVoPage(queryDTO));
    }
}