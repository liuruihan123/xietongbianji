package com.xmzs.common.office.xlsx;

import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.converter.HtmlDocumentFacade;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description :  转换xlsx为html
 * @Author : zxy
 * @Date : 2023/10/30 14:32
 **/
public class XssfExcelToHtmlConverter extends AbstractXssfExcelConverter {

    private static final Logger logger = LoggerFactory.getLogger(XssfExcelToHtmlConverter.class);


    /**
     * 测试方法
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {

        String inputPath = "/Users/xuwenfeng/Desktop/testexcel.xlsx";
        String outputPath = "/Users/xuwenfeng/Desktop/testexcel.html";

        args = new String[]{inputPath, outputPath};

        if (args.length < 2) {
            System.err.println("参数错误，需要excel文件和输出html");
            return;
        }

        System.out.println("Converting " + args[0]);
        System.out.println("Saving output to " + args[1]);

        Document doc = XssfExcelToHtmlConverter.process(new File(args[0]));

        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File(args[1]));

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        // TODO set encoding from a command argument
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "no");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
    }

    /**
     * @param xlsFile:
     * @Description : 执行转换
     * @Author : zxy
     * @Date : 2023/10/30 14:33
     * @return: org.w3c.dom.Document
     **/
    public static Document process(File xlsFile) throws IOException, ParserConfigurationException {
        final XSSFWorkbook workbook = XssfExcelToHtmlUtils.loadXls(xlsFile);
        XssfExcelToHtmlConverter excelToHtmlConverter = new XssfExcelToHtmlConverter(
                XMLHelper.getDocumentBuilderFactory().newDocumentBuilder().newDocument());
        excelToHtmlConverter.processWorkbook(workbook);
        Document doc = excelToHtmlConverter.getDocument();
        workbook.close();
        return doc;
    }

    public static Document process(InputStream inputStream) throws IOException, ParserConfigurationException {
        final XSSFWorkbook workbook = XssfExcelToHtmlUtils.loadXls(inputStream);
        XssfExcelToHtmlConverter excelToHtmlConverter = new XssfExcelToHtmlConverter(
                XMLHelper.getDocumentBuilderFactory().newDocumentBuilder().newDocument());
        excelToHtmlConverter.processWorkbook(workbook);
        Document doc = excelToHtmlConverter.getDocument();
        workbook.close();
        return doc;
    }

    private String cssClassContainerCell = null;

    private String cssClassContainerDiv = null;

    private String cssClassPrefixCell = "c";

    private String cssClassPrefixDiv = "d";

    private String cssClassPrefixRow = "r";

    private String cssClassPrefixTable = "t";

    private Map<Short, String> excelStyleToClass = new LinkedHashMap<Short, String>();

    private final HtmlDocumentFacade htmlDocumentFacade;

    private boolean useDivsToSpan = false;

    public XssfExcelToHtmlConverter(Document doc) {
        htmlDocumentFacade = new HtmlDocumentFacade(doc);
    }

    public XssfExcelToHtmlConverter(HtmlDocumentFacade htmlDocumentFacade) {
        this.htmlDocumentFacade = htmlDocumentFacade;
    }

    protected String buildStyle(XSSFWorkbook workbook, XSSFCellStyle cellStyle) {
        StringBuilder style = new StringBuilder();

        style.append("white-space:pre-wrap;");
        XssfExcelToHtmlUtils.appendAlign(style, cellStyle.getAlignment().getCode());

        switch (cellStyle.getFillPattern().getCode()) {
            // no fill
            case 0:
                break;
            case 1:
                final XSSFColor foregroundColor = cellStyle.getFillForegroundColorColor();
                if (foregroundColor == null) {
                    break;
                }
                String fgCol = XssfExcelToHtmlUtils.getColor(foregroundColor);
                style.append("background-color:" + fgCol + ";");
                break;
            default:
                final XSSFColor backgroundColor = cellStyle.getFillBackgroundColorColor();
                if (backgroundColor == null) {
                    break;
                }
                String bgCol = XssfExcelToHtmlUtils.getColor(backgroundColor);
                style.append("background-color:" + bgCol + ";");
                break;
        }


        buildStyle_border(workbook, style, "top", cellStyle.getBorderTop().getCode(), cellStyle.getTopBorderXSSFColor());
        buildStyle_border(workbook, style, "right", cellStyle.getBorderRight().getCode(), cellStyle.getTopBorderXSSFColor());
        buildStyle_border(workbook, style, "bottom", cellStyle.getBorderBottom().getCode(), cellStyle.getBottomBorderXSSFColor());
        buildStyle_border(workbook, style, "left", cellStyle.getBorderLeft().getCode(), cellStyle.getLeftBorderXSSFColor());

        XSSFFont font = cellStyle.getFont();

        buildStyle_font(workbook, style, font);

        return style.toString();
    }

    private void buildStyle_border(XSSFWorkbook workbook, StringBuilder style, String type, short xlsBorder, XSSFColor xssfColor) {
        if (xlsBorder == BorderStyle.NONE.getCode()) {
            return;
        }

        StringBuilder borderStyle = new StringBuilder();
        borderStyle.append(XssfExcelToHtmlUtils.getBorderWidth(xlsBorder));
        borderStyle.append(' ');
        borderStyle.append(XssfExcelToHtmlUtils.getBorderStyle(xlsBorder));


        if (xssfColor != null) {
            borderStyle.append(' ');
            borderStyle.append(XssfExcelToHtmlUtils.getColor(xssfColor));
        }

        style.append("border-" + type + ":" + borderStyle + ";");
    }

    void buildStyle_font(XSSFWorkbook workbook, StringBuilder style, XSSFFont font) {
		/*switch (font.getBoldweight()) {
			case XSSFFont.BOLDWEIGHT_BOLD:
				style.append("font-weight:bold;");
				break;
			case XSSFFont.BOLDWEIGHT_NORMAL:
				// by default, not not increase HTML size
				// style.append( "font-weight: normal; " );
				break;
		}*/
        // 上面判断修改为下面，针对字体粗细设置
        if (font.getBold()) {
            style.append("font-weight:bold;");
        }

        final XSSFColor fontColor = font.getXSSFColor();
        if (fontColor != null) {
            style.append("color: " + XssfExcelToHtmlUtils.getColor(fontColor) + "; ");
        }

        if (font.getFontHeightInPoints() != 0) {
            style.append("font-size:" + font.getFontHeightInPoints() + "pt;");
        }

        if (font.getItalic()) {
            style.append("font-style:italic;");
        }
    }

    public String getCssClassPrefixCell() {
        return cssClassPrefixCell;
    }

    public String getCssClassPrefixDiv() {
        return cssClassPrefixDiv;
    }

    public String getCssClassPrefixRow() {
        return cssClassPrefixRow;
    }

    public String getCssClassPrefixTable() {
        return cssClassPrefixTable;
    }

    @Override
    public Document getDocument() {
        return htmlDocumentFacade.getDocument();
    }

    protected String getStyleClassName(XSSFWorkbook workbook, XSSFCellStyle cellStyle) {
        final Short cellStyleKey = Short.valueOf(cellStyle.getIndex());

        String knownClass = excelStyleToClass.get(cellStyleKey);
        if (knownClass != null) {
            return knownClass;
        }

        String cssStyle = buildStyle(workbook, cellStyle);
        String cssClass = htmlDocumentFacade.getOrCreateCssClass(cssClassPrefixCell, cssStyle);
        excelStyleToClass.put(cellStyleKey, cssClass);
        return cssClass;
    }

    public boolean isUseDivsToSpan() {
        return useDivsToSpan;
    }

    protected boolean processCell(XSSFCell cell, Element tableCellElement, int normalWidthPx, int maxSpannedWidthPx, float normalHeightPt) {
        final XSSFCellStyle cellStyle = cell.getCellStyle();

        String value;
        switch (cell.getCellType()) {
            case STRING:
                // XXX: enrich
                value = cell.getRichStringCellValue().getString();
                break;
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        XSSFRichTextString str = cell.getRichStringCellValue();
                        if (str != null && str.length() > 0) {
                            value = (str.toString());
                        } else {
                            value = XssfExcelToHtmlUtils.EMPTY;
                        }
                        break;
                    case NUMERIC:
                        double nValue = cell.getNumericCellValue();
                        short df = cellStyle.getDataFormat();
                        String dfs = cellStyle.getDataFormatString();
                        value = _formatter.formatRawCellContents(nValue, df, dfs);
                        break;
                    case BOOLEAN:
                        value = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case ERROR:
                        value = ErrorEval.getText(cell.getErrorCellValue());
                        break;
                    default:
                        logger.info("Unexpected cell cachedFormulaResultType (" + cell.getCachedFormulaResultType() + ")");
                        value = XssfExcelToHtmlUtils.EMPTY;
                        break;
                }
                break;
            case BLANK:
                value = XssfExcelToHtmlUtils.EMPTY;
                break;
            case NUMERIC:
                value = _formatter.formatCellValue(cell);
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case ERROR:
                value = ErrorEval.getText(cell.getErrorCellValue());
                break;
            default:
                logger.info("Unexpected cell type (" + cell.getCellType() + ")");
                return true;
        }

        final boolean noText = XssfExcelToHtmlUtils.isEmpty(value);
        final boolean wrapInDivs = !noText && isUseDivsToSpan() && !cellStyle.getWrapText();

        if (cellStyle.getIndex() != 0) {
            @SuppressWarnings("resource")
            XSSFWorkbook workbook = cell.getRow().getSheet().getWorkbook();
            String mainCssClass = getStyleClassName(workbook, cellStyle);

            if (wrapInDivs) {
                tableCellElement.setAttribute("class", mainCssClass + " " + cssClassContainerCell);
            } else {
                tableCellElement.setAttribute("class", mainCssClass);
            }

            if (noText) {
                value = "\u00A0";
            }
        }

        if (isOutputLeadingSpacesAsNonBreaking() && value.startsWith(" ")) {
            StringBuilder builder = new StringBuilder();
            for (int c = 0; c < value.length(); c++) {
                if (value.charAt(c) != ' ') {
                    break;
                }
                builder.append('\u00a0');
            }

            if (value.length() != builder.length()) {
                builder.append(value.substring(builder.length()));
            }

            value = builder.toString();
        }

        Text text = htmlDocumentFacade.createText(value);

        if (wrapInDivs) {
            Element outerDiv = htmlDocumentFacade.createBlock();
            outerDiv.setAttribute("class", this.cssClassContainerDiv);

            Element innerDiv = htmlDocumentFacade.createBlock();
            StringBuilder innerDivStyle = new StringBuilder();
            innerDivStyle.append("position:absolute;min-width:");
            innerDivStyle.append(normalWidthPx);
            innerDivStyle.append("px;");
            if (maxSpannedWidthPx != Integer.MAX_VALUE) {
                innerDivStyle.append("max-width:");
                innerDivStyle.append(maxSpannedWidthPx);
                innerDivStyle.append("px;");
            }
            innerDivStyle.append("overflow:hidden;max-height:");
            innerDivStyle.append(normalHeightPt);
            innerDivStyle.append("pt;white-space:nowrap;");
            XssfExcelToHtmlUtils.appendAlign(innerDivStyle, cellStyle.getAlignment().getCode());
            htmlDocumentFacade.addStyleClass(outerDiv, cssClassPrefixDiv, innerDivStyle.toString());

            innerDiv.appendChild(text);
            outerDiv.appendChild(innerDiv);
            tableCellElement.appendChild(outerDiv);
        } else {
            tableCellElement.appendChild(text);
        }

        return XssfExcelToHtmlUtils.isEmpty(value) && (cellStyle.getIndex() == 0);
    }

    protected void processColumnHeaders(XSSFSheet sheet, int maxSheetColumns, Element table) {
        Element tableHeader = htmlDocumentFacade.createTableHeader();
        table.appendChild(tableHeader);

        Element tr = htmlDocumentFacade.createTableRow();

        if (isOutputRowNumbers()) {
            tr.appendChild(htmlDocumentFacade.createTableHeaderCell());
        }

        for (int c = 0; c < maxSheetColumns; c++) {
            if (!isOutputHiddenColumns() && sheet.isColumnHidden(c)) {
                continue;
            }

            Element th = htmlDocumentFacade.createTableHeaderCell();
            String text = getColumnName(c);
            th.appendChild(htmlDocumentFacade.createText(text));
            tr.appendChild(th);
        }
        tableHeader.appendChild(tr);
    }

    /**
     * @param sheet:
     * @param maxSheetColumns:
     * @param table:
     * @Description : 处理宽度
     * @Author : zxy
     * @Date : 2023/10/30 14:35
     * @return: void
     **/
    protected void processColumnWidths(XSSFSheet sheet, int maxSheetColumns, Element table) {
        // draw COLS after we know max column number
        Element columnGroup = htmlDocumentFacade.createTableColumnGroup();
        if (isOutputRowNumbers()) {
            columnGroup.appendChild(htmlDocumentFacade.createTableColumn());
        }
        for (int c = 0; c < maxSheetColumns; c++) {
            if (!isOutputHiddenColumns() && sheet.isColumnHidden(c)) {
                continue;
            }
            Element col = htmlDocumentFacade.createTableColumn();
            col.setAttribute("width", String.valueOf(getColumnWidth(sheet, c)));
            columnGroup.appendChild(col);
        }
        table.appendChild(columnGroup);
    }

    protected void processDocumentInformation(SummaryInformation summaryInformation) {
        if (XssfExcelToHtmlUtils.isNotEmpty(summaryInformation.getTitle())) {
            htmlDocumentFacade.setTitle(summaryInformation.getTitle());
        }

        if (XssfExcelToHtmlUtils.isNotEmpty(summaryInformation.getAuthor())) {
            htmlDocumentFacade.addAuthor(summaryInformation.getAuthor());
        }

        if (XssfExcelToHtmlUtils.isNotEmpty(summaryInformation.getKeywords())) {
            htmlDocumentFacade.addKeywords(summaryInformation.getKeywords());
        }

        if (XssfExcelToHtmlUtils.isNotEmpty(summaryInformation.getComments())) {
            htmlDocumentFacade.addDescription(summaryInformation.getComments());
        }
    }

    /**
     * @param mergedRanges:
     * @param row:
     * @param tableRowElement:
     * @Description : 处理行 ， 设计合并
     * @Author : zxy
     * @Date : 2023/10/30 14:35
     * @return: int
     **/
    protected int processRow(CellRangeAddress[][] mergedRanges, XSSFRow row, Element tableRowElement) {
        final XSSFSheet sheet = row.getSheet();
        final short maxColIx = row.getLastCellNum();
        if (maxColIx <= 0) {
            return 0;
        }

        final List<Element> emptyCells = new ArrayList<Element>(maxColIx);

        if (isOutputRowNumbers()) {
            Element tableRowNumberCellElement = htmlDocumentFacade.createTableHeaderCell();
            processRowNumber(row, tableRowNumberCellElement);
            emptyCells.add(tableRowNumberCellElement);
        }

        int maxRenderedColumn = 0;
        for (int colIx = 0; colIx < maxColIx; colIx++) {
            if (!isOutputHiddenColumns() && sheet.isColumnHidden(colIx)) {
                continue;
            }
            CellRangeAddress range = XssfExcelToHtmlUtils.getMergedRange(mergedRanges, row.getRowNum(), colIx);

            if (range != null && (range.getFirstColumn() != colIx || range.getFirstRow() != row.getRowNum())) {
                continue;
            }

            XSSFCell cell = row.getCell(colIx);

            int divWidthPx = 0;
            if (isUseDivsToSpan()) {
                divWidthPx = getColumnWidth(sheet, colIx);

                boolean hasBreaks = false;
                for (int nextColumnIndex = colIx + 1; nextColumnIndex < maxColIx; nextColumnIndex++) {
                    if (!isOutputHiddenColumns() && sheet.isColumnHidden(nextColumnIndex)) {
                        continue;
                    }

                    if (row.getCell(nextColumnIndex) != null && !isTextEmpty(row.getCell(nextColumnIndex))) {
                        hasBreaks = true;
                        break;
                    }

                    divWidthPx += getColumnWidth(sheet, nextColumnIndex);
                }

                if (!hasBreaks) {
                    divWidthPx = Integer.MAX_VALUE;
                }
            }

            Element tableCellElement = htmlDocumentFacade.createTableCell();

            if (range != null) {
                if (range.getFirstColumn() != range.getLastColumn()) {
                    tableCellElement.setAttribute("colspan", String.valueOf(range.getLastColumn() - range.getFirstColumn() + 1));
                }
                if (range.getFirstRow() != range.getLastRow()) {
                    tableCellElement.setAttribute("rowspan", String.valueOf(range.getLastRow() - range.getFirstRow() + 1));
                }
            }

            boolean emptyCell;
            if (cell != null) {
                emptyCell = processCell(cell, tableCellElement, getColumnWidth(sheet, colIx), divWidthPx, row.getHeight() / 20f);
            } else {
                emptyCell = true;
            }

            if (emptyCell) {
                emptyCells.add(tableCellElement);
            } else {
                for (Element emptyCellElement : emptyCells) {
                    tableRowElement.appendChild(emptyCellElement);
                }
                emptyCells.clear();

                tableRowElement.appendChild(tableCellElement);
                maxRenderedColumn = colIx;
            }
        }

        return maxRenderedColumn + 1;
    }

    protected void processRowNumber(XSSFRow row, Element tableRowNumberCellElement) {
        tableRowNumberCellElement.setAttribute("class", "rownumber");
        Text text = htmlDocumentFacade.createText(getRowName(row));
        tableRowNumberCellElement.appendChild(text);
    }

    protected void processSheet(XSSFSheet sheet) {
        processSheetHeader(htmlDocumentFacade.getBody(), sheet);

        final int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        if (physicalNumberOfRows <= 0) {
            return;
        }

        Element table = htmlDocumentFacade.createTable();
        htmlDocumentFacade.addStyleClass(table, cssClassPrefixTable, "border-collapse:collapse;border-spacing:0;");
        Element tableBody = htmlDocumentFacade.createTableBody();

        final CellRangeAddress[][] mergedRanges = XssfExcelToHtmlUtils.buildMergedRangesMap(sheet);

        final List<Element> emptyRowElements = new ArrayList<Element>(physicalNumberOfRows);
        int maxSheetColumns = 1;
        for (int r = sheet.getFirstRowNum(); r <= sheet.getLastRowNum(); r++) {
            XSSFRow row = sheet.getRow(r);

            if (row == null) {
                continue;
            }

            if (!isOutputHiddenRows() && row.getZeroHeight()) {
                continue;
            }
            Element tableRowElement = htmlDocumentFacade.createTableRow();
            htmlDocumentFacade.addStyleClass(tableRowElement, cssClassPrefixRow, "height:" + (row.getHeight() / 20f) + "pt;");

            int maxRowColumnNumber = processRow(mergedRanges, row, tableRowElement);

            if (maxRowColumnNumber == 0) {
                emptyRowElements.add(tableRowElement);
            } else {
                if (!emptyRowElements.isEmpty()) {
                    for (Element emptyRowElement : emptyRowElements) {
                        tableBody.appendChild(emptyRowElement);
                    }
                    emptyRowElements.clear();
                }

                tableBody.appendChild(tableRowElement);
            }
            maxSheetColumns = Math.max(maxSheetColumns, maxRowColumnNumber);
        }

        processColumnWidths(sheet, maxSheetColumns, table);

        if (isOutputColumnHeaders()) {
            processColumnHeaders(sheet, maxSheetColumns, table);
        }

        table.appendChild(tableBody);

        htmlDocumentFacade.getBody().appendChild(table);
    }

    protected void processSheetHeader(Element htmlBody, XSSFSheet sheet) {
        Element h2 = htmlDocumentFacade.createHeader2();
        h2.appendChild(htmlDocumentFacade.createText(sheet.getSheetName()));
        htmlBody.appendChild(h2);
    }

    public void processWorkbook(XSSFWorkbook workbook) {
        if (isUseDivsToSpan()) {
            this.cssClassContainerCell = htmlDocumentFacade.getOrCreateCssClass(cssClassPrefixCell,
                    "padding:0;margin:0;align:left;vertical-align:top;");
            this.cssClassContainerDiv = htmlDocumentFacade.getOrCreateCssClass(cssClassPrefixDiv, "position:relative;");
        }

        for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
            XSSFSheet sheet = workbook.getSheetAt(s);
            processSheet(sheet);
        }

        htmlDocumentFacade.updateStylesheet();
    }

    public void setCssClassPrefixCell(String cssClassPrefixCell) {
        this.cssClassPrefixCell = cssClassPrefixCell;
    }

    public void setCssClassPrefixDiv(String cssClassPrefixDiv) {
        this.cssClassPrefixDiv = cssClassPrefixDiv;
    }

    public void setCssClassPrefixRow(String cssClassPrefixRow) {
        this.cssClassPrefixRow = cssClassPrefixRow;
    }

    public void setCssClassPrefixTable(String cssClassPrefixTable) {
        this.cssClassPrefixTable = cssClassPrefixTable;
    }

    public void setUseDivsToSpan(boolean useDivsToSpan) {
        this.useDivsToSpan = useDivsToSpan;
    }
}
