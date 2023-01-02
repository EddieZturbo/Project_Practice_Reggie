package com.eddie.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * //TODO 全局异常处理 底层基于spring AOP 代理形式实现 try-catch-finally
 @author EddieZhang
 @create 2023-01-02 11:52
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//指明要负责处理那些注解类下的controller中的异常
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 全局处理SQL异常
     * @param exception
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//指明要负责处理的异常类
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        //根据异常信息进行判断 并将异常原因反馈给前台页面
        if (exception.getMessage().contains("Duplicate entry")) {//判断异常信息中是否包含Duplicate entry
            //Duplicate entry 'Eddie' for key 'employee.idx_username'
            String[] split = exception.getMessage().split(" ");//将异常信息按照空格进行分割
            String exString = "添加的 " + split[2] + " 重复了";
            return R.error(exString);
        } else {//若是其他未知错误则 返回系统繁忙
            return R.error("系统繁忙请稍后再试");
        }
    }

    /**
     * 全局处理自定义异常
     * @param exception
     * @return
     */
    @ExceptionHandler(CustomException.class)//指明要负责处理的异常类
    public R<String> exceptionHandler(CustomException exception) {
        log.error(exception.getMessage());
        return R.error(exception.getMessage());
    }
}
