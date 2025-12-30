package com.guang.resms.module.customer.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.module.customer.entity.Customer;
import com.guang.resms.module.customer.entity.dto.CustomerQueryDTO;
import com.guang.resms.module.customer.mapper.CustomerMapper;
import com.guang.resms.module.customer.service.CustomerService;
import com.guang.resms.module.team.mapper.TeamMemberMapper;
import com.guang.resms.module.transaction.entity.Transaction;
import com.guang.resms.module.transaction.mapper.TransactionMapper;
import com.guang.resms.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private com.guang.resms.module.chat.service.NotificationService notificationService;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public Page<Customer> getCustomerPage(CustomerQueryDTO queryDTO) {
        Page<Customer> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        // ========== 数据权限过滤 ==========
        Integer currentUserId = SecurityUtils.getUserId();
        Integer roleType = SecurityUtils.getRoleType();

        if (roleType != null) {
            if (roleType == 2) {
                // 销售顾问: 只看自己负责的客户
                queryDTO.setSalesId(currentUserId);
                wrapper.eq(Customer::getSalesId, currentUserId);
            } else if (roleType == 3) {
                // 销售经理: 看团队所有成员的客户
                List<Integer> teamMemberIds = getTeamMemberIds(currentUserId);
                if (teamMemberIds != null && !teamMemberIds.isEmpty()) {
                    wrapper.in(Customer::getSalesId, teamMemberIds);
                } else {
                    // 如果没有团队成员,只能看自己的
                    wrapper.eq(Customer::getSalesId, currentUserId);
                }
            }
            // roleType == 1 (管理员) 或 roleType == 4 (财务): 看全部,不加限制
        }

        // 动态查询条件
        wrapper.like(StrUtil.isNotBlank(queryDTO.getRealName()), Customer::getRealName, queryDTO.getRealName())
                .like(StrUtil.isNotBlank(queryDTO.getPhone()), Customer::getPhone, queryDTO.getPhone())
                .eq(queryDTO.getIntentionLevel() != null, Customer::getIntentionLevel, queryDTO.getIntentionLevel())
                .eq(queryDTO.getSalesId() != null, Customer::getSalesId, queryDTO.getSalesId())
                // 排序
                .orderByDesc(Customer::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveCustomer(Customer customer) {
        Integer oldSalesId = null;
        boolean isNew = (customer.getId() == null);

        if (!isNew) {
            Customer old = this.getById(customer.getId());
            if (old != null) {
                oldSalesId = old.getSalesId();
            }
        } else {
            // 新增客户: 自动生成客户编号
            customer.setCustomerNo("KH" + System.currentTimeMillis());

            // ========== 强制绑定当前用户为负责销售 ==========
            Integer currentUserId = SecurityUtils.getUserId();
            Integer roleType = SecurityUtils.getRoleType();

            if (roleType != null && roleType == 2) {
                // 销售顾问: 强制绑定自己
                customer.setSalesId(currentUserId);
            } else if (customer.getSalesId() == null) {
                // 管理员/经理: 如果未指定销售,默认为当前用户
                customer.setSalesId(currentUserId);
            }
            // 管理员/经理明确指定了salesId则使用传入的值
        }

        boolean result = this.saveOrUpdate(customer);

        // 检查是否发生销售分配变更，发送通知
        if (result && customer.getSalesId() != null) {
            // 新增分配 或 修改分配
            if (oldSalesId == null || !oldSalesId.equals(customer.getSalesId())) {
                notificationService.notifyCustomerAssigned(
                        customer.getId(),
                        customer.getRealName(),
                        customer.getSalesId());
            }
        }
        return result;
    }

    /**
     * 获取销售经理团队下所有成员的用户ID列表
     */
    private List<Integer> getTeamMemberIds(Integer managerId) {
        return teamMemberMapper.selectUserIdsByManagerId(managerId);
    }

    @Autowired
    private com.guang.resms.module.customer.mapper.ViewRecordMapper viewRecordMapper;

    /**
     * 重写删除方法,添加关联记录校验
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        // 权限校验：仅管理员可删除客户
        Integer roleType = SecurityUtils.getRoleType();
        if (roleType == null || roleType != 1) {
            throw new com.guang.resms.common.exception.ServiceException(
                    "无权限删除客户，仅管理员可操作");
        }

        // 检查是否有关联的未完成交易（status!=5表示未取消）
        LambdaQueryWrapper<Transaction> txWrapper = new LambdaQueryWrapper<>();
        txWrapper.eq(Transaction::getCustomerId, id)
                .ne(Transaction::getStatus, 5); // 5=已取消
        Long txCount = transactionMapper.selectCount(txWrapper);

        if (txCount != null && txCount > 0) {
            throw new com.guang.resms.common.exception.ServiceException(
                    "该客户存在关联的交易记录，无法删除。请先取消或完成相关交易");
        }

        // 检查是否有关联的看房记录
        LambdaQueryWrapper<com.guang.resms.module.customer.entity.ViewRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.guang.resms.module.customer.entity.ViewRecord::getCustomerId, id);
        Long count = viewRecordMapper.selectCount(wrapper);

        if (count > 0) {
            throw new com.guang.resms.common.exception.ServiceException(
                    "该客户存在关联的看房记录,无法删除");
        }

        return super.removeById(id);
    }
}
