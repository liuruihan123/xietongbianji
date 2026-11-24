package com.xmzs.system.service.impl;

/**
 *
 * @author zhuzi
 * @date 2024/6/30
 * @description TODO
 */

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DatasetChecker {


    public String getFineTuneFile(String jsonContent){
        // 加载数据集
        List<Map<String, Object>> dataset = new ArrayList<>();
        String datastr[] = jsonContent.split("</>");
        for(String json:datastr){
            ObjectMapper mapper = new ObjectMapper();
            try {
                // 将JSON字符串转换为Map
                Map<String, Object> data = mapper.readValue(
                    json,
                    new TypeReference<>() {
                    }
                );
                dataset.add(data);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }



        // 初始数据集统计
        System.out.println("Num examples: " + dataset.size());
        System.out.println("First example:");
//        List<Map<String, Object>> firstMessages = (List<Map<String, Object>>) dataset.get(0).get("messages");
//        for (Map<String, Object> message : firstMessages) {
//            System.out.println(message);
//        }

        // 格式错误检查
        Map<String, Integer> formatErrors = new HashMap<>();

        for (Map<String, Object> ex : dataset) {
            if (!(ex instanceof Map)) {
                formatErrors.merge("data_type", 1, Integer::sum);
                continue;
            }

            List<Map<String, Object>> messages = (List<Map<String, Object>>) ex.get("messages");
            if (messages == null || messages.isEmpty()) {
                formatErrors.merge("missing_messages_list", 1, Integer::sum);
                continue;
            }

            for (Map<String, Object> message : messages) {
                if (!message.containsKey("role") || !message.containsKey("content")) {
                    formatErrors.merge("message_missing_key", 1, Integer::sum);
                }

                Set<String> validKeys = new HashSet<>(Arrays.asList("role", "content", "name", "function_call", "weight"));
                for (String key : message.keySet()) {
                    if (!validKeys.contains(key)) {
                        formatErrors.merge("message_unrecognized_key", 1, Integer::sum);
                        break;
                    }
                }

                String role = (String) message.get("role");
                if (role == null || !Arrays.asList("system", "user", "assistant", "function").contains(role)) {
                    formatErrors.merge("unrecognized_role", 1, Integer::sum);
                }

                String content = (String) message.get("content");
                Object functionCall = message.get("function_call");

                if ((content == null && functionCall == null) || (content != null && !(content instanceof String))) {
                    formatErrors.merge("missing_content", 1, Integer::sum);
                }
            }

            boolean hasAssistantMessage = messages.stream().anyMatch(m -> "assistant".equals(m.get("role")));
            if (!hasAssistantMessage) {
                formatErrors.merge("example_missing_assistant_message", 1, Integer::sum);
            }
        }

        String errorMsg = "";
        if (!formatErrors.isEmpty()) {
            System.out.println("Found errors:");
            for (Map.Entry<String, Integer> entry : formatErrors.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
                errorMsg += entry.getKey() + ": " + entry.getValue();
            }
        } else {
            System.out.println("No errors found");
        }

        return errorMsg;
    }
}

