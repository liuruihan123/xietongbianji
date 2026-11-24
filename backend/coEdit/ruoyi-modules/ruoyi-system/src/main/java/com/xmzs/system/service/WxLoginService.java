package com.xmzs.system.service;

import com.xmzs.common.core.domain.model.VisitorLoginBody;
import com.xmzs.common.core.enums.LoginUserType;
import com.xmzs.system.config.base.BaseConfig;
import com.xmzs.system.domain.vo.LoginVo;
import com.xmzs.system.request.WxLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.xmzs.system.service
 *
 * @author zhuzi
 * @date 2025/3/9
 * @description TODO
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class WxLoginService {

    private final RestTemplate restTemplate;

    private final BaseConfig baseConfig;

    private final SysLoginService loginService;

    public LoginVo handleWxLogin(WxLoginRequest request) {
        Map<String,String> miniConfigMap =  baseConfig.get("miniApp");

        // 1. 获取access_token
        String accessToken = getAccessToken(miniConfigMap);

        // 2. 获取手机号
        String phoneNumber = getPhoneNumber(accessToken, request.getPhoneCode());

        // 3. 执行登录
        VisitorLoginBody loginBody = new VisitorLoginBody();
        loginBody.setCode(request.getCode());
        loginBody.setType(LoginUserType.XCX.getCode());
        loginBody.setPhoneNumer(phoneNumber);
        loginBody.setInviteCode(request.getInviteCode());
       return  loginService.visitorLogin(loginBody);
//        return doLogin(phoneNumber, request.getType());
    }

    private String getAccessToken(Map<String,String> miniConfigMap) {
        String url = "https://api.weixin.qq.com/cgi-bin/stable_token";

        Map<String, String> params = new HashMap<>();
        params.put("appid", miniConfigMap.get("appstarid"));
        params.put("secret", miniConfigMap.get("stark"));
        params.put("grant_type", "client_credential");

        ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        }

        throw new RuntimeException("获取access_token失败");
    }

    private String getPhoneNumber(String accessToken, String code) {
        String url = String.format("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s", accessToken);

        Map<String, String> params = new HashMap<>();
        params.put("code", code);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> phoneInfo = (Map<String, Object>) response.getBody().get("phone_info");
            return (String) phoneInfo.get("purePhoneNumber");
        }

        throw new RuntimeException("获取手机号失败");
    }

}
