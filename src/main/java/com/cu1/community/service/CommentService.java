package com.cu1.community.service;

import com.cu1.community.dao.CommentMapper;
import com.cu1.community.entity.Comment;
import com.cu1.community.utils.CommunityConstant;
import com.cu1.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * 增加评论
     * @param comment 评论
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);
        //更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            //查找帖子下的评论数量 或者 查找回复下面的评论数
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            //修改帖子下评论数量
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

}
