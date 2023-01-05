package com.eddie.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eddie.reggie.entity.AddressBook;
import com.eddie.reggie.mapper.AddressBookMapper;
import com.eddie.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 @author EddieZhang
 @create 2023-01-04 22:57
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
