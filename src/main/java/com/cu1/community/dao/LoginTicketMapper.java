package com.cu1.community.dao;

import com.cu1.community.entity.LoginTicket;
import jdk.jfr.Enabled;
import org.apache.ibatis.annotations.*;

@Mapper
//声明不推荐使用这个组件
@Deprecated
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id") //自动生成主键
    public int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //修改凭证状态
    @Update({
            "update login_ticket set status = #{status} where ticket = #{ticket} "
    })
    public int updateStatus(String ticket, int status);

}
