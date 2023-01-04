package com.eddie.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.eddie.reggie.common.BaseContext;
import com.eddie.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * //TODO 用于验证是否登录的过滤器（权限验证）
 @author EddieZhang
 @create 2023-01-01 21:05
 */
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")//过滤器 指定name 拦截路径
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();//路径匹配器 支持通配符写法

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截到 {} 请求",request.getRequestURI());
        //获取本次请求的uri
        String requestURI = request.getRequestURI();
        String[] urls = new String[]{//不进行拦截的路径
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录
        };
        //判断本次请求是否需要处理
        boolean check = checkUrl(urls, requestURI);
        //如果不需要处理，直接放行
        if(check){
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态 如果已经登录则直接放行
        if(null != request.getSession().getAttribute("employee")){
            //TODO 在filter中将已经登录的账号的id储存至ThreadLocal中
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //判断移动端用户登录状态 如果已经登录则直接放行
        if(null != request.getSession().getAttribute("user")){
            //TODO 在filter中将已经登录的账号的id储存至ThreadLocal中
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        //如果未登录则返回未登录结果 通过输出流的方式响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }


    /**
     * 路径匹配 检测本次请求的是否要被放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean checkUrl(String[] urls,String requestURI){
        for (String url :
                urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
