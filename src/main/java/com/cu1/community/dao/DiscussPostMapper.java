package com.cu1.community.dao;

import com.cu1.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /*
    * 如果 userId 为空 那么就说明在主页位置去查帖子 如果不为空说明是在用户主页查用户帖子
    * offset 分页时每页开始的行号
    * limit 每一页最多显示多少条数据
    * */
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset")int offset,
                                         @Param("limit")int limit);

    /*
    * 查询行数
    * */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 增加帖子
     * @param discussPost 要增加的帖子
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    /**
     * 修改帖子评论数量
     * @param id 帖子 id
     * @param commentCount 最新评论数量
     * @return 更新的行号
     */
    int updateCommentCount(int id, int commentCount);
}
