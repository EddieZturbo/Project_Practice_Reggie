package com.eddie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eddie.reggie.common.R;
import com.eddie.reggie.dto.DishDto;
import com.eddie.reggie.entity.Category;
import com.eddie.reggie.entity.Dish;
import com.eddie.reggie.entity.DishFlavor;
import com.eddie.reggie.service.CategoryService;
import com.eddie.reggie.service.DishFlavorService;
import com.eddie.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        //TODO 当数据发生了变化就要清理redis中的缓存 以保证数据一致性
        //精确清理redis缓存中某一个category下的dish_开头的所有菜品的数据
        Set keys = redisTemplate.keys("dish_" + dishDto.getCategoryId() + "_1");
        redisTemplate.delete(keys);//清除缓存中所有dish_开头的key的数据
        log.info("数据发生了更新 清除redis中缓存的数据以保证数据一致性");

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
    public R<DishDto> getById(@PathVariable("id") Long id) {
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

        //TODO 当数据发生了变化就要清理redis中的缓存 以保证数据一致性
        //精确清理redis缓存中某一个category下的dish_开头的所有菜品的数据
        Set keys = redisTemplate.keys("dish_" + dishDto.getCategoryId() + "_1");
        redisTemplate.delete(keys);//清除缓存中所有dish_开头的key的数据
        log.info("数据发生了更新 清除redis中缓存的数据以保证数据一致性");

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

    /**
     * 获取数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = null;//要返回的对象

        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//动态定义key

        //TODO 先从redis缓存中获取数据 若redis中不存在则查询数据库并将查询到的数据缓存到redis中
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (null != dishDtoList) {//若redis中存在数据则直接返回 否则查询数据库并将查询到的数据写入redis中
            log.info("从redis中查到了数据");
            return R.success(dishDtoList);
        }

        //查询DishFlavor数据
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(dish.getId() != null, DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

        //查询Dish数据
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
        //TODO (三大缓存问题之一)缓存穿透问题解决
        //解决缓存穿透问题 方式:缓存一个空对象
        if(null == dishList){
            redisTemplate.opsForValue().set(key,new DishDto(),60,TimeUnit.SECONDS);//存储在redis 中一个空对象 并设置expire时间为1分钟
            return R.success(dishDtoList);
        }
        dishDtoList = dishList.stream()
                .map((item) -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(item, dishDto);
                    dishDto.setFlavors(flavorList);
                    return dishDto;
                })
                .collect(Collectors.toList());
        //将从数据库中查询到的数据缓存至redis中 并设置缓存数据expire时间为1h
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);
        log.info("将数据缓存到了redis中");
        return R.success(dishDtoList);
    }

}
