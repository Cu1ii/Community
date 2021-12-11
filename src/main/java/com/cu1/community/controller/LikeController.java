package com.cu1.community.controller;

import com.cu1.community.entity.User;
import com.cu1.community.service.LikeService;
import com.cu1.community.utils.CommunityUtil;
import com.cu1.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 点赞
     * @param entityType 点赞的实体类型
     * @param entityId 点赞的实体 id
     * @return 带有点赞信息的 map
     */
    @ResponseBody
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    public String like(int entityType, int entityId) {
        User user = hostHolder.getUser();
        //点赞
        likeService.like(user.getId(), entityType, entityId);
        //数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return CommunityUtil.getJSONString(0, null, map);
    }
}
