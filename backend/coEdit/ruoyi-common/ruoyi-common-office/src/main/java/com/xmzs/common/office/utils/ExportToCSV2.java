package com.xmzs.common.office.utils;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;

public class ExportToCSV2 {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/chat-comparer?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&allowMultiQueries=true";
        String userName = "root";
        String password = "123456";

        String csvFilePath = "D:/yourfile.csv";
        try {
            // 加载MySQL JDBC驱动
            Class.forName( "com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found");
            e.printStackTrace();
            return;
        }

        try (
                Connection conn = DriverManager.getConnection(jdbcURL, userName, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM gpt_spider_article_content"); // 替换为你的查询语句
                FileWriter writer = new FileWriter(new File(csvFilePath))
        ) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // 写入CSV文件的表头
            for (int i = 1; i <= columnCount; i++) {
                writer.append("\"").append(rsmd.getColumnName(i)).append("\"");
                if (i != columnCount) writer.append(',');
            }
            writer.append('\n');

            // 写入CSV文件的数据
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String output = rs.getString(i);
                    output = "\"" + output + "\""; // 无论数据是什么，都将其包裹在双引号中
                    writer.append(output);
                    if (i != columnCount) writer.append(',');
                }
                writer.append('\n');
            }

            System.out.println("CSV file created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



