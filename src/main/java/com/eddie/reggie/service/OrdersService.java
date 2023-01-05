package com.eddie.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eddie.reggie.entity.Orders;

/**
 @author EddieZhang
 @create 2023-01-05 15:53
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 提交订单
     * @param orders
     */
    public void submit(Orders orders);
}
