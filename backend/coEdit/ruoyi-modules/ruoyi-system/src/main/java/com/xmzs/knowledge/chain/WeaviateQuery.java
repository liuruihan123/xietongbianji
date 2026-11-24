package com.xmzs.knowledge.chain;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeaviateQuery {

    private static final String WEAVIATE_URL = "http://192.168.245.129:6038/v1/graphql"; // 替换为您的 Weaviate 实例 URL

    public static void main(String[] args) {
        String fidValue = "fy8o8v2pz7gb0w5e"; // 替换为您要查询的 fid 值
        String response = queryWeaviate(fidValue);
        System.out.println(response);
    }




    public static String queryWeaviate(String fidValue) {
        try {
            // 创建正确的GraphQL查询
            String graphqlQuery =
                "{ " +
                    "  Get { " +
                    "    LocalKnowledgelitl075b0d(where: { " +
                    "      path: [\\\"fid\\\"], " +
                    "      operator: Equal, " +
                    "      valueString: \\\"" + fidValue + "\\\"" +
                    "    }) { " +
                    "      fid " +
                    "      kid " +
                    "      uuid " +
                    "    } " +
                    "  } " +
                    "}";

            // 创建正确的JSON请求体
            String jsonBody = "{\"query\":\"" + graphqlQuery + "\"}";

            URL url = new URL(WEAVIATE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 打印生成的JSON请求体进行检查
            System.out.println("Generated JSON Body: " + jsonBody);

            // 发送请求
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 检查响应代码
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                System.out.println("Error Response: " + errorResponse.toString());
                return null;
            }

            // 读取响应
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }






}
