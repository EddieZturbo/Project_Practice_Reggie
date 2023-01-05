package com.eddie.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eddie.reggie.common.BaseContext;
import com.eddie.reggie.common.CustomException;
import com.eddie.reggie.entity.*;
import com.eddie.reggie.mapper.OrdersMapper;
import com.eddie.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 @author EddieZhang
 @create 2023-01-05 15:54
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    /**
     * 提交订单
     * @param orders
     */
    @Override
    public void submit(Orders orders) {
        //获取当前登录用户的id
        Long userId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(userId != null, ShoppingCart::getUserId,userId);
        List<ShoppingCart> carts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);

        //判断购物车是否为null
        if (null == carts){//若购物车为null 抛出自定义的异常
            throw new CustomException("购物车是空的哦~~无法下订单");
        }

        //查询用户数据以及地址数据
        User userData = userService.getById(userId);//用户数据
        AddressBook addressBookData = addressBookService.getById(orders.getAddressBookId());//地址数据
        if (null == addressBookData){//若地址信息为null 抛出自定义的异常
            throw new CustomException("地址信息是空的哦~~无法下订单");
        }

        //订单号生成
        String idStr = IdWorker.getIdStr();//使用mybatisPlus提供的IdWorker 获取订单号 并给order赋订单号的值

        //向订单表中插入数据
        AtomicInteger amount = new AtomicInteger(0);//总金额 TODO JUC原子类

        List<OrderDetail> orderDetails = carts.stream()//遍历购物车数据 计算总金额 同时封装订单明细数据
                .map((item) -> {
                    OrderDetail orderDetail = new OrderDetail();//订单明细对象 要进行数据封装
                    orderDetail.setOrderId(Long.parseLong(idStr));
                    orderDetail.setNumber(item.getNumber());
                    orderDetail.setDishFlavor(item.getDishFlavor());
                    orderDetail.setDishId(item.getDishId());
                    orderDetail.setSetmealId(item.getSetmealId());
                    orderDetail.setName(item.getName());
                    orderDetail.setImage(item.getImage());
                    orderDetail.setAmount(item.getAmount());

                    amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());//总金额计算 TODO JUC原子类计算

                    return orderDetail;
                })
                .collect(Collectors.toList());

        orders.setId(Long.parseLong(idStr));//设置订单id
        orders.setOrderTime(LocalDateTime.now());//设置下单时间
        orders.setCheckoutTime(LocalDateTime.now());//设置核查时间
        orders.setStatus(2);//设置订单状态 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
        orders.setAmount(new BigDecimal(amount.get()));//设置总金额
        orders.setUserId(userId);//设置用户id
        orders.setNumber(idStr);//设置订单id
        orders.setUserName(userData.getName());
        orders.setConsignee(addressBookData.getConsignee());
        orders.setPhone(addressBookData.getPhone());
        orders.setAddress((addressBookData.getProvinceName() == null ? "" : addressBookData.getProvinceName())
                + (addressBookData.getCityName() == null ? "" : addressBookData.getCityName())
                + (addressBookData.getDistrictName() == null ? "" : addressBookData.getDistrictName())
                + (addressBookData.getDetail() == null ? "" : addressBookData.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);


        //向订单明细表中插入数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }
}
