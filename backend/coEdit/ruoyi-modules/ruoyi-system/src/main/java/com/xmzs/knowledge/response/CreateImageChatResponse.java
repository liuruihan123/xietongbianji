package com.xmzs.knowledge.response;

import com.xmzs.knowledge.domain.MediaFile;
import lombok.Data;

import java.util.List;

@Data
public class CreateImageChatResponse {

    private String textMsg;

    private List<MediaFile> mediaFileList;
}
