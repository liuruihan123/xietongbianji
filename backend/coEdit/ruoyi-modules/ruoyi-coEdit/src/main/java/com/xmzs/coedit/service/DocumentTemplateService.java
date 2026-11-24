package com.xmzs.coedit.service;

import cn.hutool.core.io.FileUtil;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.common.core.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文档模板服务
 * 用于生成空白文档模板
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentTemplateService {

    private final OnlyOfficeConfig onlyOfficeConfig;

    /**
     * 创建Word文档模板
     *
     * @param filePath 文件路径
     * @param title 文档标题
     * @throws IOException IO异常
     */
    public void createWordTemplate(String filePath, String title) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // 添加标题段落
        XWPFParagraph titleParagraph = document.createParagraph();
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        titleRun.setFontFamily("宋体");

        // 添加空白段落
        document.createParagraph();

        // 保存文档
        File file = new File(filePath);
        FileUtil.mkParentDirs(file);
        try (FileOutputStream out = new FileOutputStream(file)) {
            document.write(out);
        }
        document.close();

        log.info("Word文档模板创建成功: {}", filePath);
    }

    /**
     * 创建Excel表格模板
     *
     * @param filePath 文件路径
     * @param title 表格标题
     * @throws IOException IO异常
     */
    public void createExcelTemplate(String filePath, String title) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 创建工作表
        workbook.createSheet(title);

        // 保存工作簿
        File file = new File(filePath);
        FileUtil.mkParentDirs(file);
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        workbook.close();

        log.info("Excel表格模板创建成功: {}", filePath);
    }

    /**
     * 创建PowerPoint演示文稿模板
     *
     * @param filePath 文件路径
     * @param title 演示文稿标题
     * @throws IOException IO异常
     */
    public void createPowerPointTemplate(String filePath, String title) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow();

        // 创建标题幻灯片
        ppt.createSlide();

        // 保存演示文稿
        File file = new File(filePath);
        FileUtil.mkParentDirs(file);
        try (FileOutputStream out = new FileOutputStream(file)) {
            ppt.write(out);
        }
        ppt.close();

        log.info("PowerPoint演示文稿模板创建成功: {}", filePath);
    }

    /**
     * 创建文本文件模板
     *
     * @param filePath 文件路径
     * @param title 文件标题
     * @throws IOException IO异常
     */
    public void createTextTemplate(String filePath, String title) throws IOException {
        String content = title + "\n\n";

        File file = new File(filePath);
        FileUtil.mkParentDirs(file);
        FileUtil.writeUtf8String(content, file);

        log.info("文本文件模板创建成功: {}", filePath);
    }

    /**
     * 根据文件类型创建模板
     *
     * @param filePath 文件路径
     * @param fileType 文件类型（docx/xlsx/pptx/txt）
     * @param title 文档标题
     * @throws IOException IO异常
     */
    public void createTemplate(String filePath, String fileType, String title) throws IOException {
        switch (fileType.toLowerCase()) {
            case "docx":
                createWordTemplate(filePath, title);
                break;
            case "xlsx":
                createExcelTemplate(filePath, title);
                break;
            case "pptx":
                createPowerPointTemplate(filePath, title);
                break;
            case "txt":
                createTextTemplate(filePath, title);
                break;
            default:
                throw new ServiceException("不支持的文件类型：" + fileType);
        }
    }

    /**
     * 检查文件类型是否支持创建
     *
     * @param fileType 文件类型
     * @return 是否支持
     */
    public boolean isSupportedType(String fileType) {
        return "docx".equalsIgnoreCase(fileType)
            || "xlsx".equalsIgnoreCase(fileType)
            || "pptx".equalsIgnoreCase(fileType)
            || "txt".equalsIgnoreCase(fileType);
    }

    /**
     * 创建财务报表模板
     *
     * @param filePath 文件路径
     * @param title 报表标题
     * @throws IOException IO异常
     */
    public void createFinancialReportTemplate(String filePath, String title) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("财务报表");

        // 创建标题样式
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        // 创建表头
        Row headerRow = sheet.createRow(2);
        String[] headers = {"日期", "项目", "收入", "支出", "余额", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        // 保存工作簿
        File file = new File(filePath);
        FileUtil.mkParentDirs(file);
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        workbook.close();

        log.info("财务报表模板创建成功: {}", filePath);
    }

    /**
     * 创建考勤表模板
     *
     * @param filePath 文件路径
     * @param title 考勤表标题
     * @throws IOException IO异常
     */
    public void createAttendanceTemplate(String filePath, String title) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("考勤表");

        // 创建标题样式
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        // 创建表头
        Row headerRow = sheet.createRow(2);
        String[] headers = {"姓名", "部门", "日期", "上班时间", "下班时间", "状态", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 3500);
        }

        // 保存工作簿
        File file = new File(filePath);
        FileUtil.mkParentDirs(file);
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        workbook.close();

        log.info("考勤表模板创建成功: {}", filePath);
    }

}

