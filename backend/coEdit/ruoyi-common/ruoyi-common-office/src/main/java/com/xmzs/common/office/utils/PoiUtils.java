package com.xmzs.common.office.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PoiUtils {
    /**
     * 创建临时的csv文件
     *
     * @return
     * @throws IOException
     */
    public static File createTempFile(List<String[]> datas, String[] headers) throws IOException {
        File tempFile = File.createTempFile("vehicle", ".csv");
        CsvWriter csvWriter = new CsvWriter(tempFile.getCanonicalPath(), ',', StandardCharsets.UTF_8);
        // 写表头
        csvWriter.writeRecord(headers);
        for (String[] data : datas) {
            //这里如果数据不是String类型，请进行转换
            for (String datum : data) {
                csvWriter.write(datum, true);
            }
            csvWriter.endRecord();
        }
        csvWriter.close();
        return tempFile;
    }


    /**
     * 普通csv文件传浏览器
     *
     * @param response
     * @param tempFile
     * @throws IOException
     */
    public static void outCsvStream(HttpServletResponse response, File tempFile) throws IOException {
        java.io.OutputStream out = response.getOutputStream();
        byte[] b = new byte[10240];
        File fileLoad = new File(tempFile.getCanonicalPath());
        response.reset();
        response.setContentType("application/csv");
        response.setHeader("content-disposition", "attachment; filename=" + URLEncoder.encode("export.csv", "UTF-8"));
        java.io.FileInputStream in = new java.io.FileInputStream(fileLoad);
        int n;
        //为了保证excel打开csv不出现中文乱码
        out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        while ((n = in.read(b)) != -1) {
            //每次写入out1024字节
            out.write(b, 0, n);
        }
        in.close();
        out.close();
    }


    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

}
