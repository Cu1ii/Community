package com.cu1.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.cu1.community.entity.Message;
import com.cu1.community.entity.User;
import com.cu1.community.service.MessageService;
import com.cu1.community.service.UserService;
import com.cu1.community.utils.CommunityConstant;
import com.cu1.community.utils.CommunityUtil;
import com.cu1.community.utils.HostHolder;
import com.cu1.community.utils.PagePaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.security.MessageDigest;
import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 处理私信列表
     */
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
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
        //查询未读通知
        int noticeUnreadCount = messageService.findNotcieUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "site/letter";
    }


    private List<Integer> getLetterIds(List<Message> letterList) {
        ArrayList<Integer> list = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    list.add(message.getId());
                }
            }
        }
        return list;
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
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

        //设置已读
        List<Integer> ids = this.getLetterIds(letterList);

        if (!ids.isEmpty()) {
            for (Integer id : ids) {
                messageService.readMessage(ids);
            }
        }

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
            System.out.println(parseInt);
            if (hostHolder.getUser().getId() != parseInt) {
                return userService.findUserById(parseInt);
            }
        }
        //自己给自己发送的时候, 上面的循环不会 return 所以在下面需要单独返回一个
        int parseInt = Integer.parseInt(ids[0]);
        return userService.findUserById(parseInt);
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
             message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 显示通知列表
     */
    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();
        //查询评论类的通知
        Message latestNotice = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVo = new HashMap<>();
        messageVo.put("message", latestNotice);
        String content = (latestNotice == null ? null : HtmlUtils.htmlUnescape(latestNotice.getContent()));
        Map<String, Object> data = (content == null ? null : JSONObject.parseObject(content, HashMap.class));
        if (data != null) {
            messageVo.put("user", userService.findUserById((Integer)data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));
        }
        int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
        int unreadCount = messageService.findNotcieUnreadCount(user.getId(), TOPIC_COMMENT);
        messageVo.put("count", count);
        messageVo.put("unread", unreadCount);
        model.addAttribute("commentNotice", messageVo);

        //查询点赞类的通知
        latestNotice = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVo = new HashMap<String, Object>();
        messageVo.put("message", latestNotice);
        content = (latestNotice == null ? null : HtmlUtils.htmlUnescape(latestNotice.getContent()));
        data = (content == null ? null : JSONObject.parseObject(content, HashMap.class));
        if (data != null) {
            messageVo.put("user", userService.findUserById((Integer)data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));
        }
        count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
        unreadCount = messageService.findNotcieUnreadCount(user.getId(), TOPIC_LIKE);
        messageVo.put("count", count);
        messageVo.put("unread", unreadCount);
        model.addAttribute("likeNotice", messageVo);

        //查询关注类的通知
        latestNotice = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVo = new HashMap<String, Object>();
        messageVo.put("message", latestNotice);
        content = (latestNotice == null ? null : HtmlUtils.htmlUnescape(latestNotice.getContent()));
        data = (content == null ? null : JSONObject.parseObject(content, HashMap.class));
        if (data != null) {
            messageVo.put("user", userService.findUserById((Integer)data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));
        }
        count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
        unreadCount = messageService.findNotcieUnreadCount(user.getId(), TOPIC_FOLLOW);
        messageVo.put("count", count);
        messageVo.put("unread", unreadCount);
        model.addAttribute("followNotice", messageVo);
        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount" ,letterUnreadCount);
        //未读通知的数量
        int noticeUnreadCount = messageService.findNotcieUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, PagePaginationUtil page, Model model) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        ArrayList<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                //通知
                map.put("notice", notice);
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                //通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);
        model.addAttribute("topic", topic);
        //设置已读
        List<Integer> ids = this.getLetterIds(noticeList);
        model.addAttribute("page", page);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "site/notice-detail";
    }

}
