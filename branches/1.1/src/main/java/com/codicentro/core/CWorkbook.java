/**
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 3/05/2011 at 02:44:11 PM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: CWorkbook.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core;

import com.codicentro.core.model.CCell;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CWorkbook implements Serializable {

    public enum XFormat {

        XLSX, XLS
    };
    private Logger logger = LoggerFactory.getLogger(CWorkbook.class);
    private Workbook workbook = null;
    private XFormat xFormat = XFormat.XLSX;
    private Sheet sheet = null;
    private Element template = null;
    private String idHeader = null;
    private Row row = null;
    private int idxRow = 0;
    private int idxCell = -1;
    private List<CCell> cells = null;

    public CWorkbook(XFormat xFormat) {
        workbook = (xFormat == XFormat.XLSX) ? new XSSFWorkbook() : new HSSFWorkbook();
        this.xFormat = xFormat;
    }

    /**
     *
     * @param template
     * @throws CDCException
     */
    public CWorkbook(InputStream template) throws CDCException {
        this(template, XFormat.XLSX);

    }

    /**
     * 
     * @param template
     * @param xFormat
     * @throws CDCException 
     */
    public CWorkbook(InputStream template, XFormat xFormat) throws CDCException {
        this(xFormat);
        try {
            this.template = new SAXBuilder().build(template).getRootElement();
        } catch (JDOMException ex) {
            throw new CDCException(ex);
        } catch (IOException ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @param template
     * @param idHeader
     * @throws CDCException
     */
    public CWorkbook(InputStream template, String idHeader) throws CDCException {
        this(template, idHeader, XFormat.XLSX);
    }

    public CWorkbook(InputStream template, String idHeader, XFormat xFormat) throws CDCException {
        this(template, xFormat);
        this.idHeader = idHeader;
        try {
            createHeader();
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @param template
     * @throws CDCException 
     */
    public CWorkbook(File template) throws CDCException {
        this(template, XFormat.XLSX);
    }

    /**
     * 
     * @param template
     * @param xFormat
     * @throws CDCException 
     */
    public CWorkbook(File template, XFormat xFormat) throws CDCException {
        this(xFormat);
        try {
            this.template = new SAXBuilder().build(template).getRootElement();
        } catch (JDOMException ex) {
            throw new CDCException(ex);
        } catch (IOException ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @param template
     * @param idHeader
     * @throws CDCException
     */
    public CWorkbook(File template, String idHeader) throws CDCException {
        this(template, idHeader, XFormat.XLSX);
    }

    /**
     * 
     * @param template
     * @param idHeader
     * @param xFormat
     * @throws CDCException 
     */
    public CWorkbook(File template, String idHeader, XFormat xFormat) throws CDCException {
        this(template, xFormat);
        this.idHeader = idHeader;
        try {
            createHeader();
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @param template
     * @throws CDCException 
     */
    public CWorkbook(URL template) throws CDCException {
        this(template, XFormat.XLSX);
    }

    /**
     * 
     * @param template
     * @param xFormat
     * @throws CDCException 
     */
    public CWorkbook(URL template, XFormat xFormat) throws CDCException {
        this(xFormat);
        try {
            this.template = new SAXBuilder().build(template).getRootElement();
        } catch (JDOMException ex) {
            throw new CDCException(ex);
        } catch (IOException ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @param template
     * @param idHeader
     * @throws CDCException
     */
    public CWorkbook(URL template, String idHeader) throws CDCException {
        this(template, idHeader, XFormat.XLSX);
    }

    /**
     * 
     * @param template
     * @param idHeader
     * @param xFormat
     * @throws CDCException 
     */
    public CWorkbook(URL template, String idHeader, XFormat xFormat) throws CDCException {
        this(template, xFormat);
        this.idHeader = idHeader;
        try {
            createHeader();
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @return 
     */
    public Workbook getWorkbook() {
        return workbook;
    }

    /**
     * 
     * @param idHeader
     * @throws CDCException 
     */
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

        cells = new ArrayList<CCell>();
        /*** HEADERS ***/
        idxCell = -1;
        while (iColumn.hasNext()) {
            columnDef(row, iColumn.next());
        }
    }

    private void columnDef(Row row, Element column) throws CDCException {
        CCell c = new CCell(column.getAttribute("name").getValue());
        /*** VARS ***/
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(TypeCast.toShort(1));
        style.setBorderTop(TypeCast.toShort(1));
        style.setBorderLeft(TypeCast.toShort(1));
        style.setBorderRight(TypeCast.toShort(1));

        Font font = workbook.createFont();
        Cell cell = null;
        /*** COL INDEX INCREMENT, DEFAULT 1 ***/
        BigInteger cindexinc = (column.getAttribute("cindexinc") == null) ? null : TypeCast.toBigInteger(column.getAttribute("cindexinc").getValue());
        if (cindexinc != null) {
            idxCell += cindexinc.intValue();
        } else {
            idxCell++;
        }
        /*** COL INDEX ***/
        BigInteger cindex = (column.getAttribute("cindex") == null) ? null : TypeCast.toBigInteger(column.getAttribute("cindex").getValue());
        if (cindex != null) {
            idxCell = cindex.intValue();
        }
        logger.info("Cell: " + CellReference.convertNumToColString(idxCell) + idxRow);
        /*** ROW INDEX ***/
        BigInteger rindex = (column.getAttribute("rindex") == null) ? null : TypeCast.toBigInteger(column.getAttribute("rindex").getValue());
        if (rindex != null) {
            row = sheet.getRow(rindex.intValue());
        }
        cell = row.createCell(idxCell);
        /*** ROW SPAN (MERGED ROW) ***/
        BigInteger rowspan = (column.getAttribute("rowspan") == null) ? null : TypeCast.toBigInteger(column.getAttribute("rowspan").getValue());
        if (rowspan != null) {
            logger.info("Rows a cell should span: " + idxCell + " to " + (cell.getRowIndex() + rowspan.intValue() - 1));
            CellRangeAddress cra = new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex() + rowspan.intValue() - 1, idxCell, idxCell);
            RegionUtil.setBorderBottom(1, cra, sheet, workbook);
            RegionUtil.setBorderTop(1, cra, sheet, workbook);
            RegionUtil.setBorderLeft(1, cra, sheet, workbook);
            RegionUtil.setBorderRight(1, cra, sheet, workbook);
            sheet.addMergedRegion(cra);
        }
        /*** COL SPAN (MERGED CELL) ***/
        BigInteger colspan = (column.getAttribute("colspan") == null) ? null : TypeCast.toBigInteger(column.getAttribute("colspan").getValue());
        if (colspan != null) {
            logger.info("Columns a cell should span: " + cell.getColumnIndex() + " to " + (cell.getColumnIndex() + colspan.intValue() - 1));
            CellRangeAddress cra = new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex(), idxCell, cell.getColumnIndex() + colspan.intValue() - 1);
            RegionUtil.setBorderBottom(1, cra, sheet, workbook);
            RegionUtil.setBorderTop(1, cra, sheet, workbook);
            RegionUtil.setBorderLeft(1, cra, sheet, workbook);
            RegionUtil.setBorderRight(1, cra, sheet, workbook);
            sheet.addMergedRegion(cra);
        }
        /*** ALIGMENT ***/
        String alignment = (column.getAttribute("alignment") == null) ? null : column.getAttribute("alignment").getValue();
        if (!TypeCast.isNullOrEmpty(alignment)) {
            if (alignment.equals("alCenter")) {
                style.setAlignment(CellStyle.ALIGN_CENTER);
            } else if (alignment.equals("alCenterSelection")) {
                style.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
            } else if (alignment.equals("alFill")) {
                style.setAlignment(CellStyle.ALIGN_FILL);
            } else if (alignment.equals("alGeneral")) {
                style.setAlignment(CellStyle.ALIGN_GENERAL);
            } else if (alignment.equals("alJustify")) {
                style.setAlignment(CellStyle.ALIGN_JUSTIFY);
            } else if (alignment.equals("alLeft")) {
                style.setAlignment(CellStyle.ALIGN_LEFT);
            } else if (alignment.equals("alRight")) {
                style.setAlignment(CellStyle.ALIGN_RIGHT);
            }
        }

        /*** VERTICAL ALIGMENT ***/
        String valignment = (column.getAttribute("valignment") == null) ? null : column.getAttribute("valignment").getValue();
        if (!TypeCast.isNullOrEmpty(valignment)) {
            if (valignment.equals("alBottom")) {
                style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
            } else if (valignment.equals("alCenter")) {
                style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            } else if (valignment.equals("alJustify")) {
                style.setVerticalAlignment(CellStyle.VERTICAL_JUSTIFY);
            } else if (valignment.equals("alTop")) {
                style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
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
            style.setFillPattern(TypeCast.toShort(FillPatternType.SOLID_FOREGROUND.ordinal()));
            if (TypeCast.toShortD(background) != null) {
                style.setFillForegroundColor(TypeCast.toShortD(background));
            } else {
                style.setFillForegroundColor(TypeCast.toShort(TypeCast.GF("org.apache.poi.hssf.util.HSSFColor$" + background.toUpperCase(), "index")));
            }
        }
        /*** WIDTH ***/
        BigDecimal width = (column.getAttribute("width") == null) ? null : TypeCast.toBigDecimal(column.getAttribute("width").getValue());
        if (width != null) {
            width = TypeCast.toBigDecimal(width.doubleValue() * ((xFormat == XFormat.XLSX) ? 1308.90 : 1508.90));
            sheet.setColumnWidth(idxCell, width.intValue());
        }

        /*** HEIGHT ***/
        BigDecimal height = (column.getAttribute("height") == null) ? null : TypeCast.toBigDecimal(column.getAttribute("height").getValue());
        if (height != null) {
            height = TypeCast.toBigDecimal(height.doubleValue() * ((xFormat == XFormat.XLSX) ? 1308.90 : 1508.90));
            row.setHeight(height.shortValue());
        }

        /*** FONT STYLE ***/
        Boolean bold = (column.getAttribute("bold") == null) ? TypeCast.toBoolean("false") : TypeCast.toBoolean(column.getAttribute("bold").getValue());
        if (bold) {
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        }
        BigInteger size = (column.getAttribute("size") == null) ? null : TypeCast.toBigInteger(column.getAttribute("size").getValue());
        if (size != null) {
            font.setFontHeightInPoints(size.shortValue());
        }
        String fcolor = (column.getAttribute("fcolor") == null) ? null : column.getAttribute("fcolor").getValue();
        if (!TypeCast.isNullOrEmpty(fcolor)) {
            if (TypeCast.toShortD(fcolor) != null) {
                font.setColor(TypeCast.toShortD(fcolor));
            } else {
                // org.apache.poi.hssf.util.HSSFColor.
                font.setColor(TypeCast.toShort(TypeCast.GF("org.apache.poi.hssf.util.HSSFColor$" + fcolor.toUpperCase(), "index")));
            }
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
        /*** VALUE CELL ***/
        c.setRValue((column.getAttribute("rvalue") == null) ? null : column.getAttribute("rvalue").getValue());
        /*** CALCULATE CELL VALUE ***/
        c.setCalculateValue((column.getAttribute("calculateValue") == null) ? false : TypeCast.toBoolean(column.getAttribute("calculateValue").getValue()));
        /*** BEAN ***/
        c.setBean((column.getAttribute("bean") == null) ? null : column.getAttribute("bean").getValue());
        /*** BEAN OPERATION ***/
        c.setBeanOperation((column.getAttribute("beanOperation") == null) ? null : column.getAttribute("beanOperation").getValue());
        if (c.getBean() != null) {
            c.setCell(cell);
        }
        style.setFont(font);
        cell.setCellStyle(style);
        if (c.isCalculateValue()) {
            if (c.getFormula() != null) {
                cell.setCellFormula(mkFormula(c.getFormula(), idxCell, null));
            }
        } else {
            cell.setCellValue(column.getValue());
        }
        if (colspan != null) {
            idxCell += colspan.intValue() - 1;
        }
        cells.add(c);
    }

    public <TEntity> void render(List<TEntity> values) throws CDCException {
        Row localRow = null;
        Object oValue = null;
        Cell cell = null;
        idxCell = -1;
        CellStyle style = null;
        for (Object value : values) {
            idxRow++;
            localRow = sheet.createRow(idxRow);
            idxCell = -1;
            for (int idx = 0; idx < cells.size(); idx++) {
                if (cells.get(idx).isRender()) {
                    idxCell++;
                    cell = localRow.createCell(idxCell);
                    if (cells.get(idx).getDataFormat() != null) {
                        /*** STYLE ***/
                        style = sheet.getWorkbook().createCellStyle();
                        style.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat(cells.get(idx).getDataFormat()));
                        cell.setCellStyle(style);
                    }
                    style = null;
                    /*** ***/
                    if (cells.get(idx).getFormula() != null) {
                        cell.setCellFormula(mkFormula(cells.get(idx).getFormula(), idxCell, value));
                    } else {
                        if (cells.get(idx).getRValue() != null) {
                            oValue = cells.get(idx).getRValue();
                        } else {
                            oValue = (cells.get(idx).isRender()) ? TypeCast.GN(value, "get" + cells.get(idx).getName()) : null;
                        }
                        if (oValue instanceof java.lang.Number) {
                            cell.setCellValue(TypeCast.toBigDecimal(oValue).doubleValue());
                        } else {
                            cell.setCellValue(TypeCast.toString(oValue));
                        }
                    }
                } else if (cells.get(idx).getBean() != null) {
                    oValue = TypeCast.GN(value, "get" + cells.get(idx).getBean());                  
                    if (oValue instanceof java.lang.Number) {
                        if (cells.get(idx).getBeanOperation() != null) {
                            if (cells.get(idx).getBeanOperation().equals("+")) {
                                cells.get(idx).getCell().setCellValue(cells.get(idx).getCell().getNumericCellValue() + TypeCast.toBigDecimal(oValue).doubleValue());
                            }
                        } else {
                            cells.get(idx).getCell().setCellValue(TypeCast.toBigDecimal(oValue).doubleValue());
                        }
                    } else {
                        cells.get(idx).getCell().setCellValue(TypeCast.toString(oValue));
                    }
                }
            }
        }
    }

    private <TEntity> String mkFormula(String formula, int idxCol, TEntity value) throws CDCException {
        formula = formula.replaceAll("\\{row\\}", "" + (idxRow + 1));
        formula = formula.replaceAll("\\{col\\}", CellReference.convertNumToColString(idxCol));
        formula = formulaCheckCol(formula, idxCol);
        formula = formulaBean(formula, value);
        return formula;
    }

    private <TEntity> String formulaBean(String fm, TEntity value) throws CDCException {
        if (value == null) {
            return fm;
        }
        int posOpenBean = fm.indexOf("{bean:");
        if (posOpenBean == -1) {
            return fm;
        }
        int posCloseBean = fm.indexOf("}", posOpenBean);
        if (posCloseBean == -1) {
            return fm;
        }
        String tk = fm.substring(posOpenBean, posCloseBean + 1);
        tk = Pattern.quote(tk);
        String bean = fm.substring(posOpenBean + 6, posCloseBean);
        Object oValue = TypeCast.GN(value, "get" + bean);
        fm = fm.replaceFirst(tk, TypeCast.toString(oValue));
        return formulaBean(fm, value);
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
                fm = fm.substring(0, posCol + 4) + fm.substring(posCol + 5);
                count--;
            }
        }
        fm = fm.replaceAll("\\{col\\}", CellReference.convertNumToColString(idxCol + count));
        return formulaCheckCol(fm, idxCol);
    }

    /**
     *
     * @param file
     */
    public void save(File file) {
        try {
            OutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
        } catch (Exception ex) {
            logger.error("Error guarando reporte " + file, ex);
        }
    }

    /**
     * 
     * @param fos
     */
    public void save(OutputStream fos) {
        try {
            workbook.write(fos);
            fos.close();
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
