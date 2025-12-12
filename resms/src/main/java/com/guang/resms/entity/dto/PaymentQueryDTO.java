package com.guang.resms.entity.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class PaymentQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private Integer transactionId;
    private String receiptNo;
    private Integer paymentType;
    private Integer paymentStatus;
    private Integer flowType;
    private Integer financeId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}