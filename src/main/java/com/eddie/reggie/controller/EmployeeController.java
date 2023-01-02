package com.eddie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eddie.reggie.common.R;
import com.eddie.reggie.entity.Employee;
import com.eddie.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1）对获取到的密码进行MD5加密
        String password = new String();
        password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

        //2) 根据获取到的用户名查询数据库中是否存在此用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (null == emp) {
            return R.error("查无此用户");
        }
        //3) 若有此用户则进行用户的状态查看 是否是禁用状态
        if (0 == emp.getStatus()) {
            return R.error("当前用户处于禁用状态");
        }
        //4) 验证用户的密码是否正确
        if (!password.equals(emp.getPassword())) {
            return R.error("密码错误");
        }
        //5) 至此登录成功 同时将用户的ID写入session中
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    /**
     * 退出请求
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工请求
     * @param employee
     * @param request
     * @return
     */
    @PostMapping
    public R<String> addEmployee(@RequestBody Employee employee, HttpServletRequest request) {
        //设置系统默认的密码（md5加密）
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置用户创建时间和最后更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //设置创建人以及更新人的ID 即登录的人的id 从session中取
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        //将员工储存到数据库中
        employeeService.save(employee);
        return R.success("添加员工成功");
    }


    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);
        //构造page
        Page pageInfo = new Page(page, pageSize);
        //构造查询条件wrapper
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);//like语句 同时判断是否为null 若为null则不进行like查询 StringUtils.isNotEmpty()--org.apache.commons.lang包下
        queryWrapper.orderByDesc(Employee::getUpdateTime);//根据修改时间进行降序排序
        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 更新员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updateUser(HttpServletRequest request, @RequestBody Employee employee) {
        Long empId = (Long) request.getSession().getAttribute("employee");//从session中获取登录的员工id
        employee.setUpdateUser(empId);//设置最近一次更改的员工的id
        employee.setUpdateTime(LocalDateTime.now());//设置最近一次更改的时间
        //在数据库中进行update操作
        employeeService.updateById(employee);
        return R.success("员工信息更新成功");
    }

    /**
     * 根据id查询employee
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id) {
        log.info("根据id {} 查询employee", id);
        Employee employee = employeeService.getById(id);
        if (null != employee) {
            return R.success(employee);
        } else {
            return R.error("未查询到id为: " + id + " 的员工");
        }
    }

}
