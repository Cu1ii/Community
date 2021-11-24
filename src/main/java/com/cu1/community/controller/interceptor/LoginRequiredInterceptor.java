package com.cu1.community.controller.interceptor;

import com.cu1.community.annotation.LoginRequired;
import com.cu1.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    /**
     * 访问该路径时拦截调用
     * @param request
     * @param response
     * @param handler 要拦截的目标
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //静态资源不拦截 如果是方法(该访问路径所属的方法)就拦截
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class); //获取该方法名为 LoginRequired 的注解
            //如果当前访问的路径方法需要登录但是又没有登录就表示无权访问
            if (loginRequired != null && hostHolder.getUser() == null) {
                response.sendRedirect(request.getContextPath() + "/login"); //重定向到登录界面
                return false;
            }
        }

        return true;
    }

}
