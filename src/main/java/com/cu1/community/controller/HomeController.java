package com.cu1.community.controller;

import com.cu1.community.Utils.PagePaginationUtils;
import com.cu1.community.entity.DiscussPost;
import com.cu1.community.entity.User;
import com.cu1.community.service.DiscussPostService;
import com.cu1.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = {"/index", "/"}, method = RequestMethod.GET)
    public String getIndexPage(Model model, PagePaginationUtils page) {

        //方法调用之前 SpringMVC 会自动实例化后 model 和 page 并将 page 注入 model
        //但是 但是 在实际应用中会出现 thymeleaf 取不出值的情况 所以一定要加上!!!!!!
        //所以在 thymeleaf 中可以直接访问 page 对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        //访问主页的时候要把所有帖子都查出并返回给前端
        List<DiscussPost> lists =
                discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object> > discussPosts = new ArrayList<>();
        if (lists != null) {
            for (DiscussPost discussPost : lists) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                //根据帖子信息中的 userId 找到用户
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("page", page);
        return "index";
    }
}
