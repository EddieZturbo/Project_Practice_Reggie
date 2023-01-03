package com.eddie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eddie.reggie.common.R;
import com.eddie.reggie.dto.SetmealDto;
import com.eddie.reggie.entity.Category;
import com.eddie.reggie.entity.Setmeal;
import com.eddie.reggie.service.CategoryService;
import com.eddie.reggie.service.SetmealDishService;
import com.eddie.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 @author EddieZhang
 @create 2023-01-03 18:12
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐操作成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造page构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPageInfo = new Page<>(page, pageSize);

        //处理Setmeal的pageInfo
        //构建queryWrapper构造器
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        setmealLambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        //根据updateTime进行降序排序
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //进行分页查询
        setmealService.page(pageInfo, setmealLambdaQueryWrapper);

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream()
                .map((item) -> {
                    SetmealDto setmealDto = new SetmealDto();
                    Category category = categoryService.getById(item.getCategoryId());
                    if (category != null) {
                        setmealDto.setCategoryName(category.getName());
                    }
                    BeanUtils.copyProperties(item, setmealDto);
                    return setmealDto;
                })
                .collect(Collectors.toList());

        //处理SetmealDto的pageInfo
        //进行对象copy 并且忽略掉records字段(不进行records字段的cp)
        BeanUtils.copyProperties(pageInfo, setmealDtoPageInfo, "records");
        setmealDtoPageInfo.setRecords(list);

        //将SetmealDto的pageInfo返回
        return R.success(setmealDtoPageInfo);
    }


    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestBody List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

}
