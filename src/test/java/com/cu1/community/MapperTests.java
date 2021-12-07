package com.cu1.community;

import com.cu1.community.dao.DiscussPostMapper;
import com.cu1.community.dao.LoginTicketMapper;
import com.cu1.community.dao.MessageMapper;
import com.cu1.community.dao.UserMapper;
import com.cu1.community.entity.DiscussPost;
import com.cu1.community.entity.LoginTicket;
import com.cu1.community.entity.Message;
import com.cu1.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser() {
        System.out.println(userMapper.selectByName("liubei"));
        System.out.println(userMapper.selectById(101));
        System.out.println(userMapper.selectByEmail("nowcoder101@sina.com"));
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("Cu1");
        user.setPassword("35157210");
        user.setSalt("abc");
        user.setEmail("cu1universe@gmail.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        int user1 = userMapper.insertUser(user);
        System.out.println(user1);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateStatus() {
        int status = userMapper.updateStatus(149, 1);
        System.out.println(status);

        int rows = userMapper.updateHeader(149, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(149, "hello");

        System.out.println(rows);
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);

    }

    @Test
    public void testInsertLoginTicket() {

        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(101);
        ticket.setTicket("abc");
        ticket.setStatus(0);
        ticket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(ticket);

    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket ticket = loginTicketMapper.selectByTicket("abc");
        System.out.println(ticket);

        loginTicketMapper.updateStatus("abc", 1);
    }

    @Test
    public void testSelectLetters() {
        List<Message> messageList = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messageList) {
            System.out.println(message.toString());
        }
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> messages = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : messages) {
            System.out.println(message);
        }

        int letterCount = messageMapper.selectLetterCount("111_112");
        System.out.println(letterCount);

        int unreadCount = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(unreadCount);
    }

}
