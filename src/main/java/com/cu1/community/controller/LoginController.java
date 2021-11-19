package com.cu1.community.controller;

import com.cu1.community.entity.User;
import com.cu1.community.service.UserService;
import com.cu1.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    UserService userService;

    //设置返回登录页面
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPages() { return "site/register"; }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPages() { return "site/login"; }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        //注册中没有问题 就跳到首页
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功, 我们已经向您的邮箱发送了激活邮件, 请尽快激活");
            model.addAttribute("target", "/index");
            return "site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("user", user);
            return "site/register";
        }
    }

    // http://localhost:8080/community/activation/101(用户 id)/code(激活码)
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        //处理激活结果
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功, 您的账号已经可以正常使用");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作, 该账号已经激活");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败, 您提供的激活码不正确");
            model.addAttribute("target", "/index");
        }
        return "site/operate-result";
    }


}
