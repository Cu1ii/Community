package com.cu1.community.service;

import com.cu1.community.dao.UserMapper;
import com.cu1.community.entity.User;
import com.cu1.community.utils.CommunityUtil;
import com.cu1.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    //域名
    @Value("${community.path.domain}")
    private String domain;

    //项目路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        HashMap<String, Object> map = new HashMap<>();
        //对空值进行判断处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        //可以在前端设置不允许表单提交空值
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        //验证账号是不是存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在");
            return map;
        }
        //判断邮箱是不是存在
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }
        //注册用户 给用户一个随机序号 并将原密码覆盖为 MD5 加密后的密码
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.MD5(user.getPassword()));
        //注册用户默认为普通用户
        user.setType(0);
        //初始用户的状态为未激活
        user.setStatus(0);
        //获取激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        //给用户设置随机头像
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        //设置注册时间
        user.setCreateTime(new Date());

        userMapper.insertUser(user);

        //给用户发激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 设置以什么路径发送邮件
        // http://localhost:8080/community/activation/101(用户 id)/code(激活码)
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content); //发送邮件

        return map;
    }


}
