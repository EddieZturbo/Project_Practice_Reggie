package com.eddie.reggie.common;

/**
 * 自定义业务异常类
 @author EddieZhang
 @create 2023-01-02 20:46
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
