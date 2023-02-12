package com.eddie.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eddie.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 @author EddieZhang
 @create 2023-01-04 22:56
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
