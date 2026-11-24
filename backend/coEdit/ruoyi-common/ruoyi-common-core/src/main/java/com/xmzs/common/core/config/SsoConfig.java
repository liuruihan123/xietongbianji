package com.xmzs.common.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sso")
public class SsoConfig {
    private String validateUrl;
    private String visitTokenUrl;
    private String clientId;
    private String clientSecret;
    private String allOrgUserUrl;
    private String systemId;
}
