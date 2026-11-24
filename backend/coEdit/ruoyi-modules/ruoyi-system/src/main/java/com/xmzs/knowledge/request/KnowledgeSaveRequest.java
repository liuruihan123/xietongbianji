package com.xmzs.knowledge.request;

import lombok.Data;

@Data
public class KnowledgeSaveRequest {

    private Integer id;

    private String kid;

    private Integer typeId;

    private Long uid;

    private String kname;

    private String description;

}
