package com.guang.resms.module.chat.service.impl;

import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.module.user.entity.User;
import com.guang.resms.module.notice.entity.WorkNotice;
import com.guang.resms.module.user.mapper.UserMapper;
import com.guang.resms.module.chat.service.NotificationService;
import com.guang.resms.module.notice.service.WorkNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 自动通知服务实现类
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private WorkNoticeService workNoticeService;

    @Autowired
    private UserMapper userMapper;

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");

    /**
     * 小区新建通知 - 通知管理员审核
     */
    @Override
    public void notifyCommunityCreated(Integer communityId, String communityName, Integer creatorId) {
        User creator = userMapper.selectById(creatorId);
        String creatorName = creator != null ? creator.getRealName() : "未知用户";

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("小区待审核");
        notice.setNoticeContent(String.format("%s 提交了新小区【%s】，请及时审核。", creatorName, communityName));
        notice.setNoticeType(1); // 系统通知
        notice.setPriority(2); // 普通
        notice.setBizType("community_audit");
        notice.setBizId(communityId);
        notice.setRouterPath("/community/list");
        notice.setReceiverType(2); // 角色
        notice.setReceiverIds("[1]"); // 管理员角色ID=1
        notice.setSenderId(0); // 系统发送
        notice.setSenderName("系统");

        workNoticeService.publishNotice(notice);
    }

    @Override
    public void notifyTransactionPendingAdminFinishAudit(Integer transactionId, String transactionCode, Integer salesId) {
        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("交易完成待审核");
        notice.setNoticeContent(String.format("交易【%s】已提交完成申请，请管理员审核。", transactionCode));
        notice.setNoticeType(2); // 任务
        notice.setPriority(1); // 紧急
        notice.setBizType("transaction_finish_audit");
        notice.setBizId(transactionId);
        notice.setRouterPath("/transaction/audit");
        notice.setReceiverType(2); // 角色
        notice.setReceiverIds("[1]"); // 管理员角色ID=1
        notice.setSenderId(0);
        notice.setSenderName("系统");
        workNoticeService.publishNotice(notice);
    }

    @Override
    public void notifyTransactionPendingManagerAudit(Integer transactionId, String transactionCode, Integer salesId) {
        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("交易待审核(待定金)");
        notice.setNoticeContent(String.format("交易【%s】已进入待付定金待经理审核阶段，请及时处理。", transactionCode));
        notice.setNoticeType(2); // 任务
        notice.setPriority(1); // 紧急
        notice.setBizType("transaction_manager_audit");
        notice.setBizId(transactionId);
        notice.setRouterPath("/transaction/audit");
        notice.setReceiverType(2); // 角色
        notice.setReceiverIds("[3]"); // 销售经理角色ID=3
        notice.setSenderId(0);
        notice.setSenderName("系统");
        workNoticeService.publishNotice(notice);
    }

    @Override
    public void notifyTransactionPendingFinanceAudit(Integer transactionId, String transactionCode, Integer salesId,
            String stageText) {
        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("交易待财务处理");
        notice.setNoticeContent(String.format("交易【%s】已进入阶段：%s，请财务跟进处理。", transactionCode, stageText));
        notice.setNoticeType(2); // 任务
        notice.setPriority(2); // 普通
        notice.setBizType("transaction_finance_task");
        notice.setBizId(transactionId);
        notice.setRouterPath("/transaction/audit");
        notice.setReceiverType(2); // 角色
        notice.setReceiverIds("[4]"); // 财务角色ID=4
        notice.setSenderId(0);
        notice.setSenderName("系统");
        workNoticeService.publishNotice(notice);
    }

    /**
     * 小区审核结果通知 - 通知创建人
     */
    @Override
    public void notifyCommunityAudited(Integer communityId, String communityName, Integer creatorId,
            boolean approved, String reason) {
        if (creatorId == null)
            return;

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle(approved ? "小区审核通过" : "小区审核未通过");

        String content = approved
                ? String.format("您提交的小区【%s】已审核通过。", communityName)
                : String.format("您提交的小区【%s】未通过审核%s", communityName,
                        reason != null && !reason.isEmpty() ? "，原因：" + reason : "。");

        notice.setNoticeContent(content);
        notice.setNoticeType(1); // 系统通知
        notice.setPriority(approved ? 2 : 1); // 驳回为紧急
        notice.setBizType("community_audit_result");
        notice.setBizId(communityId);
        notice.setRouterPath("/community/list");
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + creatorId + "]");

        // 获取当前审核人信息
        Integer auditorId = SecurityUtils.getUserId();
        User auditor = auditorId != null ? userMapper.selectById(auditorId) : null;
        notice.setSenderId(auditorId != null ? auditorId : 0);
        notice.setSenderName(auditor != null ? auditor.getRealName() : "系统");

        workNoticeService.publishNotice(notice);
    }

    /**
     * 楼盘新建通知 - 通知管理员审核
     */
    @Override
    public void notifyProjectCreated(Integer projectId, String projectName, Integer creatorId) {
        User creator = userMapper.selectById(creatorId);
        String creatorName = creator != null ? creator.getRealName() : "未知用户";

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("楼盘待审核");
        notice.setNoticeContent(String.format("%s 提交了新楼盘【%s】，请及时审核。", creatorName, projectName));
        notice.setNoticeType(1); // 系统通知
        notice.setPriority(2); // 普通
        notice.setBizType("project_audit");
        notice.setBizId(projectId);
        notice.setRouterPath("/project/list");
        notice.setReceiverType(2); // 角色
        notice.setReceiverIds("[1]"); // 管理员角色ID=1
        notice.setSenderId(0); // 系统发送
        notice.setSenderName("系统");

        workNoticeService.publishNotice(notice);
    }

    /**
     * 楼盘审核结果通知 - 通知创建人
     */
    @Override
    public void notifyProjectAudited(Integer projectId, String projectName, Integer creatorId,
            boolean approved, Integer newStatus, String reason) {
        if (creatorId == null)
            return;

        String statusText = getProjectStatusText(newStatus);

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle(approved ? "楼盘审核通过" : "楼盘审核未通过");

        String content = approved
                ? String.format("您提交的楼盘【%s】已审核通过，当前状态：%s。", projectName, statusText)
                : String.format("您提交的楼盘【%s】未通过审核%s", projectName,
                        reason != null && !reason.isEmpty() ? "，原因：" + reason : "。");

        notice.setNoticeContent(content);
        notice.setNoticeType(1); // 系统通知
        notice.setPriority(approved ? 2 : 1); // 驳回为紧急
        notice.setBizType("project_audit_result");
        notice.setBizId(projectId);
        notice.setRouterPath("/project/list");
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + creatorId + "]");

        Integer auditorId = SecurityUtils.getUserId();
        User auditor = auditorId != null ? userMapper.selectById(auditorId) : null;
        notice.setSenderId(auditorId != null ? auditorId : 0);
        notice.setSenderName(auditor != null ? auditor.getRealName() : "系统");

        workNoticeService.publishNotice(notice);
    }

    /**
     * 佣金核算通知 - 通知对应销售
     */
    @Override
    public void notifyCommissionCalculated(Integer commissionId, Integer salesId, BigDecimal amount) {
        if (salesId == null)
            return;

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("佣金已核算");
        notice.setNoticeContent(String.format("您有一笔佣金（￥%s）已完成核算，请查看详情。", MONEY_FORMAT.format(amount)));
        notice.setNoticeType(4); // 佣金通知
        notice.setPriority(2); // 普通
        notice.setBizType("commission_calculate");
        notice.setBizId(commissionId);
        notice.setRouterPath("/commission/list");
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + salesId + "]");

        Integer financeId = SecurityUtils.getUserId();
        User finance = financeId != null ? userMapper.selectById(financeId) : null;
        notice.setSenderId(financeId != null ? financeId : 0);
        notice.setSenderName(finance != null ? finance.getRealName() : "财务部");

        workNoticeService.publishNotice(notice);
    }

    /**
     * 佣金发放通知 - 通知对应销售
     */
    @Override
    public void notifyCommissionIssued(Integer commissionId, Integer salesId, BigDecimal amount) {
        if (salesId == null)
            return;

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("佣金已发放");
        notice.setNoticeContent(String.format("恭喜！您有一笔佣金（￥%s）已发放，请注意查收。", MONEY_FORMAT.format(amount)));
        notice.setNoticeType(4); // 佣金通知
        notice.setPriority(1); // 紧急（好消息优先展示）
        notice.setBizType("commission_issue");
        notice.setBizId(commissionId);
        notice.setRouterPath("/commission/list");
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + salesId + "]");

        Integer financeId = SecurityUtils.getUserId();
        User finance = financeId != null ? userMapper.selectById(financeId) : null;
        notice.setSenderId(financeId != null ? financeId : 0);
        notice.setSenderName(finance != null ? finance.getRealName() : "财务部");

        workNoticeService.publishNotice(notice);
    }

    /**
     * 房源新建通知 - 通知管理员审核
     */
    @Override
    public void notifyHouseCreated(Integer houseId, String houseTitle, Integer salesId) {
        User sales = userMapper.selectById(salesId);
        String salesName = sales != null ? sales.getRealName() : "未知销售";

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("房源待审核");
        notice.setNoticeContent(String.format("%s 提交了新房源【%s】，请及时审核。", salesName, houseTitle));
        notice.setNoticeType(1); // 系统通知
        notice.setPriority(2); // 普通
        notice.setBizType("house_audit");
        notice.setBizId(houseId);
        notice.setRouterPath("/house/audit");
        notice.setReceiverType(2); // 角色
        notice.setReceiverIds("[1]"); // 管理员角色ID=1
        notice.setSenderId(0); // 系统发送
        notice.setSenderName("系统");

        workNoticeService.publishNotice(notice);
    }

    /**
     * 房源审核结果通知 - 通知销售
     */
    @Override
    public void notifyHouseAudited(Integer houseId, String houseTitle, Integer salesId, boolean approved,
            String reason) {
        if (salesId == null)
            return;

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle(approved ? "房源审核通过" : "房源审核未通过");

        String content = approved
                ? String.format("您的房源【%s】已审核通过，已上架。", houseTitle)
                : String.format("您的房源【%s】未通过审核%s", houseTitle,
                        reason != null && !reason.isEmpty() ? "，原因：" + reason : "。");

        notice.setNoticeContent(content);
        notice.setNoticeType(1); // 系统通知
        notice.setPriority(approved ? 2 : 1); // 驳回为紧急
        notice.setBizType("house_audit_result");
        notice.setBizId(houseId);
        notice.setRouterPath("/house/my");
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + salesId + "]");

        Integer auditorId = SecurityUtils.getUserId();
        User auditor = auditorId != null ? userMapper.selectById(auditorId) : null;
        notice.setSenderId(auditorId != null ? auditorId : 0);
        notice.setSenderName(auditor != null ? auditor.getRealName() : "系统");

        workNoticeService.publishNotice(notice);
    }

    /**
     * 交易状态变更通知 - 通知销售
     */
    @Override
    public void notifyTransactionStatusChanged(Integer transactionId, String transactionCode, Integer salesId,
            String statusText) {
        if (salesId == null)
            return;

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("交易状态更新");
        notice.setNoticeContent(String.format("您的交易【%s】状态已更新为：%s", transactionCode, statusText));
        notice.setNoticeType(3); // 交易通知
        notice.setPriority(2); // 普通
        notice.setBizType("transaction_update");
        notice.setBizId(transactionId);
        notice.setRouterPath("/transaction/detail/" + transactionId);
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + salesId + "]");

        Integer operatorId = SecurityUtils.getUserId();
        User operator = operatorId != null ? userMapper.selectById(operatorId) : null;
        notice.setSenderId(operatorId != null ? operatorId : 0);
        notice.setSenderName(operator != null ? operator.getRealName() : "系统");

        workNoticeService.publishNotice(notice);
    }

    /**
     * 收款确认通知 - 通知销售
     */
    @Override
    public void notifyPaymentConfirmed(Integer paymentId, Integer transactionId, Integer salesId, BigDecimal amount) {
        if (salesId == null)
            return;

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("收款已确认");
        notice.setNoticeContent(String.format("交易关联的收款（￥%s）已确认到账。", MONEY_FORMAT.format(amount)));
        notice.setNoticeType(3); // 交易通知
        notice.setPriority(2); // 普通
        notice.setBizType("payment_confirmed");
        notice.setBizId(paymentId);
        notice.setRouterPath("/transaction/detail/" + transactionId); // 跳转到交易详情
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + salesId + "]");

        Integer financeId = SecurityUtils.getUserId();
        User finance = financeId != null ? userMapper.selectById(financeId) : null;
        notice.setSenderId(financeId != null ? financeId : 0);
        notice.setSenderName(finance != null ? finance.getRealName() : "财务部");

        workNoticeService.publishNotice(notice);
    }

    /**
     * 收款作废通知 - 通知销售
     */
    @Override
    public void notifyPaymentVoided(Integer paymentId, Integer transactionId, Integer salesId, String reason) {
        if (salesId == null)
            return;

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("收款已作废");
        notice.setNoticeContent(String.format("交易关联的一笔收款已被作废%s",
                reason != null && !reason.isEmpty() ? "，原因：" + reason : "。"));
        notice.setNoticeType(3); // 交易通知
        notice.setPriority(1); // 紧急
        notice.setBizType("payment_voided");
        notice.setBizId(paymentId);
        notice.setRouterPath("/transaction/detail/" + transactionId);
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + salesId + "]");

        Integer financeId = SecurityUtils.getUserId();
        User finance = financeId != null ? userMapper.selectById(financeId) : null;
        notice.setSenderId(financeId != null ? financeId : 0);
        notice.setSenderName(finance != null ? finance.getRealName() : "财务部");

        workNoticeService.publishNotice(notice);
    }

    @Override
    public void notifyCustomerAssigned(Integer customerId, String customerName, Integer salesId) {
        if (salesId == null)
            return;

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("新客户分配提醒");
        notice.setNoticeContent(String.format("您已分配新客户【%s】，请尽快跟进。", customerName));
        notice.setNoticeType(1); // 系统通知
        notice.setPriority(2); // 普通
        notice.setBizType("customer_assign");
        notice.setBizId(customerId);
        notice.setRouterPath("/customer/my");
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + salesId + "]");

        Integer operatorId = SecurityUtils.getUserId();
        User operator = (operatorId != null) ? userMapper.selectById(operatorId) : null;
        notice.setSenderId(operatorId != null ? operatorId : 0);
        notice.setSenderName(operator != null ? operator.getRealName() : "系统");

        workNoticeService.publishNotice(notice);
    }

    @Override
    public void notifyVisitCreated(Integer visitId, String customerName, Integer salesId, java.util.Date viewTime) {
        if (salesId == null)
            return;

        String timeStr = cn.hutool.core.date.DateUtil.formatDateTime(viewTime);

        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("新增带看任务");
        notice.setNoticeContent(String.format("客户【%s】预约了带看，时间：%s，请准时参加。", customerName, timeStr));
        notice.setNoticeType(1); // 系统通知
        notice.setPriority(2); // 普通
        notice.setBizType("visit_task");
        notice.setBizId(visitId);
        notice.setRouterPath("/view-record/my");
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + salesId + "]");

        Integer operatorId = SecurityUtils.getUserId();
        User operator = (operatorId != null) ? userMapper.selectById(operatorId) : null;
        notice.setSenderId(operatorId != null ? operatorId : 0);
        notice.setSenderName(operator != null ? operator.getRealName() : "系统");

        workNoticeService.publishNotice(notice);
    }

    @Override
    public void notifyTeamChanged(Integer userId, String teamName, boolean isJoin) {
        WorkNotice notice = new WorkNotice();
        notice.setNoticeTitle("团队变动通知");
        String action = isJoin ? "加入" : "移出";
        notice.setNoticeContent(String.format("您已被%s团队【%s】。", action, teamName));
        notice.setNoticeType(1); // 系统通知
        notice.setPriority(2); // 普通
        notice.setBizType("team_change");
        notice.setBizId(userId);
        notice.setRouterPath("/account/profile");
        notice.setReceiverType(1); // 个人
        notice.setReceiverIds("[" + userId + "]");

        Integer operatorId = SecurityUtils.getUserId();
        User operator = (operatorId != null) ? userMapper.selectById(operatorId) : null;
        notice.setSenderId(operatorId != null ? operatorId : 0);
        notice.setSenderName(operator != null ? operator.getRealName() : "系统");

        workNoticeService.publishNotice(notice);
    }

    private String getProjectStatusText(Integer status) {
        if (status == null)
            return "未知";
        return switch (status) {
            case 1 -> "在售";
            case 2 -> "售罄";
            case 3 -> "待售";
            case 4 -> "待审核";
            default -> "未知";
        };
    }
}
