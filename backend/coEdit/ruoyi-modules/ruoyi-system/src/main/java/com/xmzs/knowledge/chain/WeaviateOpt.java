package com.xmzs.knowledge.chain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * packageName com.mdd.front.plugin.knowledge.chain
 *
 * @author zhuzi
 * @date 2025/2/27
 * @description 主要用于向量数据库的额外操作，比如查询所有的 className 删除等
 */
public class WeaviateOpt {
    private final static String IP = "http://192.168.245.129:6038";
    private static final String WEAVIATE_URL = IP+"/v1/graphql"; // 修改为您的 Weaviate 实例地址



    public static void main(String[] args) {
        // 查询所有类
        System.out.println("查询所有类:");
        queryAllClasses();

//        // 指定删除某个类
//        String classNameToDelete = "YourClassName"; // 替换为您要删除的类名
//        System.out.println("\n删除类: " + classNameToDelete);
//        deleteClass(classNameToDelete);

        // 查询某个类是否存在
//        String classNameToCheck = "LocalKnowledgelitl075b0123d"; // 替换为您要检查的类名
//        System.out.println("\n检查类是否存在: " + classNameToCheck);
//        boolean exists = classExists(classNameToCheck);
//        System.out.println("类 " + classNameToCheck + (exists ? " 存在。" : " 不存在。"));


        //查询类中某个doc以及片段是否存在
//        String queryData = queryWeaviate("LocalKnowledgelitl075b0d", "lfbip029clgtmjqf");
//        System.out.println("Document exists: " + queryData);


        // 批量删除所有类
        System.out.println("\n批量删除所有类:");
        deleteAllClasses();
    }





    public static String queryWeaviate(String classname,String fidValue) {
        try {
            // 创建正确的GraphQL查询
            String graphqlQuery =
                "{ " +
                    "  Get { " +
                    "    "+classname+"(where: { " +
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








    public static void queryAllClasses() {
        try {
            String query = "{ __schema { types { name } } }";
            HttpURLConnection conn = (HttpURLConnection) new URL(WEAVIATE_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            String jsonInputString = "{\"query\": \"" + query + "\"}";
            conn.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // 解析并打印类名
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray types = jsonResponse.getJSONObject("data").getJSONObject("__schema").getJSONArray("types");
            for (int i = 0; i < types.length(); i++) {
                String className = types.getJSONObject(i).getString("name");
                if (!className.startsWith("__")) { // 排除系统类
                    System.out.println("Class: " + className);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean classExists(String className) {
        try {
            // 使用 __type 查询来检查类是否存在
            String query = "{ __type(name: \"" + className + "\") { name } }";
            System.out.println("GraphQL Query: " + query); // 打印查询以调试

            HttpURLConnection conn = (HttpURLConnection) new URL(WEAVIATE_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 转义双引号
            String jsonInputString = "{\"query\": \"" + query.replace("\"", "\\\"") + "\"}";
            System.out.println("Request Body: " + jsonInputString); // 打印请求体以调试
            conn.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode); // 打印响应码以调试
            if (responseCode != 200) {
                // 打印错误响应
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine.trim());
                }
                System.out.println("Error Response: " + errorResponse.toString());
                return false; // 请求失败，返回 false
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // 解析响应，检查类是否存在
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONObject("data").optJSONObject("__type") != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




    public static void deleteClass(String className) {
        try {
            String url = IP+"/v1/schema/" + className; // 修改为您的 Weaviate 实例地址
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("DELETE");

            int responseCode = conn.getResponseCode();
            if (responseCode == 204) {
                System.out.println("Class " + className + " deleted successfully.");
            } else {
                System.out.println("Failed to delete class " + className + ". Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllClasses() {
        try {
            // 查询所有类以获取类名
            String query = "{ __schema { types { name } } }";
            HttpURLConnection conn = (HttpURLConnection) new URL(WEAVIATE_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            String jsonInputString = "{\"query\": \"" + query + "\"}";
            conn.getOutputStream().write(jsonInputString.getBytes("UTF-8"));



            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // 解析类名并删除
            JSONObject jsonResponse = new JSONObject(response.toString());
//            JSONArray types = jsonResponse.getJSONObject("__schema").getJSONArray("types");
            JSONArray types = jsonResponse.getJSONObject("data").getJSONObject("__schema").getJSONArray("types");
            for (int i = 0; i < types.length(); i++) {
                String className = types.getJSONObject(i).getString("name");
                if (!className.startsWith("__")) { // 排除系统类
                    deleteClass(className);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
