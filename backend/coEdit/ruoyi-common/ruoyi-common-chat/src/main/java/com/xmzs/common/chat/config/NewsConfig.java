package com.xmzs.common.chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 新闻 配置项
 *
 * @author zhuzi
 */
@Component
@ConfigurationProperties(prefix = "news")
public class NewsConfig {

//    private Boolean enabled;
    private static String news_key;

    private static String news_host;

    public static String getNews_key() {
        return news_key;
    }

    public void setNews_key(String news_key) {
        this.news_key = news_key;
    }

    public static String getNews_host() {
        return news_host;
    }

    public void setNews_host(String news_host) {
        this.news_host = news_host;
    }
}
