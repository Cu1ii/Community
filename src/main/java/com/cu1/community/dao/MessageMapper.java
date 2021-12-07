package com.cu1.community.dao;

import com.cu1.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * 查询当前用户的会话列表, 针对每一个会话只返回一条最新的私信
     * @param userId 用户 id
     * @param offset 当前是第几页
     * @param limit 分页大小
     * @return 用户当前页的会话私信
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量
     * @param userId 用户 id
     * @return 当前用户会话数
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     * @param conversationId  会话 id
     * @param offset 当前第几页
     * @param limit 分页大小
     * @return 两用户之间会话的私信列表
     */
    List<Message> selectLetters(String conversationId, int offset, int limit);

    /**
     * 查询某个会话包含的私信数量
     * @param conversationId 会话中私信数量
     * @return 某个会话包含的私信数量
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读私信的数量
     * @param userId 用户 id
     * @param conversationId 会话 id
     */
    int selectLetterUnreadCount(int userId, String conversationId);

}

