// src/main/java/com/guang/resms/service/CustomerService.java
package com.guang.resms.module.customer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guang.resms.module.customer.entity.Customer;
import com.guang.resms.module.customer.entity.dto.CustomerQueryDTO;

public interface CustomerService extends IService<Customer> {
    Page<Customer> getCustomerPage(CustomerQueryDTO queryDTO);
    boolean saveCustomer(Customer customer);
}