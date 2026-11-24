package com.xmzs.common.office;

import cn.hutool.core.codec.Base64;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlCursor;

/**
 * @ClassName : WordToHtml
 * @Description ：
 * @Author : zxy
 * @Date : 2023/10/26 11:38
 * @Version : 1.0
 */
public class WordToHtml {


    public static void main(String[] args) throws Exception {

        String filepath = "E:\\my_project\\huyifeng\\Interview_app\\文档资料\\2024年6月8日资料\\案例模拟\\市场大小估算相关_案例_20240608.docx";
//        String htmlp = "E:\\my_project\\huyifeng\\Interview_app\\文档资料\\软件设计说明书.html";
//        convertDocxFileToHtml(filepath,htmlp);
        List<String> content = getDocxFileTextContent(new FileInputStream(new File(filepath)));
        for(String li:content){
            System.out.println(li);
            System.out.println("---");
        }
    }

    public static void convertDocxFileToHtml(String fielPath, String htmlPath) throws Exception {
        convertDocxFileToHtml(new File(fielPath), htmlPath);
    }

    public static void convertDocxFileToHtml(File fielPath, String htmlPath) throws Exception {
        convertDocxFileToHtml(new FileInputStream(fielPath), htmlPath);
    }

    /**
     * 将 docx 转成 html
     *
     * @throws Exception
     */
    public static void convertDocxFileToHtml(InputStream fielPath, String htmlPath) throws Exception {
        //创建操作word的对象
        XWPFDocument document = new XWPFDocument(fielPath);
        XHTMLOptions options = XHTMLOptions.create();
        options.setIgnoreStylesIfUnused(false);
        options.setFragment(true);

        //图片用base64转化
        options.setImageManager(new Base64ImgManager());
        OutputStream stream = new FileOutputStream(htmlPath);
        //转化成HTML
        XHTMLConverter.getInstance().convert(document, stream, options);
    }

    /**
     * 获取纯文本内容
     * @param fileStream
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     */
//    public static String getDocxFileTextContent(InputStream fileStream) throws IOException, ParserConfigurationException {
//        XWPFDocument document = new XWPFDocument(fileStream);
//        String documentText = "";
//        StringBuilder sb = new StringBuilder();
//        for (XWPFParagraph paragraph : document.getParagraphs()) {
//            sb.append(paragraph.getText()).append("\n");
//        }
//        for (XWPFTable table : document.getTables()) {
//            for (XWPFTableRow row : table.getRows()) {
//                for (XWPFTableCell cell : row.getTableCells()) {
//                    sb.append(cell.getText()).append("\t");
//                }
//                sb.append("\n");
//            }
//            sb.append("\n");
//        }
////        document.close();
//         documentText = sb.toString();
//        documentText  =documentText.replace("\r", "。");
//        return documentText;
//    }


    /**
     * 获取文本内容
     * @param fileStream
     * @return
     * @throws IOException
     */
    public static List<String> getDocxFileTextContent(InputStream fileStream) throws IOException {
        XWPFDocument document = new XWPFDocument(fileStream);
        List<String> graphList = new ArrayList<>();

        List<Map<String,Object>> docGraphList =new ArrayList<Map<String,Object>>();


        StringBuilder sb = new StringBuilder();
        boolean newSection = false;

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String title = isHeading2(paragraph);
            if (!StringUtils.isEmpty(title)) {
                // 当遇到新的标题二时，如果StringBuilder不为空，则添加到列表中
                if (sb.length() > 0) {
                    graphList.add(sb.toString());
                    sb.setLength(0); // 重置StringBuilder
                }
                newSection = true;
            }
            // 如果是新段落，不添加换行符在最前面
            if (newSection) {
                sb.append(paragraph.getText());
                newSection = false;
            } else {
                sb.append("\n").append(paragraph.getText());
            }
        }
        // 添加最后一段内容（如果存在）到列表中
        if (sb.length() > 0) {
            graphList.add(sb.toString());
        }

        // 处理表格内容
        for (XWPFTable table : document.getTables()) {
            sb.setLength(0); // 对于表格，从新的StringBuilder开始
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    sb.append(cell.getText()).append("\t");
                }
                sb.append("\n");
            }
            // 将表格内容添加到列表中
            graphList.add(sb.toString());
            sb.setLength(0); // 重置StringBuilder
        }

        return graphList;
    }


    /**
     * 输出标题和内容 分开输出
     * @param fileStream
     * @return
     * @throws IOException
     */
    public static List<Map<String, Object>> getDocxInfo(InputStream fileStream) throws IOException {
        XWPFDocument document = new XWPFDocument(fileStream);
        List<Map<String, Object>> docGraphList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        String currentTitle = null;

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String title = isHeading2(paragraph);
            if (!StringUtils.isEmpty(title)) {
                // 如果当前有标题，且内容不为空，则将之前的标题和内容添加到列表中
                if (currentTitle != null && sb.length() > 0) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", currentTitle);
                    map.put("content", sb.toString());
                    docGraphList.add(map);
                    sb.setLength(0); // 重置StringBuilder
                }
                currentTitle = title; // 更新当前标题
            }
            // 将段落内容添加到StringBuilder中
            if (sb.length() > 0) {
                sb.append("\n").append(paragraph.getText());
            } else {
                sb.append(paragraph.getText());
            }
        }
        // 添加最后一段内容（如果存在）到列表中
        if (currentTitle != null && sb.length() > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", currentTitle);
            map.put("content", sb.toString());
            docGraphList.add(map);
        }

        // 处理表格内容
        for (XWPFTable table : document.getTables()) {
            sb.setLength(0); // 对于表格，从新的StringBuilder开始
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    sb.append(cell.getText()).append("\t");
                }
                sb.append("\n");
            }
            // 将表格内容添加到列表中
            if (currentTitle != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("title", currentTitle);
                map.put("content", sb.toString());
                docGraphList.add(map);
            }
            sb.setLength(0); // 重置StringBuilder
        }

        return docGraphList;
    }

    private static String isHeading2(XWPFParagraph paragraph) {
        // 检查段落是否为标题二样式
//        System.out.println(paragraph.getStyle()+"~~~");
        if (paragraph.getStyle() != null) {
            // 根据Word中的样式ID判断，通常“标题二”的样式ID为"2"
            System.out.println("测试标题：：："+paragraph.getText());
            if(paragraph.getStyle().equals("2")||paragraph.getStyle().equals("Heading2")){
                return paragraph.getText();
            }else{
                return null;
            }

        }
        return null;
    }


    public static void convertDocFileToHtml(String fielPath, String htmlPath) throws Exception {
        convertDocFileToHtml(new File(fielPath), htmlPath);
    }

    public static void convertDocFileToHtml(File fielPath, String htmlPath) throws Exception {
        convertDocFileToHtml(new FileInputStream(fielPath), htmlPath);
    }




    /**
     * 获取纯文本
     * @param fileStream
     * @return
     * @throws IOException
     */
    public static String getDocFileTextContent(InputStream fileStream) throws IOException, ParserConfigurationException {
        HWPFDocument wordDocument = (HWPFDocument) WordToHtmlUtils.loadDoc(fileStream);
        String tcontent = String.valueOf(wordDocument.getText());
        tcontent  =tcontent.replace("\r", "。");
        return tcontent.toString();
    }

    /**
     * 将 doc 转成 html
     * @param fielPath
     * @param htmlPath
     * @throws Exception
     */
    public static void convertDocFileToHtml(InputStream fielPath, String htmlPath) throws Exception {
        //ps:当inputStream!=null，而生成wordDocument报错，请检查文档是否用office word保存的
        HWPFDocument wordDocument = (HWPFDocument) WordToHtmlUtils.loadDoc(fielPath);

        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        //将图片转成base64的格式
        PicturesManager pictureRunMapper = (bytes, pictureType, s, v, v1) -> Base64ImgManager.EMBED_IMG_SRC_PREFIX + Base64.encode(bytes);
        wordToHtmlConverter.setPicturesManager(pictureRunMapper);
        //解析word文档
        wordToHtmlConverter.processDocument(wordDocument);
        Document htmlDocument = wordToHtmlConverter.getDocument();
        DOMSource domSource = new DOMSource(htmlDocument);
        OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(htmlPath), "utf-8");

        StreamResult streamResult = new StreamResult(outputStream);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer serializer = factory.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
    }

}
