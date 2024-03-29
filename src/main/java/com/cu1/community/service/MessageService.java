package com.cu1.community.service;

import com.cu1.community.dao.MessageMapper;
import com.cu1.community.entity.Message;
import com.cu1.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.lang.reflect.Method;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 查询所有会话
     * @param userId 用户 id
     * @param offset 分页页号
     * @param limit 每一页显示上限
     * @return
     */
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    /**
     * 查询当前用户的会话数量
     * @param userId 用户 id
     * @return 当前用户会话数
     */
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * 查询某个会话所包含的私信列表
     * @param conversationId  会话 id
     * @param offset 当前第几页
     * @param limit 分页大小
     * @return 两用户之间会话的私信列表
     */
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    /**
     * 查询某个会话包含的私信数量
     * @param conversationId 会话中私信数量
     * @return 某个会话包含的私信数量
     */
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    /**
     * 查询未读私信的数量
     * @param userId 用户 id
     * @param conversationId 会话 id
     */
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    /**
     * 读取最后一条通知
     * @param userId 要查询的用户 id
     * @param topic 要查询的主题
     * @return 返回查到的通知
     */
    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    /**
     * 读取通知数量
     * @param userId 要查询的用户 id
     * @param topic 要查询的主题
     * @return 返回该主题下的通知数量
     */
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    /**
     * 读取某主题下的通知数量
     * @param userId 要查询的用户 id
     * @param topic 要查询的主题 如果为空则查询所有主题
     * @return 返回未读数量
     */
    public int findNotcieUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    /**
     * 某个主题所包含的通知列表
     */
    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
