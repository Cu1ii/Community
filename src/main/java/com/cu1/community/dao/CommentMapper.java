package com.cu1.community.dao;

import com.cu1.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 根据评论对象的类型进行查询
     * @param entityId  评论对象的 id
     * @param entityType 评论对象的类型
     * @param offset 第几页
     * @param limit 每页最多显示行数
     * @return 评论列表
     */
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 查询数据的条目数
     * @param entityType 评论对象的类型
     * @param entityId 评论对象的 id
     * @return 查询数据的条目数
     */
    int selectCountByEntity(int entityType, int entityId);

}

