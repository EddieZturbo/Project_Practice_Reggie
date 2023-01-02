package com.eddie.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eddie.reggie.common.CustomException;
import com.eddie.reggie.entity.Category;
import com.eddie.reggie.entity.Dish;
import com.eddie.reggie.entity.Setmeal;
import com.eddie.reggie.mapper.CategoryMapper;
import com.eddie.reggie.service.CategoryService;
import com.eddie.reggie.service.DishService;
import com.eddie.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 @author EddieZhang
 @create 2023-01-02 17:37
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 移除方法 根据id进行移除 判断是否关联了dish或者setmeal
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查看当前分类是否关联了Dish 添加查询条件 根据分类id进行查询
        LambdaQueryWrapper<Dish> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(categoryLambdaQueryWrapper);
        if (count > 0){
            //关联了dish 抛出异常
            throw new CustomException("当前分类下关联了dish,不能删除");
        }
        //查看当前分类是否关联了Setmeal 添加查询条件 根据分类id进行查询
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1 > 0){
            //关联了setmeal 抛出异常
            throw new CustomException("当前分类下关联了setmeal,不能删除");
        }
        //未关联 直接删除
        super.removeById(id);
    }
}
