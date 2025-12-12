// src/main/java/com/guang/resms/service/impl/CustomerServiceImpl.java
package com.guang.resms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guang.resms.entity.Customer;
import com.guang.resms.entity.dto.CustomerQueryDTO;
import com.guang.resms.mapper.CustomerMapper;
import com.guang.resms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private com.guang.resms.service.NotificationService notificationService;

    @Override
    public Page<Customer> getCustomerPage(CustomerQueryDTO queryDTO) {
        Page<Customer> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

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
        if (customer.getId() != null) {
            Customer old = this.getById(customer.getId());
            if (old != null) {
                oldSalesId = old.getSalesId();
            }
        } else {
            // 如果是新增，自动生成客户编号
            customer.setCustomerNo("KH" + System.currentTimeMillis());
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
}