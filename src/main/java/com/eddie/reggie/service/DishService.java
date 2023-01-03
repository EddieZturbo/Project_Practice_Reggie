package com.eddie.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eddie.reggie.dto.DishDto;
import com.eddie.reggie.entity.Dish;

/**
 @author EddieZhang
 @create 2023-01-02 19:46
 */
public interface DishService extends IService<Dish> {
    /**
     * 保存dish并同时保存dishFlavor
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品信息和对应的Flavor信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id);

    /**
     * 修改dish同时修改dishFlavor
     * @param dishDto
     */
    public void updateWithFlavor(DishDto dishDto);
}
