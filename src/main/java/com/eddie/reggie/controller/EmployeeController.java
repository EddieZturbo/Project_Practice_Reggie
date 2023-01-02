package com.eddie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eddie.reggie.common.R;
import com.eddie.reggie.entity.Employee;
import com.eddie.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
 @author EddieZhang
 @create 2023-01-01 17:50
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录请求
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        //1）对获取到的密码进行MD5加密
        String password = new String();
        password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

        //2) 根据获取到的用户名查询数据库中是否存在此用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if(null == emp){
            return R.error("查无此用户");
        }
        //3) 若有此用户则进行用户的状态查看 是否是禁用状态
        if(0 == emp.getStatus()){
            return R.error("当前用户处于禁用状态");
        }
        //4) 验证用户的密码是否正确
        if(!password.equals(emp.getPassword())){
            return R.error("密码错误");
        }
        //5) 至此登录成功 同时将用户的ID写入session中
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    /**
     * 退出请求
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

}
