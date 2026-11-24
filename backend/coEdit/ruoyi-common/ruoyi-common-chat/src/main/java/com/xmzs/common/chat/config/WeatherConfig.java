package com.xmzs.common.chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 天气 配置项
 *
 * @author zhuzi
 */
@Component
@ConfigurationProperties(prefix = "weather")
public class WeatherConfig {

//    private Boolean enabled;
    private static String weath_key;

    private static String weath_host;

    public static String getWeath_key() {
        return weath_key;
    }

    public  void setWeath_key(String weath_key) {
        WeatherConfig.weath_key = weath_key;
    }

    public static String getWeath_host() {
        return weath_host;
    }

    public  void setWeath_host(String weath_host) {
        WeatherConfig.weath_host = weath_host;
    }
}
