package com.cu1.community.controller;

import com.cu1.community.entity.Message;
import com.cu1.community.entity.User;
import com.cu1.community.service.MessageService;
import com.cu1.community.service.UserService;
import com.cu1.community.utils.HostHolder;
import com.cu1.community.utils.PagePaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/letter")
public class MessageController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 处理私信列表
     */
    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public String getLetterList(Model model, PagePaginationUtil page) {
        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> messageList =
                messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        ArrayList<Map<String, Object>> conversations = new ArrayList<>();
        if (messageList != null) {
            for (Message message : messageList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int tragetId = (user.getId() == message.getFromId() ? message.getToId() : message.getFromId());
                map.put("target", userService.findUserById(tragetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        model.addAttribute("page", page);
        //查询未读消息数量
        model.addAttribute("letterUnreadCount",
                messageService.findLetterUnreadCount(user.getId(), null));
        return "site/letter";
    }

    @RequestMapping(path = "/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,
                                  PagePaginationUtil page, Model model) {
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        ArrayList<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("page", page);
        model.addAttribute("target", getLetterTarget(conversationId));
        return "site/letter-detail";
    }

    /**
     * 根据对话编号得到 from_to 用户
     * @param conversationId 会话编号
     * @return from_to 用户
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        for (String id : ids) {
            int parseInt = Integer.parseInt(id);
            if (hostHolder.getUser().getId() != parseInt) {
                return userService.findUserById(parseInt);
            }
        }
        return null;
    }

}
