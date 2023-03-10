package com.eddie.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eddie.reggie.dto.SetmealDto;
import com.eddie.reggie.entity.Setmeal;

import java.util.List;

/**
 @author EddieZhang
 @create 2023-01-02 19:46
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 添加套餐并添加dish
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);


    /**
     * 删除套餐并删除dish
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 根据id查询setmeal同时携带setmealDish
     * @param id
     * @return
     */
    public SetmealDto getByIdWithSetmealDish(Long id);

    /**
     * 修改setmeal同时修改setmealDish
     * @param setmealDto
     */
    public void updateSetmealWishDish(SetmealDto  setmealDto);
}
