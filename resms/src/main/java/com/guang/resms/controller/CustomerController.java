// src/main/java/com/guang/resms/controller/CustomerController.java
package com.guang.resms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guang.resms.entity.Customer;
import com.guang.resms.entity.dto.CustomerQueryDTO;
import com.guang.resms.service.CustomerService;
import com.guang.resms.common.exception.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // 分页查询
    @PostMapping("/page")
    public ResponseResult<Page<Customer>> getPage(@RequestBody CustomerQueryDTO queryDTO) {
        return ResponseResult.success(customerService.getCustomerPage(queryDTO));
    }

    // 获取详情
    @GetMapping("/{id}")
    public ResponseResult<Customer> getById(@PathVariable Integer id) {
        return ResponseResult.success(customerService.getById(id));
    }

    // 新增或更新
    @PostMapping
    public ResponseResult<Boolean> saveOrUpdate(@RequestBody Customer customer) {
        return ResponseResult.success(customerService.saveCustomer(customer));
    }

    // 删除
    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> delete(@PathVariable Integer id) {
        return ResponseResult.success(customerService.removeById(id));
    }
    
    // 批量删除
    @DeleteMapping("/batch")
    public ResponseResult<Boolean> batchDelete(@RequestBody List<Integer> ids) {
        return ResponseResult.success(customerService.removeByIds(ids));
    }
}