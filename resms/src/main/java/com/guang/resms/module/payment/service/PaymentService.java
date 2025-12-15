package com.guang.resms.module.payment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.module.payment.entity.Payment;
import com.guang.resms.module.payment.entity.dto.PaymentDTO;
import com.guang.resms.module.payment.entity.dto.PaymentQueryDTO;
import com.guang.resms.module.payment.entity.vo.PaymentStatisticsVO;
import com.guang.resms.module.payment.entity.vo.PaymentVO;

import java.util.List;

public interface PaymentService extends IService<Payment> {
    public IPage<PaymentVO> getPaymentPage(PaymentQueryDTO dto);
    public boolean addPayment(PaymentDTO paymentDTO);
    public boolean confirmPayment(Integer id);
    public boolean voidPayment(Integer id, String reason);
    public boolean updatePayment(PaymentDTO paymentDTO);
    public PaymentStatisticsVO getStatistics();
}