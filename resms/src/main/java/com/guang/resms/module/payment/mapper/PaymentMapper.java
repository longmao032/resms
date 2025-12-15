package com.guang.resms.module.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.module.payment.entity.Payment;
import com.guang.resms.module.payment.entity.dto.PaymentQueryDTO;
import com.guang.resms.module.payment.entity.vo.PaymentVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
    // 复杂关联查询
    IPage<PaymentVO> selectPaymentPage(Page<PaymentVO> page, @Param("dto") PaymentQueryDTO dto);

    // 统计各状态/时间段的金额 (返回 Map 方便一次性查询)
    @MapKey("key_name")
    List<Map<String, Object>> selectOverviewStats();

    // 统计近 6 个月趋势
    List<Map<String, Object>> selectTrendStats();

    // 统计款项类型占比
    List<Map<String, Object>> selectTypeStats();

    // 统计销售回款排行 Top 10
    List<Map<String, Object>> selectSalesRanking();
}