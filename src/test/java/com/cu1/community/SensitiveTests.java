package com.cu1.community;


import com.cu1.community.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTests {

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以嫖娼和赌|博以及***的外||围, 还可以贩冰冰";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }

}
