// src/main/java/com/guang/resms/mapper/CustomerMapper.java
package com.guang.resms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guang.resms.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}