package com.xmzs.common.office.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExportToCSV {
    public static void main(String[] args) throws SQLException {
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
        try (Connection connection = DriverManager.getConnection(jdbcURL, userName, password)) {
            String sql = " select id, gen_content,gen_catalog,title,keyWords,customId from gpt_topic_content order by customId asc ";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setFetchSize(1000); // 设置每次获取的数据量
            ResultSet result = statement.executeQuery();

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(csvFilePath));

            // 写入列名
            fileWriter.write("Name,Catalogue,SummaryA,PRT Words,ID");

            String[] title = new String[]{"Name", "Catalogue", "SummaryA", "PRT Words", "ID"};
            List<String[]> rows = new ArrayList<>();
            String[] row = null;
            while (result.next()) {
                String name = result.getString("title");
                String catalogue = result.getString("gen_catalog");
                String summaryA = result.getString("gen_content");
                String prtWords = result.getString("keyWords");
                int id = result.getInt("customId");

                row = new String[]{name,catalogue,summaryA,prtWords,String.valueOf(id)};
                rows.add(row);
                File tempFile = PoiUtils.createTempFile(rows, title);
                System.out.println(tempFile.getAbsolutePath()+"----");
//                String line = String.format("\"%s\",%s,%s,%s,%d",
//                        name, catalogue, summaryA, prtWords, id);
//
//                fileWriter.newLine();
//                fileWriter.write(line);
            }

            statement.close();
            fileWriter.close();

        } catch (SQLException e) {
            System.out.println("Database error:");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("File error:");
            e.printStackTrace();
        }
    }
}


