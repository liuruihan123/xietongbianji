package com.unfbx.chatgpt;

/**
 * packageName com.unfbx.chatgpt
 *
 * @author zhuzi
 * @date 2024/6/30
 * @description TODO
 */
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

public class DatasetChecker {






    public static void main(String[] args) {
        String dataPath = "C:\\Users\\竹子\\AppData\\Local\\Temp\\finetune202408175662388310231238550.jsonl";

        // 加载数据集
        List<Map<String, Object>> dataset = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(dataPath), StandardCharsets.UTF_8)) {
            String line;
            ObjectMapper mapper = new ObjectMapper();
            while ((line = br.readLine()) != null) {
                System.out.println(line+"~~~");
                Map<String, Object> data = mapper.readValue(line, Map.class);
                dataset.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        String jsonString = "{ \"messages\": [...] }";
//        // Create a JSONObject from the JSON string
//        JSONObject jsonObject = new JSONObject(jsonString);
//
//        // Get the "messages" JSON array from the object
//        JSONArray messages = jsonObject.getJSONArray("messages");
//


        // 初始数据集统计
        System.out.println("Num examples: " + dataset.size());
        System.out.println("First example:");
        List<Map<String, Object>> firstMessages = (List<Map<String, Object>>) dataset.get(0).get("messages");
        for (Map<String, Object> message : firstMessages) {
            System.out.println(message);
        }

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

        if (!formatErrors.isEmpty()) {
            System.out.println("Found errors:");
            for (Map.Entry<String, Integer> entry : formatErrors.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } else {
            System.out.println("No errors found");
        }
    }
}

