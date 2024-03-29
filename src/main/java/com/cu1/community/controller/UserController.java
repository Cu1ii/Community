package com.cu1.community.controller;


import com.cu1.community.annotation.LoginRequired;
import com.cu1.community.entity.User;
import com.cu1.community.service.FollowService;
import com.cu1.community.service.LikeService;
import com.cu1.community.service.UserService;
import com.cu1.community.utils.CommunityConstant;
import com.cu1.community.utils.CommunityUtil;
import com.cu1.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    public String uploadPath;

    @Value("${community.path.domain}")
    public String domain;

    @Value("${server.servlet.context-path}")
    public String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    /**
     * 当前用户
     */
    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() { return "site/setting"; }

    /**
     * 上传的时候表单上传方式必须要为 Post
     * @multipartFile 前端传过来的文件
     */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {

        //文件不存在
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "site/setting";
        }

        //获取传的文件的原始名字
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "site/setting";
        }
        //生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;

        //确定文件存放的路径
        File dest = new File(uploadPath + "/" + filename);

        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败:" + e.getMessage());
            throw new RuntimeException("上传文件失败, 服务器发生异常", e);
        }

        //跟新当前用户的头像的路径 http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        //允许 Web 访问的路径
        String headerUrl = domain + contextPath + "/user" + "/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {

        //找到服务器上存储的路径
        filename = uploadPath + "/" + filename;

        //文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));

        //响应图片
        response.setContentType("image/" + suffix);
        try(
                FileInputStream fileInputStream = new FileInputStream(filename);
                ServletOutputStream outputStream = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {

            logger.error("读取头像失败" + e.getMessage());
        }

    }

    /**
     * 个人主页
     */
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        User loginUser = hostHolder.getUser();
        model.addAttribute("loginUser", loginUser);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        //用户
        model.addAttribute("user", user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        //当前用户是否已经关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "site/profile";
    }

}