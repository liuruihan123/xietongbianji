package com.xmzs.knowledge.request;

import lombok.Data;

@Data
public class ChatSessionSaveRequest {

    private String title;

    private Integer modelId;

    private String sid;

}
