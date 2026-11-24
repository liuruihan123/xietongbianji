package com.xmzs.knowledge.chain.vectorizer.openai;

import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;
import com.xmzs.knowledge.chain.llm.OpenAiServiceProxy;
import com.xmzs.knowledge.chain.vectorizer.Vectorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OpenAiVectorization implements Vectorization {

    @Value("${chain.vectorization.openai.model}")
    private String embeddingModel;

    @Autowired
    private OpenAiServiceProxy openAiServiceProxy;

    @Override
    public List<List<Double>> batchVectorization(List<String> chunkList) {
        OpenAiService service = openAiServiceProxy.service();
        EmbeddingRequest embeddingRequest = EmbeddingRequest
                .builder()
                .model(embeddingModel)
                .input(chunkList)
                .build();
//        EmbeddingResult embeddings = service.createEmbeddings(embeddingRequest);
        EmbeddingResult embeddings = null;
        try {
             embeddings = service.createEmbeddings(embeddingRequest);
            List<Embedding> embeddingDataList = embeddings.getData();
            if (embeddingDataList == null || embeddingDataList.isEmpty()) {
                System.err.println("No embeddings data returned.");
            }
            // 继续处理embeddingDataList
        } catch (Exception e) {
            // 记录异常信息
            log.error("向量错误："+e.getMessage());
            e.printStackTrace();
        }



        List<List<Double>> vectorList = new ArrayList<>();
        for (Embedding embeddingData : embeddings.getData()){
            List<Double> vector = embeddingData.getEmbedding();
            vectorList.add(vector);
        }
        return vectorList;
    }

    @Override
    public List<Double> singleVectorization(String chunk) {
        List<String> chunkList = new ArrayList<>();
        chunkList.add(chunk);
        List<List<Double>> vectorList = batchVectorization(chunkList);
        return vectorList.get(0);
    }
}
