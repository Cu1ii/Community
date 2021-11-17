package com.cu1.community.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommunityUtil {

    //生成随机的字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * MD5 加密
     * MD5 只能加密不能解密
     * 一般为了防止密码简单 所以都在原密码上添加一个随机的字符串
     * @param key 传入的明文密码
     * @return
     */
    public static String MD5(String key) {
        //如果为空值就直接返回 null 不是的话再进行加密
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));
    }

}
