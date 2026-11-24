package com.xmzs.knowledge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomChatMessage {

    private String content;

    private String sessionId;

}
