package com.xmzs.common.office;

import cn.hutool.core.io.FileUtil;
import com.xmzs.common.office.utils.OfficeUtils;
import com.xmzs.common.office.xlsx.XssfExcelToHtmlConverter;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.usermodel.Picture;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

/**
 * @ClassName : ExcelToHtml
 * @Description ：
 * @Author : zxy
 * @Date : 2023/10/26 12:09
 * @Version : 1.0
 */
public class ExcelToHtml {
	public static void main(String[] args) throws Exception {
		xls2Html("C:\\v3.xls", "C:\\html\\");
		xls2Html("C:\\v3.xlsx", "C:\\html\\");
	}

	public static void xls2Html(String xlsFilePath, String targetDir) throws Exception {
		File file = OfficeUtils.checkFileExists(xlsFilePath);
		targetDir = OfficeUtils.dealTargetDir(targetDir);
		if (file.getName().endsWith("xlsx")) {
			xlsx2Html(file, targetDir);
		} else if (file.getName().endsWith("xls")) {
			xls2Html(file, targetDir);
		} else {
			throw new RuntimeException("file not xls!");
		}
	}

	public static String xlsx2Html(InputStream xlsFile) throws Exception {
		Document doc = XssfExcelToHtmlConverter.process(xlsFile);
		DOMSource domSource = new DOMSource(doc);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		StreamResult streamResult = new StreamResult(outStream);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "no");
		serializer.setOutputProperty(OutputKeys.METHOD, "html");
		serializer.transform(domSource, streamResult);
		outStream.close();

		String content = new String(outStream.toByteArray());
		content = content.replace("</style>", "tr,td{border: 1px solid;}</style>");
		return content;
	}


	private static void xlsx2Html(File xlsFile, String targetDir) throws Exception {

		Document doc = XssfExcelToHtmlConverter.process(xlsFile);
		DOMSource domSource = new DOMSource(doc);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		StreamResult streamResult = new StreamResult(outStream);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "no");
		serializer.setOutputProperty(OutputKeys.METHOD, "html");
		serializer.transform(domSource, streamResult);
		outStream.close();

		String content = new String(outStream.toByteArray());
		content = content.replace("</style>", "tr,td{border: 1px solid;}</style>");
		String targetFileName = targetDir + xlsFile.getName() + ".html";
		FileUtil.writeUtf8String(content, targetFileName);
	}

	private static void xls2Html(File xlsFile, String targetDir) throws Exception {
		InputStream input = new FileInputStream(xlsFile);
		HSSFWorkbook excelBook = new HSSFWorkbook(input);
		Document newDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(newDocument);
		excelToHtmlConverter.processWorkbook(excelBook);
		List pics = excelBook.getAllPictures();
		if (pics != null) {
			for (Object pic1 : pics) {
				Picture pic = (Picture) pic1;
				try {
					pic.writeImageContent(new FileOutputStream(targetDir + pic.suggestFullFileName()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		Document htmlDocument = excelToHtmlConverter.getDocument();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		DOMSource domSource = new DOMSource(htmlDocument);
		StreamResult streamResult = new StreamResult(outStream);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty(OutputKeys.METHOD, "html");
		serializer.transform(domSource, streamResult);
		outStream.close();

		String content = new String(outStream.toByteArray());
		content = content.replace("</style>", "tr,td{border: 1px solid;}</style>");
		String targetFilePath = targetDir + xlsFile.getName() + ".html";
		FileUtil.writeUtf8String(content, targetFilePath);

	}

	public static String xls2Html(InputStream input) throws Exception {
		HSSFWorkbook excelBook = new HSSFWorkbook(input);
		Document newDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(newDocument);
		excelToHtmlConverter.processWorkbook(excelBook);
		List pics = excelBook.getAllPictures();
		if (pics != null) {
			for (Object pic1 : pics) {
				Picture pic = (Picture) pic1;
				try {
					pic.writeImageContent(new FileOutputStream(pic.suggestFullFileName()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		Document htmlDocument = excelToHtmlConverter.getDocument();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		DOMSource domSource = new DOMSource(htmlDocument);
		StreamResult streamResult = new StreamResult(outStream);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty(OutputKeys.METHOD, "html");
		serializer.transform(domSource, streamResult);
		outStream.close();

		String content = new String(outStream.toByteArray());
		content = content.replace("</style>", "tr,td{border: 1px solid;}</style>");
		return content;
	}
}
