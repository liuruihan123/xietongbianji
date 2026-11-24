package com.xmzs.coedit.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import com.xmzs.coedit.domain.vo.CoFileVo;
import com.xmzs.common.core.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 表格服务
 * 用于处理Excel表格的特殊功能
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SpreadsheetService {

    private final ICoFileService fileService;
    private final OnlyOfficeConfig onlyOfficeConfig;

    /**
     * 从CSV导入数据到Excel
     *
     * @param workspaceId 工作空间ID
     * @param folderId 文件夹ID
     * @param csvFile CSV文件
     * @param fileName 目标文件名
     * @return 创建的Excel文件信息
     */
    public CoFileVo importFromCsv(Long workspaceId, Long folderId, MultipartFile csvFile, String fileName) {
        log.info("开始从CSV导入数据: fileName={}", fileName);

        try {
            // 1. 读取CSV文件
            List<List<String>> csvData = readCsvFile(csvFile);
            if (csvData.isEmpty()) {
                throw new ServiceException("CSV文件为空");
            }

            // 2. 创建Excel工作簿
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sheet1");

            // 3. 写入数据到Excel
            for (int i = 0; i < csvData.size(); i++) {
                Row row = sheet.createRow(i);
                List<String> rowData = csvData.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowData.get(j));
                }
            }

            // 4. 自动调整列宽
            for (int i = 0; i < csvData.get(0).size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // 5. 保存Excel文件到临时位置
            String tempFilePath = onlyOfficeConfig.getStoragePath() + File.separator + 
                                  "temp" + File.separator + IdUtil.getSnowflakeNextId() + ".xlsx";
            File tempFile = new File(tempFilePath);
            FileUtil.mkParentDirs(tempFile);
            
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
            }
            workbook.close();

            // 6. 上传Excel文件
            String excelFileName = fileName.endsWith(".xlsx") ? fileName : fileName + ".xlsx";
            MultipartFile excelFile = createMultipartFile(tempFile, excelFileName);
            CoFileVo fileVo = fileService.uploadFile(workspaceId, folderId, excelFile);

            // 7. 删除临时文件
            FileUtil.del(tempFile);

            log.info("CSV导入成功: fileId={}", fileVo.getId());
            return fileVo;

        } catch (IOException e) {
            log.error("CSV导入失败", e);
            throw new ServiceException("CSV导入失败: " + e.getMessage());
        }
    }

    /**
     * 导出Excel为CSV
     *
     * @param fileId 文件ID
     * @return CSV文件字节数组
     */
    public byte[] exportToCsv(Long fileId) {
        log.info("开始导出CSV: fileId={}", fileId);

        try {
            // 1. 获取文件信息
            CoFileVo file = fileService.queryById(fileId);
            if (file == null) {
                throw new ServiceException("文件不存在");
            }

            // 2. 检查文件类型
            String fileType = file.getFileExtension();
            if (!isExcelFile(fileType)) {
                throw new ServiceException("仅支持Excel文件导出为CSV");
            }

            // 3. 读取Excel文件
            byte[] fileBytes = fileService.downloadFile(fileId);
            ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
            Workbook workbook = WorkbookFactory.create(bis);

            // 4. 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);

            // 5. 转换为CSV格式
            StringBuilder csvContent = new StringBuilder();
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    rowData.add(getCellValueAsString(cell));
                }
                csvContent.append(String.join(",", rowData)).append("\n");
            }

            workbook.close();

            log.info("CSV导出成功: fileId={}", fileId);
            return csvContent.toString().getBytes(StandardCharsets.UTF_8);

        } catch (IOException e) {
            log.error("CSV导出失败", e);
            throw new ServiceException("CSV导出失败: " + e.getMessage());
        }
    }

    /**
     * 读取CSV文件
     *
     * @param csvFile CSV文件
     * @return CSV数据
     * @throws IOException IO异常
     */
    private List<List<String>> readCsvFile(MultipartFile csvFile) throws IOException {
        List<List<String>> data = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> row = parseCsvLine(line);
                data.add(row);
            }
        }
        
        return data;
    }

    /**
     * 解析CSV行
     *
     * @param line CSV行
     * @return 行数据
     */
    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        result.add(current.toString().trim());
        return result;
    }

    /**
     * 获取单元格值为字符串
     *
     * @param cell 单元格
     * @return 字符串值
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return escapeCsvValue(cell.getStringCellValue());
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return escapeCsvValue(cell.getCellFormula());
            default:
                return "";
        }
    }

    /**
     * 转义CSV值
     *
     * @param value 原始值
     * @return 转义后的值
     */
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * 检查是否为Excel文件
     *
     * @param fileType 文件类型
     * @return 是否为Excel文件
     */
    private boolean isExcelFile(String fileType) {
        return "xlsx".equalsIgnoreCase(fileType) || 
               "xls".equalsIgnoreCase(fileType) || 
               "xlsm".equalsIgnoreCase(fileType);
    }

    /**
     * 创建MultipartFile
     *
     * @param file 文件
     * @param fileName 文件名
     * @return MultipartFile
     */
    private MultipartFile createMultipartFile(File file, String fileName) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return fileName;
            }

            @Override
            public String getContentType() {
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }

            @Override
            public boolean isEmpty() {
                return file.length() == 0;
            }

            @Override
            public long getSize() {
                return file.length();
            }

            @Override
            public byte[] getBytes() throws IOException {
                return FileUtil.readBytes(file);
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(file);
            }

            @Override
            public void transferTo(File dest) throws IOException {
                FileUtil.copy(file, dest, true);
            }
        };
    }

}

