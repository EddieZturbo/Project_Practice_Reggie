package com.eddie.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eddie.reggie.dto.DishDto;
import com.eddie.reggie.entity.Dish;
import com.eddie.reggie.entity.DishFlavor;
import com.eddie.reggie.mapper.DishMapper;
import com.eddie.reggie.service.DishFlavorService;
import com.eddie.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 @author EddieZhang
 @create 2023-01-02 19:47
 */
@Service
public class DIshServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 保存dish并同时保存dishFlavor
     * @param dishDto
     */
    @Override
    @Transactional//TODO 开启事务
    public void saveWithFlavor(DishDto dishDto) {
        //将dish储存进数据库中的dish表
        this.save(dishDto);

        //将dishFlavor储存到数据库中的dishFlavor表 储存之前使用stream流的方式将dishId储存进dishFlavor中的相应字段
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream()//TODO 使用stream流的方式将dishId储存进dishFlavor中的相应字段
                .map((dishFlavor) -> {
                    dishFlavor.setDishId(dishId);
                    return dishFlavor;
                })
                .collect(Collectors.toList());

        //将dishFlavor储存到数据库中的dishFlavor表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的Flavor信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();

        //查询dish表
        Dish dish = this.getById(id);


        //查询dishFlavor表
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(id != null, DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);


        //将查询到的数据封装到DishDto对象中
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表数据
        this.updateById(dishDto);

        //更新dishFlavor表数据
        //先清理 再保存 TODO 由于口味同时存在删除和修改的操作 因此修改的时候使用先清理再保存的方式
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(null != dishDto.getId(), DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);//清理

        List<DishFlavor> flavors = dishDto.getFlavors();//此时的flavors中dishId字段未进行赋值 使用stream()流的形式进行赋值
        flavors.stream()
                .map((item) -> {
                    item.setDishId(dishDto.getId());
                    return item;
                })
                .collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);//保存
    }


}
