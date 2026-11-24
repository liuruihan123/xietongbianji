package com.xmzs.knowledge.request;

import lombok.Data;

@Data
public class CompletionKeywordRequest {

    private String content;

    private String keywordArea;

    private Integer keywordNum = 3;

}
