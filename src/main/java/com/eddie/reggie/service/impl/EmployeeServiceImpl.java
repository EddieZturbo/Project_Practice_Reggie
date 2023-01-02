package com.eddie.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eddie.reggie.entity.Employee;
import com.eddie.reggie.mapper.EmployeeMapper;
import com.eddie.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 @author EddieZhang
 @create 2023-01-01 17:47
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
