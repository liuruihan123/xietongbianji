package com.xmzs.knowledge.request;

import lombok.Data;

@Data
public class AuthLoginRequest {

    private String userName;

    private String password;
}
