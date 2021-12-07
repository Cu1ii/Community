package com.cu1.community.service;

import com.cu1.community.dao.MessageMapper;
import com.cu1.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

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
}
