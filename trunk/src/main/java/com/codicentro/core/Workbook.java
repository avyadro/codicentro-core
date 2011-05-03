/**
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 3/05/2011 at 02:44:11 PM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: Workbook.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core;

import com.codicentro.core.model.Cell;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Workbook implements Serializable {

    private Logger logger = LoggerFactory.getLogger(Workbook.class);
    private XSSFWorkbook workbook = null;
    private XSSFSheet sheet = null;
    private Element template = null;
    private String idHeader = null;
    private XSSFRow row = null;
    private int idxRow = 0;
    private int idxCell = -1;
    private List<Cell> cells = null;

    public Workbook() {
        workbook = new XSSFWorkbook();
    }

    public Workbook(URL template) throws CDCException {
        this();
        try {
            this.template = new SAXBuilder().build(template).getRootElement();
        } catch (JDOMException ex) {
            throw new CDCException(ex);
        } catch (IOException ex) {
            throw new CDCException(ex);
        }
    }

    public Workbook(URL template, String idHeader) throws CDCException {
        this(template);
        this.idHeader = idHeader;
        try {
            createHeader();
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void createHeader(String idHeader) throws CDCException {
        this.idHeader = idHeader;
        try {
            createHeader();
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @param <TEntity>
     * @param values
     * @param doc
     * @param idHeader    
     * @return
     * @throws Exception
     */
    private void createHeader() throws CDCException {
        idxRow = 0;
        row = null;
        Element headers = template.getChild("headers");
        Iterator<Element> iHeader = headers.getChildren("header").iterator();
        Iterator<Element> iColumn = null;
        Element header = null;
        while ((iHeader.hasNext()) && (iColumn == null)) {
            header = iHeader.next();
            if ((header.getAttribute("name") != null) && (header.getAttribute("name").getValue().equals(idHeader))) {
                /*** SHEET NAME ***/
                String sheetname = (header.getAttribute("sheetname") == null) ? null : header.getAttribute("sheetname").getValue();
                sheet = (TypeCast.isNullOrEmpty(sheetname)) ? workbook.createSheet() : workbook.createSheet(sheetname);
                row = sheet.createRow(idxRow);
                /*** ROW SIZE ***/
                String frdata = (header.getAttribute("frdata") == null) ? null : header.getAttribute("frdata").getValue();

                if (!TypeCast.isNullOrEmpty(frdata)) {
                    logger.info("First row data: " + frdata);
                    while (idxRow + 1 < TypeCast.toInt(frdata)) {
                        idxRow++;
                        sheet.createRow(idxRow);
                    }
                }
                iColumn = header.getChildren("column").iterator();
            }
        }
        if (iColumn == null) {
            throw new CDCException("Header " + idHeader + " not found.");
        }

        cells = new ArrayList<Cell>();
        /*** HEADERS ***/
        idxCell = -1;
        while (iColumn.hasNext()) {
            columnDef(row, iColumn.next());
        }
    }

    private void columnDef(XSSFRow row, Element column) throws CDCException {
        Cell c = new Cell(column.getAttribute("name").getValue());
        /*** VARS ***/
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(TypeCast.toShort(1));
        style.setBorderTop(TypeCast.toShort(1));
        style.setBorderLeft(TypeCast.toShort(1));
        style.setBorderRight(TypeCast.toShort(1));

        XSSFFont font = workbook.createFont();
        XSSFCell cell = null;
        /*** COL INDEX INCREMENT, DEFAULT 1 ***/
        String cindexinc = (column.getAttribute("cindexinc") == null) ? null : column.getAttribute("cindexinc").getValue();
        if (TypeCast.toBigInteger(cindexinc) != null) {
            idxCell = idxCell + TypeCast.toInt(cindexinc);
        } else {
            idxCell++;
        }
        /*** COL INDEX ***/
        String cindex = (column.getAttribute("cindex") == null) ? null : column.getAttribute("cindex").getValue();
        if (TypeCast.toBigInteger(cindex) != null) {
            idxCell = TypeCast.toInt(cindex);
        }
        logger.info("Cell: " + CellReference.convertNumToColString(idxCell) + idxRow);
        /*** ROW INDEX ***/
        String rindex = (column.getAttribute("rindex") == null) ? null : column.getAttribute("rindex").getValue();
        if (TypeCast.toBigInteger(rindex) != null) {
            row = sheet.getRow(TypeCast.toInt(rindex));
        }
        cell = row.createCell(idxCell);
        /*** ROW SPAN ***/
        String rowspan = (column.getAttribute("rowspan") == null) ? null : column.getAttribute("rowspan").getValue();
        if (TypeCast.toBigInteger(rowspan) != null) {
            logger.info("Rows a cell should span: " + idxCell + " to " + rowspan);
            sheet.addMergedRegion(new CellRangeAddress(cell.getRowIndex(), TypeCast.toInt(rowspan), idxCell, idxCell));
        }

        /*** COL SPAN ***/
        String colspan = (column.getAttribute("colspan") == null) ? null : column.getAttribute("colspan").getValue();
        if (TypeCast.toBigInteger(colspan) != null) {
            logger.info("Columns a cell should span: " + cell.getColumnIndex() + " to " + (cell.getColumnIndex() + TypeCast.toInt(colspan) - 1));
            sheet.addMergedRegion(new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex(), idxCell, cell.getColumnIndex() + TypeCast.toInt(colspan) - 1));
        }



        /*** ALIGMENT ***/
        String alignment = (column.getAttribute("alignment") == null) ? null : column.getAttribute("alignment").getValue();
        if (!TypeCast.isNullOrEmpty(alignment)) {
            if (alignment.equals("alCenter")) {
                style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            } else if (alignment.equals("alCenterSelection")) {
                style.setAlignment(XSSFCellStyle.ALIGN_CENTER_SELECTION);
            } else if (alignment.equals("alFill")) {
                style.setAlignment(XSSFCellStyle.ALIGN_FILL);
            } else if (alignment.equals("alGeneral")) {
                style.setAlignment(XSSFCellStyle.ALIGN_GENERAL);
            } else if (alignment.equals("alJustify")) {
                style.setAlignment(XSSFCellStyle.ALIGN_JUSTIFY);
            } else if (alignment.equals("alLeft")) {
                style.setAlignment(XSSFCellStyle.ALIGN_LEFT);
            } else if (alignment.equals("alRight")) {
                style.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
            }
        }

        /*** VERTICAL ALIGMENT ***/
        String valignment = (column.getAttribute("valignment") == null) ? null : column.getAttribute("valignment").getValue();
        if (!TypeCast.isNullOrEmpty(valignment)) {
            if (valignment.equals("alBottom")) {
                style.setVerticalAlignment(XSSFCellStyle.VERTICAL_BOTTOM);
            } else if (valignment.equals("alCenter")) {
                style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
            } else if (valignment.equals("alJustify")) {
                style.setVerticalAlignment(XSSFCellStyle.VERTICAL_JUSTIFY);
            } else if (valignment.equals("alTop")) {
                style.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
            }
        }
        /*** WRAP TEXT ***/
        style.setWrapText((column.getAttribute("wrap") == null) ? false : TypeCast.toBoolean(column.getAttribute("wrap").getValue()));

        /*** ROTATION ***/
        String rotation = (column.getAttribute("rotation") == null) ? null : column.getAttribute("rotation").getValue();
        if (TypeCast.toBigDecimal(rotation) != null) {
            style.setRotation(TypeCast.toBigDecimal(rotation).shortValue());
        }

        /*** BACKGROUND ***/
        String background = (column.getAttribute("background") == null) ? null : column.getAttribute("background").getValue();
        if (!TypeCast.isNullOrEmpty(background)) {
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            if (TypeCast.toShortD(background) != null) {
                style.setFillForegroundColor(TypeCast.toShortD(background));
            } else {
                style.setFillForegroundColor(TypeCast.toShort(TypeCast.GF("org.apache.poi.hssf.util.XSSFColor$" + background.toUpperCase(), "index")));
            }
        }
        /*** WIDTH ***/
        BigDecimal width = (column.getAttribute("width") == null) ? null : TypeCast.toBigDecimal(column.getAttribute("width").getValue());
        if (width != null) {
            width = TypeCast.toBigDecimal(width.doubleValue() * 1308.90);
            sheet.setColumnWidth(idxCell, width.intValue());
        }

        /*** HEIGHT ***/
        BigDecimal height = (column.getAttribute("height") == null) ? null : TypeCast.toBigDecimal(column.getAttribute("height").getValue());
        if (height != null) {
            height = TypeCast.toBigDecimal(height.doubleValue() * 1308.90);
            row.setHeight(height.shortValue());
        }

        /*** FONT BOLD ***/
        Boolean bold = (column.getAttribute("bold") == null) ? TypeCast.toBoolean("false") : TypeCast.toBoolean(column.getAttribute("bold").getValue());
        if (bold) {
            font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        }
        /*** SUMMARY ***/
        c.setSummary((column.getAttribute("summary") == null) ? TypeCast.toBoolean("false") : TypeCast.toBoolean(column.getAttribute("summary").getValue()));
        /*** SUMMARY FORMULA ***/
        c.setSummaryFormula((column.getAttribute("summaryFormula") == null) ? null : column.getAttribute("summaryFormula").getValue());
        /*** FORMULA ***/
        c.setFormula((column.getAttribute("formula") == null) ? null : column.getAttribute("formula").getValue());
        /*** DATA FORMAT ***/
        c.setDataFormat((column.getAttribute("format") == null) ? null : column.getAttribute("format").getValue());
        /*** RENDER DATA ***/
        c.setRender((column.getAttribute("render") == null) ? true : TypeCast.toBoolean(column.getAttribute("render").getValue()));
        /*** CALCULATE BY DATA ***/
        c.setCalculate((column.getAttribute("calculate") == null) ? null : column.getAttribute("calculate").getValue());

        style.setFont(font);
        cell.setCellStyle(style);
        cell.setCellValue(column.getValue());
        cells.add(c);
    }

    public <TEntity> void render(List<TEntity> values) throws CDCException {
        XSSFRow localRow = null;
        Object oValue = null;
        XSSFCell cell = null;
        idxCell = -1;
        XSSFCellStyle style = null;
        for (Object value : values) {
            idxRow++;
            localRow = sheet.createRow(idxRow);
            idxCell = -1;
            for (int idx = 0; idx < cells.size(); idx++) {
                if (cells.get(idx).isRender()) {
                    idxCell++;
                    cell = localRow.createCell(idxCell);
                    /*** STYLE ***/
                    style = sheet.getWorkbook().createCellStyle();
                    if (cells.get(idx).getDataFormat() != null) {
                        style.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat(cells.get(idx).getDataFormat()));
                    }
                    cell.setCellStyle(style);
                    /*** ***/
                    if (cells.get(idx).getFormula() != null) {
                        cell.setCellFormula(mkFormula(cells.get(idx).getFormula(), idxCell));
                    } else {
                        oValue = (cells.get(idx).isRender()) ? TypeCast.GN(value, "get" + cells.get(idx).getName()) : null;
                        if (oValue instanceof java.lang.Number) {
                            cell.setCellValue(TypeCast.toBigDecimal(oValue).doubleValue());
                        } else {
                            cell.setCellValue(TypeCast.toString(oValue));
                        }
                    }
                }
            }
        }
    }

    private String mkFormula(String formula, int idxCol) {
        formula = formula.replaceAll("\\{row\\}", "" + (idxRow + 1));
        formula = formula.replaceAll("\\{col\\}", CellReference.convertNumToColString(idxCol));
        formula = formulaCheckCol(formula, idxCol);
        return formula;
    }

    private String formulaCheckCol(String fm, int idxCol) {
        int posCol = fm.indexOf("{col");
        if (posCol == -1) {
            return fm;
        }
        int posKey = fm.indexOf("}");
        if (posKey == -1) {
            return fm;
        }
        String str = fm.substring(posCol + 4, posKey);
        if (TypeCast.isNullOrEmpty(str)) {
            return fm;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '+') {
                fm = fm.substring(0, posCol + 4) + fm.substring(posCol + 5);
                count++;
            } else if (str.charAt(i) == '-') {
                fm = fm.substring(i);
                count--;
            }
        }
        return formulaCheckCol(fm.replaceAll("\\{col\\}", CellReference.convertNumToColString(idxCol + count)), idxCol);
    }
}
