package com.eddie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eddie.reggie.common.R;
import com.eddie.reggie.dto.DishDto;
import com.eddie.reggie.dto.SetmealDto;
import com.eddie.reggie.entity.*;
import com.eddie.reggie.service.CategoryService;
import com.eddie.reggie.service.DishFlavorService;
import com.eddie.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  菜品管理 dish以及dishFlavor的controller
 @author EddieZhang
 @create 2023-01-03 12:42
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //创建分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPageInfo = new Page<>(page, pageSize);

        //pageInfo的处理
        //创建queryWrapper
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询条件
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //根据更新时间进行降序排序
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo, dishLambdaQueryWrapper);

        List<Dish> pageInfoRecords = pageInfo.getRecords();//使用stream()将pageInfo中的record进行处理
        List<DishDto> list = pageInfoRecords.stream()
                .map((item) -> {
                    DishDto dishdto = new DishDto();
                    Category category = categoryService.getById(item.getCategoryId());
                    if (null != category) {
                        dishdto.setCategoryName(category.getName());
                    }
                    //对象copy
                    BeanUtils.copyProperties(item, dishdto);
                    return dishdto;
                })
                .collect(Collectors.toList());


        //dishDtoPageInfo的处理
        //TODO 对象copy
        BeanUtils.copyProperties(pageInfo, dishDtoPageInfo, "records");//将Dish的pageInfo拷贝给DishDto的pageInfo 并且忽略records字段
        dishDtoPageInfo.setRecords(list);

        return R.success(dishDtoPageInfo);
    }

    /**
     * 根据id查询菜品信息和对应的Flavor信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable("id") Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构造queryWrapper
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(Dish::getStatus,1);//查询status为1 即目前为起售状态的
//        dishLambdaQueryWrapper.like(dish.getName() != null,Dish::getName,dish.getName());//根据name进行查询
//        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        //先根据sort字段进行升序排序再根据updateTime进行降序排序
//        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        //执行查询
//        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
//        return R.success(dishList);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //查询DishFlavor数据
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(dish.getId() != null,DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

        //查询Dish数据
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
        List<DishDto> dishDtoList = dishList.stream()
                .map((item) -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(item,dishDto);
                    dishDto.setFlavors(flavorList);
                    return dishDto;
                })
                .collect(Collectors.toList());
        return R.success(dishDtoList);
    }

}
