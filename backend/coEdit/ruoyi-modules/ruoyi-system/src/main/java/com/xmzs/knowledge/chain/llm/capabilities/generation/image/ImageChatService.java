package com.xmzs.knowledge.chain.llm.capabilities.generation.image;

import java.util.List;

public interface ImageChatService {

    List<String> createImage(String prompt);

    List<String> editImage(String content, List<String> httpUrls);
}
