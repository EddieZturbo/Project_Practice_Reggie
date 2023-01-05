package com.eddie.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eddie.reggie.entity.OrderDetail;
import com.eddie.reggie.mapper.OrderDetailMapper;
import com.eddie.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 @author EddieZhang
 @create 2023-01-05 15:55
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
