package com.eddie.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eddie.reggie.entity.Dish;
import com.eddie.reggie.mapper.DishMapper;
import com.eddie.reggie.service.DishService;
import org.springframework.stereotype.Service;

/**
 @author EddieZhang
 @create 2023-01-02 19:47
 */
@Service
public class DIshServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
