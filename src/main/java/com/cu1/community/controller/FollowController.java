package com.cu1.community.controller;

import com.cu1.community.annotation.LoginRequired;
import com.cu1.community.entity.User;
import com.cu1.community.service.FollowService;
import com.cu1.community.service.UserService;
import com.cu1.community.utils.CommunityConstant;
import com.cu1.community.utils.CommunityUtil;
import com.cu1.community.utils.HostHolder;
import com.cu1.community.utils.PagePaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已关注");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String unFollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unFollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已取消关注");
    }

    /**
     * 显示关注列表
     * @param userId 被查看的用户 id
     * @param page 分页工具
     * @param model 后端转给前端的数据
     * @return 被查看用户的关注列表
     */
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, PagePaginationUtil page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(ENTITY_TYPE_USER, userId));
        List<Map<String, Object>> userList =
                followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        model.addAttribute("loginUser", hostHolder.getUser());
        model.addAttribute("page", page);
        return "site/followee";

    }

    /**
     * 显示某用户关注了谁
     * @param userId 被查看的用户 id
     * @param page 分页工具
     * @param model 后端转给前端的数据
     * @return 被查看用户的关注列表
     */
     @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
     public String getFollowers(@PathVariable("userId") int userId, PagePaginationUtil page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));
        List<Map<String, Object>> userList =
                followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        model.addAttribute("page", page);
        return "site/follower";
    }

    /**
     * 判断是否关注过该用户
     */
    private boolean hasFollowed(int userId) {
        //如果没有登录就认为没有登录
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

}
