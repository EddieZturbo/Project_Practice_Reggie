package com.eddie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eddie.reggie.common.BaseContext;
import com.eddie.reggie.common.R;
import com.eddie.reggie.entity.ShoppingCart;
import com.eddie.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 @author EddieZhang
 @create 2023-01-05 13:39
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车方法
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //设置用户的id到购物车中 指明购物车所属的用户
        shoppingCart.setUserId(BaseContext.getCurrentId());//TODO 购物车功能的实现使用session中的userId 每个用户的购物车是根据用户的id隔离的

        //查询购物车中是否已经添加过该菜品或者套餐 若添加过则number+=1 未添加过则进行添加
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getId() != null, ShoppingCart::getId, shoppingCart.getId());//根据购物车id进行查询 判断是否已经添加过

        //判断添加至购物车的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        if (null != dishId) {//表明添加的是菜品 根据菜品进行查询
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {//表明添加的是套餐 根据套餐进行查询
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //进行查询
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        //判断购物车中是否已经添加过此菜品或者套餐
        if (null != shoppingCartOne) {//表明添加过 将number+=1即可
            shoppingCartOne.setNumber(shoppingCartOne.getNumber() + 1);
            shoppingCartService.updateById(shoppingCartOne);
        } else {//表明未添加过 进行添加 并将number赋值为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());//设置createTime
            shoppingCartService.save(shoppingCart);
            shoppingCartOne = shoppingCart;//将新添加的cart赋值给shoppingCartOne 统一返回shoppingCartOne对象
        }
        return R.success(shoppingCartOne);
    }

    /**
     * 获取购物车方法
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        //根据用户的id查询购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(null != BaseContext.getCurrentId(), ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> cartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(cartList);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        //根据用户id查询购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(null != BaseContext.getCurrentId(), ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return R.success("成功清空购物车");
    }
}
