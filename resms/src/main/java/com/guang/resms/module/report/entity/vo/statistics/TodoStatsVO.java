package com.guang.resms.module.report.entity.vo.statistics;

import lombok.Data;

/**
 * 待办事项统计 VO
 * 根据用户角色返回不同的待办数据
 */
@Data
public class TodoStatsVO {

    // ========== 管理员待办 ==========
    /**
     * 待审核交易数
     */
    private Integer pendingTransactionAudits;

    /**
     * 待审核房源数
     */
    private Integer pendingHouseAudits;

    /**
     * 待审核项目数
     */
    private Integer pendingProjectAudits;

    // ========== 销售经理待办 ==========
    /**
     * 团队待审核交易数
     */
    private Integer teamPendingTransactions;

    /**
     * 团队待跟进客户数
     */
    private Integer teamFollowUpCustomers;

    // ========== 销售顾问待办 ==========
    /**
     * 我的待审核交易数
     */
    private Integer myPendingTransactions;

    /**
     * 我的待跟进客户数
     */
    private Integer myFollowUpCustomers;

    // ========== 财务待办 ==========
    /**
     * 待确认收款数
     */
    private Integer pendingPaymentConfirms;

    /**
     * 待发放佣金数
     */
    private Integer pendingCommissions;

    /**
     * 待处理交易完成申请数
     */
    private Integer pendingFinanceConfirms;
}
