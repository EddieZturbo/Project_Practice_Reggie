package com.eddie.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eddie.reggie.common.R;
import com.eddie.reggie.entity.Category;
import com.eddie.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类管理
 @author EddieZhang
 @create 2023-01-02 17:39
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 保存菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        if (null != category){
            categoryService.save(category);
            return R.success("保存菜品分类成功");
        }else{
            return R.error("保存菜品分类失败 数据为null");
        }
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //构造page
        Page pageInfo = new Page(page,pageSize);
        //构造查询wrapper
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getSort);//根据sort字段进行降序排序
        //进行分页查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除菜品分类 并且需要判断是否有关联dish或者setmeal 若有则抛异常 不进行删除
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    /**
     * 更新分类菜单信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("更新分类信息成功");
    }
}
