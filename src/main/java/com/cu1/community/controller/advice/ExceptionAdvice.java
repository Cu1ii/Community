package com.cu1.community.controller.advice;

import com.cu1.community.utils.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//只扫描带有 Controller 注解的 bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static  final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常" + e.getMessage());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            logger.error(stackTraceElement.toString());
        }

        String xRequestWith = request.getHeader("x-requested-with");
        //如果 xRequestWith 等于 XML... 就说明这是一个异步请求
        if ("XMLHttpRequest".equals(xRequestWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1 ,"服务器异常"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
