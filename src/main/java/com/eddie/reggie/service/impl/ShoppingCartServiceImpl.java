package com.eddie.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eddie.reggie.entity.ShoppingCart;
import com.eddie.reggie.mapper.ShoppingCartMapper;
import com.eddie.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 @author EddieZhang
 @create 2023-01-05 13:38
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
