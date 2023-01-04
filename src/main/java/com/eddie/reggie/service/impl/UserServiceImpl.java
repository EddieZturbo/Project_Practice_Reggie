package com.eddie.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eddie.reggie.entity.User;
import com.eddie.reggie.mapper.UserMapper;
import com.eddie.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 @author EddieZhang
 @create 2023-01-04 16:59
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
