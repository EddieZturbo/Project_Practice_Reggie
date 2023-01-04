package com.eddie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eddie.reggie.common.R;
import com.eddie.reggie.entity.User;
import com.eddie.reggie.service.UserService;
import com.eddie.reggie.utils.SendSms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * //移动端用户
 @author EddieZhang
 @create 2023-01-04 16:59
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 发送手机验证码的短信
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpServletRequest request, @RequestBody User user) {
        //获取手机号
        String phone = user.getPhone();

        //判断phone不为null
        if (null != phone) {
            //获取生成的验证码
            //调用阿里云提供的短信服务API完成发送短信
            SendSms.send(phone);
            //将收到的验证码保存到session中
            request.getSession().setAttribute(phone, "1234");
            return R.success("手机验证码发送成功");
        }
        return R.error("手机验证码发送失败");
    }

    /**
     * 移动端用户登录
     * @param request
     * @param map key:phone value:code
     * @return
     */
    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestBody Map map) {
        //获取手机号
        String phone = (String) map.get("phone");

        //获取验证码
        String code = (String) map.get("code");

        //对手机号和验证码进行比对（和session中的code进行比对）
        if (null != request.getSession().getAttribute(phone) && code.equals(request.getSession().getAttribute(phone))) {

            //判断当前的用户是否为新用户 若为新用户则进行自动的创建
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            if (null == user){//新用户 自动注册
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            //登录成功 将用户的id写入session中
            request.getSession().setAttribute("user",user.getId());
            return R.success(user);
        }
        //不匹配则无法登录
        return R.error("登录失败");
    }
}
