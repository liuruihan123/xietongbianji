package com.xmzs.common.office;

import cn.hutool.core.io.FileUtil;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TxtToHtml {

    public static void main(String[] args) throws IOException, ParserConfigurationException {
         String s = "E:\\my_project\\huyifeng\\Interview_app\\文档资料\\2024年6月8日资料\\案例模拟\\测试.txt";
        List<String> title = getTxtFileTextContent(new FileInputStream(new File(s)));
        title.forEach((item)-> {
            System.out.println(item);
            System.out.println("===========");
        });
         //         txtToHtml(s,"C:\\Users\\竹子\\Desktop\\示例数据\\tt.html");
    }

    /**
    获取纯文本内容
     */
    public static List<String> getTxtFileTextContent(InputStream fileStream) throws IOException, ParserConfigurationException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        String text = String.valueOf(stringBuilder);
        String[] parts = text.split("</>");
        List<String> paragraphs =  Arrays.asList(parts);

        return paragraphs;
    }


    public static void txtToHtml(String sourcePath, String path) throws IOException {
        Path filepath = Paths.get(sourcePath);
        List<String> lines = Files.readAllLines(filepath);
        String className = "";
        String html = "";
        String lineSpan = "";
        int i=0;
        for(String lineStr:lines){
            className  ="line"+i;
            lineSpan="<p class=\""+className+"\">"+lineStr+"</p> \n" ;
            html+=lineSpan;
            i++;
        }
//        System.out.println(html);
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.append(html);
        }
    }
}
