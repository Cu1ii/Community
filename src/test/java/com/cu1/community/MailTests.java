package com.cu1.community;

import com.cu1.community.utils.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.PushBuilder;

@SpringBootTest
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {
        mailClient.sendMail("1178079301@qq.com", "TEST", "welcome");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        //调用模板引擎直接去调用 template 文件下的页面
        context.setVariable("username", "sunday");  
        String content = templateEngine.process("mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("1178079301@qq.com", "TEST", content);

    }

}
