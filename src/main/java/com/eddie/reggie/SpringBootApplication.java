package com.eddie.reggie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 @author EddieZhang
 @create 2023-01-01 16:18
 */
@org.springframework.boot.autoconfigure.SpringBootApplication
@Slf4j
@MapperScan("com.eddie.reggie.mapper")
@ServletComponentScan//TODO 开启Servlet组件扫描 用来扫描@WebFilter的filter
public class SpringBootApplication {
    public static void main (String [] args){
        SpringApplication.run(SpringBootApplication.class,args);
    }
}
