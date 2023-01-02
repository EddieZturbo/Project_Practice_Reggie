package com.eddie.reggie.common;

/**
 * //TODO 基于ThreadLocal封装的工具类 用于保存和获取当前登录的ID（储存在session中的）
 * ThreadLocal是基于线程的独立的储存空间 每个线程一份单独隔离
 @author EddieZhang
 @create 2023-01-02 17:06
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 储存id
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取id
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
