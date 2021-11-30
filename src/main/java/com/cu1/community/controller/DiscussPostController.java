package com.cu1.community.controller;

import com.cu1.community.entity.DiscussPost;
import com.cu1.community.entity.User;
import com.cu1.community.service.DiscussPostService;
import com.cu1.community.utils.CommunityUtil;
import com.cu1.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    //增加帖子
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {

        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "还未登录");
        }

        DiscussPost post = new DiscussPost();

        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());

        discussPostService.addDiscussPostMapper(post);

        //报错的情况以后统一处理
        return CommunityUtil.getJSONString(0, "发布成功");

    }

}
