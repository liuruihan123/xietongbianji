package com.xmzs.common.office.xlsx;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Beta;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description : excel读取内容
 * @Author : zxy
 * @Date : 2023/10/30 14:30
 **/
@Beta
public class AbstractXssfExcelUtils {
    public static final String EMPTY = "";
    private static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;
    private static final int UNIT_OFFSET_LENGTH = 7;

    public static String getAlign(short alignment) {
        HorizontalAlignment horizontalAlignment = HorizontalAlignment.forInt(alignment);
        switch (horizontalAlignment) {
            case CENTER:
                return "center";
            case CENTER_SELECTION:
                return "center";
            case FILL:
                return "fill";
            case GENERAL:
                return "";
            case JUSTIFY:
                return "justify";
            case LEFT:
                return "left";
            case RIGHT:
                return "right";
            default:
                return "";
        }
    }

    public static String getBorderStyle(short xlsBorder) {
        final String borderStyle;
        BorderStyle style = BorderStyle.valueOf(xlsBorder);
        switch (style) {
            case NONE:
                borderStyle = "none";
                break;
            case DASH_DOT:
            case DASH_DOT_DOT:
            case DOTTED:
            case HAIR:
            case MEDIUM_DASH_DOT:
            case MEDIUM_DASH_DOT_DOT:
            case SLANTED_DASH_DOT:
                borderStyle = "dotted";
                break;
            case DASHED:
            case MEDIUM_DASHED:
                borderStyle = "dashed";
                break;
            case DOUBLE:
                borderStyle = "double";
                break;
            default:
                borderStyle = "solid";
                break;
        }
        return borderStyle;
    }

    public static String getBorderWidth(short xlsBorder) {
        final String borderWidth = "2pt";
        // TODO 需要处理
		/*switch (xlsBorder) {
			case XSSFCellStyle.BORDER_MEDIUM_DASH_DOT:
			case XSSFCellStyle.BORDER_MEDIUM_DASH_DOT_DOT:
			case XSSFCellStyle.BORDER_MEDIUM_DASHED:
				borderWidth = "2pt";
				break;
			case XSSFCellStyle.BORDER_THICK:
				borderWidth = "thick";
				break;
			default:
				borderWidth = "thin";
				break;
		}*/
        return borderWidth;
    }

    /**
     * 转换为网页颜色
     *
     * @param color
     * @return
     */
    public static String getColor(XSSFColor color) {
        String result = color.getARGBHex();
        if (result != null) {
            result = "#" + result.substring(2, result.length());
        } else {
            result = "";
        }
        return result;
    }

    /**
     * 动态计算宽度
     */
    public static int getColumnWidthInPx(int widthUnits) {
        int pixels = (widthUnits / EXCEL_COLUMN_WIDTH_FACTOR) * UNIT_OFFSET_LENGTH;

        int offsetWidthUnits = widthUnits % EXCEL_COLUMN_WIDTH_FACTOR;
        pixels += Math.round(offsetWidthUnits / ((float) EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH));

        return pixels;
    }

    /**
     * 合并计算
     */
    public static CellRangeAddress getMergedRange(CellRangeAddress[][] mergedRanges, int rowNumber, int columnNumber) {
        CellRangeAddress[] mergedRangeRowInfo = rowNumber < mergedRanges.length ? mergedRanges[rowNumber] : null;
        CellRangeAddress cellRangeAddress =
                mergedRangeRowInfo != null && columnNumber < mergedRangeRowInfo.length ? mergedRangeRowInfo[columnNumber] : null;

        return cellRangeAddress;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static XSSFWorkbook loadXls(File xlsFile) throws IOException {
        final FileInputStream inputStream = new FileInputStream(xlsFile);
        try {
            return new XSSFWorkbook(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static XSSFWorkbook loadXls(InputStream inputStream) throws IOException {
        try {
            return new XSSFWorkbook(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}

