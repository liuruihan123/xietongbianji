package com.xmzs.system.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.mail.GlobalMailAccount;
import com.xmzs.common.office.PptToHtml;
import com.xmzs.common.office.TxtToHtml;
import com.xmzs.common.office.pdfdoms.CreatePdfHtml;
import com.xmzs.common.oss.core.OssClient;
import com.xmzs.common.sms.config.SmsConfig;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import com.xmzs.common.office.WordToHtml;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.xmzs.system.util
 *
 * @author zhuzi
 * @date 2024/7/23
 * @description TODO
 */
public class OfficeUtils {



    public static File convertMultiPartToFile(MultipartFile multipartFile) {
        File file = null;
        try {
            // 获取原始文件名
            String originalFileName = multipartFile.getOriginalFilename();
            // 默认扩展名
            String extension = ".tmp";
            // 尝试从原始文件名中获取扩展名
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // 使用原始文件的扩展名创建临时文件
            Path tempFile = Files.createTempFile(null, extension);
            file = tempFile.toFile();

            // 将MultipartFile的内容写入文件
            try (InputStream inputStream = multipartFile.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(file)) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                // 处理文件写入异常
                e.printStackTrace();
            }
        } catch (IOException e) {
            // 处理临时文件创建异常
            e.printStackTrace();
        }
        return file;
    }

    public static List<String> Office2Html(InputStream inputStream, String extName) throws Exception {
//        String htmlPathWidthName = file.getAbsolutePath().replaceAll(extName,"")+"html";
//        System.out.println("格式：{}"+extName+"，转化文件路径名:{}"+htmlPathWidthName+",源文件：{}"+file.getAbsolutePath());
       List<String> content = new ArrayList<String>();
        switch (extName){
//            case "doc":
//                content =  WordToHtml.getDocFileTextContent(inputStream);
//                break;
//            case "pdf":
//                content = CreatePdfHtml.getDocFileTextContent(inputStream);
//                break;
            case "docx":
                content = WordToHtml.getDocxFileTextContent(inputStream);
                break;
            case "txt":
                content = TxtToHtml.getTxtFileTextContent(inputStream);
                break;
            default:
                return null;
        }
        return content;
    }

    /**
     * 读取html内容
     * @param filePath
     * @return
     */
    public static String readHTMLFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
