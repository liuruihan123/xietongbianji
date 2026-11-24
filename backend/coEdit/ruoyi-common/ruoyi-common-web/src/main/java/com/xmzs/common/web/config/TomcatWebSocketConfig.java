package com.xmzs.common.web.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tomcat WebSocket配置
 * 确保嵌入式Tomcat正确支持WebSocket
 *
 * @author xmzs
 */
@Configuration
public class TomcatWebSocketConfig {

    /**
     * 自定义Tomcat配置以支持WebSocket
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            // 确保WebSocket支持
            factory.addConnectorCustomizers(connector -> {
                connector.setProperty("allowTrace", "true");
                connector.setProperty("maxHttpHeaderSize", "8192");
            });

        };
    }
}
