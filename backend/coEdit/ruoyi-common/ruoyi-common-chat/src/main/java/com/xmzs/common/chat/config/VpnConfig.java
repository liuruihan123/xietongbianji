package com.xmzs.common.chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 主要是一些插件本地访问
 *
 * @author zhuzi
 */
@Component
@ConfigurationProperties(prefix = "vpn")
public class VpnConfig {

    private static Boolean enabled;

    private static String host;

    private static String port;

    public static Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public static String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public static String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
