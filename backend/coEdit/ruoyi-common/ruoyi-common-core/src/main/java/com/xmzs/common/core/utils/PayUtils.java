package com.xmzs.common.core.utils;

import lombok.extern.slf4j.Slf4j;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
/**
 * packageName com.xmzs.common.core.utils
 *
 * @author zhuzi
 * @date 2025/5/12
 * @description TODO
 */
@Slf4j
public class PayUtils {

    public Map<String, String> parseXmlToMap(String xml)  {
        Map<String, String> map = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();

        InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        org.w3c.dom.Document doc = builder.parse(inputStream);
        org.w3c.dom.NodeList nodeList = doc.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Node node = nodeList.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                map.put(node.getNodeName(), node.getTextContent());
            }
        }
        } catch (Exception e) {
            log.error("解析出错:{}",e.getMessage());
            throw new RuntimeException(e);
        }
        return map;
    }
}
