package com.eddie.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eddie.reggie.common.CustomException;
import com.eddie.reggie.dto.SetmealDto;
import com.eddie.reggie.entity.Setmeal;
import com.eddie.reggie.entity.SetmealDish;
import com.eddie.reggie.mapper.SetmealMapper;
import com.eddie.reggie.service.SetmealDishService;
import com.eddie.reggie.service.SetmealService;
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
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 添加套餐并添加dish
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //操作setmeal表数据
        this.save(setmealDto);

        //操作setmealDish表数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //使用stream()流的方式给setmealDishes中的setmealId字段赋值
        setmealDishes.stream()
                .map((item) -> {
                    item.setSetmealId(setmealDto.getId());
                    return item;
                })
                .collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐并删除dish
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态 判断是否可以删除 若不能删除则抛异常
        //select count(*) from setmeal where ids in (*,*,*) and status = 1;(若count > 0 则表明存在不能删除状态的套餐)
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(ids != null, Setmeal::getId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(setmealLambdaQueryWrapper);
        if (count > 0) {//若count > 0 则不能进行删除 抛异常
            throw new CustomException("未下架的套餐 不能进行删除哦");
        }

        //删除setmeal表中的数据
        this.removeByIds(ids);

        //删除setmealDish表中的数据
        //delete from setmeal_dish where setmeal_id in (*,*,*);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(ids != null, SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

    }

    /**
     * 根据id查询setmeal同时携带setmealDish
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealDto getByIdWithSetmealDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();

        //查询setmeal表数据
        Setmeal setmeal = this.getById(id);

        //查询setmealDish表数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);

        //将数据封装至setmealDto对象中
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    /**
     * 修改setmeal同时修改setmealDish
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateSetmealWishDish(SetmealDto setmealDto) {
        //修改setmeal数据
        this.updateById(setmealDto);

        //由于setmealDish同时存在修改和删除的情况 因此先将setmealDish的数据清空 再进行重新保存
        LambdaQueryWrapper<SetmealDish> setmealDtoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDtoLambdaQueryWrapper.in(null != setmealDto.getId(), SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDtoLambdaQueryWrapper);//清空
        //保存
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list.stream()//由于未进行setmealId字段的赋值 因此使用Stream()流的形式进行赋值
                .map((item) -> {
                    item.setSetmealId(setmealDto.getId());
                    return item;
                })
                .collect(Collectors.toList());
        setmealDishService.saveBatch(list);
    }
}
