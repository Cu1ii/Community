package com.cu1.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //前缀树节点
    private class TrieNode {

        //描述的是关键词结束表识
        private boolean isKeywordEnd = false;

        //子节点(key 是下级字符, value 是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c) { return subNodes.get(c); }
    }

    //根节点
    private TrieNode rootNode = new TrieNode();

    //当容器调用这个对象的构造器之后会被自动调用
    @PostConstruct
    public void init() {
        try (
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //转字节流
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String keyword;
            //读取敏感词
            while ((keyword = reader.readLine()) != null) {
                //添加到前缀树
                this.insertKeyword(keyword);
            }

        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }

    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) { return null; }
        //指针1
        TrieNode tempTrieNode = rootNode;
        //指针2, 3
        int begin = 0;
        int end = 0;
        //过滤后的文本
        StringBuilder builder = new StringBuilder();
        while (end < text.length()) {
            char c = text.charAt(end);
            //跳过符号
            if (isSymbol(c)) {
                //若指针 1 处于根节点
                if (tempTrieNode == rootNode) {
                    builder.append(c);
                    begin++;
                }
                end++;
                continue;
            }
            //检查子节点
            if ((tempTrieNode = tempTrieNode.getSubNode(c)) == null) {
                //以 begin 开头的字符串不是敏感词
                builder.append(text.charAt(begin));
                //进入下一个位置
                end = (++begin);
                tempTrieNode = rootNode;
            } else if (tempTrieNode.isKeywordEnd()) {
                //发现到敏感词需要将 begin 到 end 范围的字符替换掉
                builder.append(REPLACEMENT);
                //进入下一个位置
                begin = (++end);
                //重新指向根节点
                tempTrieNode = rootNode;
            } else {
                //检查下一个字符
                end++;
            }
        }
        //将最后一批字符计入结果
        builder.append(text.substring(begin));
        return builder.toString();
    }

    //判断是否为特殊符号
    private boolean isSymbol(Character c) {
        //把东亚的文字范围内不作为特殊符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //将一个敏感词添加到前缀树
    private void insertKeyword(String keyword) {
        TrieNode tempTrieNode = this.rootNode;
        for (int i = 0;  i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempTrieNode.getSubNode(c);
            if (subNode == null)  {
                //初始化子节点
                subNode = new TrieNode();
                tempTrieNode.addSubNode(c, subNode);
            }
            //让指针指向子节点
            tempTrieNode = subNode;
            if (i == keyword.length() - 1) {
                //设置结束标识
                tempTrieNode.setKeywordEnd(true);
            }
        }
    }


}
