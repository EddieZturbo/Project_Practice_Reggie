package com.eddie.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eddie.reggie.entity.Category;

/**
 @author EddieZhang
 @create 2023-01-02 17:36
 */
public interface CategoryService extends IService<Category>{

    public void remove(Long id);
}
