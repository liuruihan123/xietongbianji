package com.xmzs.common.localFile.engine;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThumFilesVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Long size;
    private String ext;
    private String thumbnailUrl;
    private String thumbnailPath;

}
