package com.guang.resms.module.chat.service;

/**
 * 自动通知服务接口
 * 用于在业务操作后自动发送系统通知
 */
public interface NotificationService {

        /**
         * 小区新建通知 - 通知管理员审核
         * 
         * @param communityId   小区ID
         * @param communityName 小区名称
         * @param creatorId     创建人ID
         */
        void notifyCommunityCreated(Integer communityId, String communityName, Integer creatorId);

        /**
         * 小区审核结果通知 - 通知创建人
         * 
         * @param communityId   小区ID
         * @param communityName 小区名称
         * @param creatorId     创建人ID
         * @param approved      是否通过
         * @param reason        审核原因（驳回时使用）
         */
        void notifyCommunityAudited(Integer communityId, String communityName, Integer creatorId,
                        boolean approved, String reason);

        /**
         * 楼盘新建通知 - 通知管理员审核
         * 
         * @param projectId   楼盘ID
         * @param projectName 楼盘名称
         * @param creatorId   创建人ID
         */
        void notifyProjectCreated(Integer projectId, String projectName, Integer creatorId);

        /**
         * 楼盘审核结果通知 - 通知创建人
         * 
         * @param projectId   楼盘ID
         * @param projectName 楼盘名称
         * @param creatorId   创建人ID
         * @param approved    是否通过
         * @param newStatus   新状态
         * @param reason      审核原因
         */
        void notifyProjectAudited(Integer projectId, String projectName, Integer creatorId,
                        boolean approved, Integer newStatus, String reason);

        /**
         * 佣金核算通知 - 通知对应销售
         * 
         * @param commissionId 佣金ID
         * @param salesId      销售ID
         * @param amount       佣金金额
         */
        void notifyCommissionCalculated(Integer commissionId, Integer salesId, java.math.BigDecimal amount);

        /**
         * 佣金发放通知 - 通知对应销售
         * 
         * @param commissionId 佣金ID
         * @param salesId      销售ID
         * @param amount       佣金金额
         */
        void notifyCommissionIssued(Integer commissionId, Integer salesId, java.math.BigDecimal amount);

        /**
         * 房源新建通知 - 通知管理员审核
         * 
         * @param houseId    房源ID
         * @param houseTitle 房源标题
         * @param salesId    销售ID
         */
        void notifyHouseCreated(Integer houseId, String houseTitle, Integer salesId);

        /**
         * 房源审核结果通知 - 通知销售
         * 
         * @param houseId    房源ID
         * @param houseTitle 房源标题
         * @param salesId    销售ID
         * @param approved   是否通过
         * @param reason     审核原因
         */
        void notifyHouseAudited(Integer houseId, String houseTitle, Integer salesId, boolean approved, String reason);

        /**
         * 交易状态变更通知 - 通知销售
         * 
         * @param transactionId   交易ID
         * @param transactionCode 交易编号
         * @param salesId         销售ID
         * @param statusText      状态文本
         */
        void notifyTransactionStatusChanged(Integer transactionId, String transactionCode, Integer salesId,
                        String statusText);

        /**
         * 交易进入“待经理审核”阶段通知 - 通知销售经理
         */
        void notifyTransactionPendingManagerAudit(Integer transactionId, String transactionCode, Integer salesId);

        /**
         * 交易进入“待财务处理”阶段通知 - 通知财务
         */
        void notifyTransactionPendingFinanceAudit(Integer transactionId, String transactionCode, Integer salesId,
                        String stageText);

        void notifyTransactionPendingAdminFinishAudit(Integer transactionId, String transactionCode, Integer salesId);

        /**
         * 收款确认通知 - 通知销售
         * 
         * @param paymentId     收款ID
         * @param transactionId 交易ID
         * @param salesId       销售ID
         * @param amount        收款金额
         */
        void notifyPaymentConfirmed(Integer paymentId, Integer transactionId, Integer salesId,
                        java.math.BigDecimal amount);

        /**
         * 收款作废通知 - 通知销售
         * 
         * @param paymentId     收款ID
         * @param transactionId 交易ID
         * @param salesId       销售ID
         * @param reason        作废原因
         */
        void notifyPaymentVoided(Integer paymentId, Integer transactionId, Integer salesId, String reason);

        /**
         * 客户分配通知 - 通知销售
         * 
         * @param customerId   客户ID
         * @param customerName 客户姓名
         * @param salesId      销售ID
         */
        void notifyCustomerAssigned(Integer customerId, String customerName, Integer salesId);

        /**
         * 带看任务通知 - 通知销售
         * 
         * @param visitId      带看ID
         * @param customerName 客户姓名
         * @param salesId      销售ID
         * @param viewTime     带看时间
         */
        void notifyVisitCreated(Integer visitId, String customerName, Integer salesId, java.util.Date viewTime);

        /**
         * 团队变动通知 - 通知用户
         * 
         * @param userId   用户ID
         * @param teamName 团队名称
         * @param isJoin   是否加入 (true=加入, false=移出)
         */
        void notifyTeamChanged(Integer userId, String teamName, boolean isJoin);
}
