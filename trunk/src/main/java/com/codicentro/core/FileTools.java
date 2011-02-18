/**
 * Author: Alexander Villalobos Yadr�
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Jun 11, 2006, 12:07:26 AM
 * Place: Quer�taro, Quer�taro, M�xico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: FileTools.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Jun 11, 2006           Alexander Villalobos Yadr�           1. New class.
 **/
package com.codicentro.core;

import com.codicentro.core.model.Cell;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatternFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class FileTools {

    /**
     *
     * @param req
     * @return
     */
    public static String getRootPath(HttpServletRequest req) {
        File p = new File("");

        return req.getSession().getServletContext().getRealPath("");
    }

    /**
     *
     * @return
     */
    public static String getRootPath() {
        String res = "";
        try {
            File p = new File(".");
            res = p.getCanonicalPath();
        } catch (IOException ex) {
            //Logs.setLogs("codicentro-tools", "Class:FileTools.java\nMethods:getRootPath()\n" + ex.getMessage());
        }

        return res;
    }

    /**
     *
     * @param directory
     * @return
     */
    public static boolean deleteAllFiles(String directory) {
        File f = new File(directory);
        if (!(f.exists())) {
            f.mkdirs();
        }

        String[] dirs = f.list();
        if ((dirs != null) && (dirs.length > 0)) {
            for (int i = 0; i < dirs.length; ++i) {
                f = new File(directory + File.separator + dirs[i]);
                f.delete();
            }
        }
        return true;
    }

    /**
     * 
     * @param book
     * @param sheet
     * @param row
     * @param column
     * @param idCell
     * @param idxCell
     * @param isHeader
     * @throws CDCException
     */
    private static void byTemplate(HSSFWorkbook book, HSSFSheet sheet, HSSFRow row, Element column, List<Cell> cells, int idxCell, boolean isHeader) throws CDCException {
        Cell c = new Cell(column.getAttribute("name").getValue());
        /*** VARS ***/
        HSSFCellStyle style = book.createCellStyle();
        style.setBorderBottom(TypeCast.toShort(1));
        style.setBorderTop(TypeCast.toShort(1));
        style.setBorderLeft(TypeCast.toShort(1));
        style.setBorderRight(TypeCast.toShort(1));

        HSSFFont font = book.createFont();


        HSSFCell cell = row.createCell(idxCell);
        /*** ALIGMENT ***/
        String alignment = (column.getAttribute("alignment") == null) ? null : column.getAttribute("alignment").getValue();
        if (!TypeCast.isNullOrEmpty(alignment)) {
            if (alignment.equals("alLeft")) {
                style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            } else if (alignment.equals("alCenter")) {
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            } else if (alignment.equals("alRight")) {
                style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            }
        }
        /*** BACKGROUND ***/
        Short background = (column.getAttribute("background") == null) ? null : TypeCast.toShortD(column.getAttribute("background").getValue());
        if (background != null) {
            style.setFillPattern(HSSFPatternFormatting.SOLID_FOREGROUND);
            style.setFillForegroundColor(background);
        }
        /*** WIDTH ***/
        BigDecimal width = (column.getAttribute("width") == null) ? null : TypeCast.toBigDecimal(column.getAttribute("width").getValue());
        if (width != null) {
            width = TypeCast.toBigDecimal(width.doubleValue() * 1308.90);
            sheet.setColumnWidth(idxCell, width.intValue());
        }
        /*** FONT BOLD ***/
        Boolean bold = (column.getAttribute("bold") == null) ? TypeCast.toBoolean("false") : TypeCast.toBoolean(column.getAttribute("bold").getValue());
        if (bold) {
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        }
        /*** SUMMARY ***/
        c.setSummary((column.getAttribute("summary") == null) ? TypeCast.toBoolean("false") : TypeCast.toBoolean(column.getAttribute("summary").getValue()));
        /*** SUMMARY FORMULA ***/
        c.setSummaryFormula((column.getAttribute("summaryFormula") == null) ? null : column.getAttribute("summaryFormula").getValue());

        /*** FORMULA ***/
        c.setFormula((column.getAttribute("formula") == null) ? null : column.getAttribute("formula").getValue());
        /*** DATA FORMAT ***/
        c.setDataFormat((column.getAttribute("format") == null) ? null : column.getAttribute("format").getValue());
        style.setFont(font);
        cell.setCellStyle(style);
        cell.setCellValue(column.getValue());
        cells.add(c);
    }

    private static String mkFormula(String formula, int idxRow, int idxCol) {
        formula = formula.replaceAll("\\{row\\}", "" + idxRow);
        formula = formula.replaceAll("\\{col\\}", CellReference.convertNumToColString(idxCol));
        return formula;
    }

    /**
     *
     * @param doc
     * @param idHeader
     * @param values
     * @param response
     * @param filename
     * @throws Exception
     */
    @Deprecated
    private static void exportXLS(Document doc, String idHeader, List<Map<String, Object>> values, HttpServletResponse response, String filename) throws Exception {
        /*** INITIALIZE TEMPLATE ***/
        Element root = doc.getRootElement();
        /*** INITIALIZED WORKBOOK ***/
        HSSFWorkbook book = new HSSFWorkbook();
        HSSFSheet sheet = book.createSheet();
        int idxRow = 0;
        HSSFRow row = sheet.createRow(idxRow);
        Element headers = root.getChild("headers");
        Iterator<Element> iHeader = headers.getChildren("header").iterator();
        Iterator<Element> iColumn = null;
        Element header = null;
        while ((iHeader.hasNext()) && (iColumn == null)) {
            header = iHeader.next();
            if ((header.getAttribute("name") != null) && (header.getAttribute("name").getValue().equals(idHeader))) {
                iColumn = header.getChildren("column").iterator();
            }
        }
        List<Cell> cells = new ArrayList<Cell>();
        /*** HEADERS ***/
        int idxCell = -1;
        while (iColumn.hasNext()) {
            idxCell++;
            byTemplate(book, sheet, row, iColumn.next(), cells, idxCell, true);
        }
        HSSFCell cell = null;
        Object oValue = null;
        HSSFCellStyle style = null;
        for (Map<String, Object> value : values) {
            idxRow++;
            row = sheet.createRow(idxRow);
            for (idxCell = 0; idxCell < cells.size(); idxCell++) {
                cell = row.createCell(idxCell);
                /*** STYLE ***/
                style = book.createCellStyle();
                if (cells.get(idxCell).getDataFormat() != null) {
                    style.setDataFormat(HSSFDataFormat.getBuiltinFormat(cells.get(idxCell).getDataFormat()));
                }
                cell.setCellStyle(style);
                /*** ***/
                if (cells.get(idxCell).getFormula() != null) {
                    cell.setCellFormula(mkFormula(cells.get(idxCell).getFormula(), (idxRow + 1), idxCell));
                } else {
                    oValue = value.get(cells.get(idxCell).getName());
                    if (oValue instanceof java.lang.Number) {
                        cell.setCellValue(TypeCast.toBigDecimal(oValue).doubleValue());
                    } else {
                        cell.setCellValue(TypeCast.toString(oValue));
                    }
                }
            }
        }
        /*** SUMMARY ***/
        idxRow++;
        row = sheet.createRow(idxRow);
        for (idxCell = 0; idxCell < cells.size(); idxCell++) {
            if (cells.get(idxCell).isSummary()) {
                cell = row.createCell(idxCell);
                /*** STYLE ***/
                style = book.createCellStyle();
                if (cells.get(idxCell).getDataFormat() != null) {
                    style.setDataFormat(HSSFDataFormat.getBuiltinFormat(cells.get(idxCell).getDataFormat()));
                }
                cell.setCellStyle(style);
                /*** ***/
                if (cells.get(idxCell).getSummaryFormula() != null) {
                    cell.setCellFormula(mkFormula(cells.get(idxCell).getSummaryFormula(), idxRow, idxCell));
                }
            }
        }
        exportXLS(response, book, filename);
    }

    private static <TEntity> void exportXLS(List<TEntity> values, Document doc, String idHeader, HttpServletResponse response, String filename) throws Exception {
        /*** INITIALIZE TEMPLATE ***/
        Element root = doc.getRootElement();
        /*** INITIALIZED WORKBOOK ***/
        HSSFWorkbook book = new HSSFWorkbook();
        HSSFSheet sheet = book.createSheet();
        int idxRow = 0;
        HSSFRow row = sheet.createRow(idxRow);
        Element headers = root.getChild("headers");
        Iterator<Element> iHeader = headers.getChildren("header").iterator();
        Iterator<Element> iColumn = null;
        Element header = null;
        while ((iHeader.hasNext()) && (iColumn == null)) {
            header = iHeader.next();
            if ((header.getAttribute("name") != null) && (header.getAttribute("name").getValue().equals(idHeader))) {
                iColumn = header.getChildren("column").iterator();
            }
        }
        List<Cell> cells = new ArrayList<Cell>();
        /*** HEADERS ***/
        int idxCell = -1;
        while (iColumn.hasNext()) {
            idxCell++;
            byTemplate(book, sheet, row, iColumn.next(), cells, idxCell, true);
        }
        HSSFCell cell = null;
        Object oValue = null;
        HSSFCellStyle style = null;
        for (Object value : values) {
            idxRow++;
            row = sheet.createRow(idxRow);
            for (idxCell = 0; idxCell < cells.size(); idxCell++) {
                cell = row.createCell(idxCell);
                /*** STYLE ***/
                style = book.createCellStyle();
                if (cells.get(idxCell).getDataFormat() != null) {
                    style.setDataFormat(HSSFDataFormat.getBuiltinFormat(cells.get(idxCell).getDataFormat()));
                }
                cell.setCellStyle(style);
                /*** ***/
                if (cells.get(idxCell).getFormula() != null) {
                    cell.setCellFormula(mkFormula(cells.get(idxCell).getFormula(), (idxRow + 1), idxCell));
                } else {
                    oValue = TypeCast.GN(value, "get" + cells.get(idxCell).getName());
                    if (oValue instanceof java.lang.Number) {
                        cell.setCellValue(TypeCast.toBigDecimal(oValue).doubleValue());
                    } else {
                        cell.setCellValue(TypeCast.toString(oValue));
                    }
                }
            }
        }
        /*** SUMMARY ***/
        idxRow++;
        row = sheet.createRow(idxRow);
        for (idxCell = 0; idxCell < cells.size(); idxCell++) {
            if (cells.get(idxCell).isSummary()) {
                cell = row.createCell(idxCell);
                /*** STYLE ***/
                style = book.createCellStyle();
                if (cells.get(idxCell).getDataFormat() != null) {
                    style.setDataFormat(HSSFDataFormat.getBuiltinFormat(cells.get(idxCell).getDataFormat()));
                }
                cell.setCellStyle(style);
                /*** ***/
                if (cells.get(idxCell).getSummaryFormula() != null) {
                    cell.setCellFormula(mkFormula(cells.get(idxCell).getSummaryFormula(), idxRow, idxCell));
                }
            }
        }
        exportXLS(response, book, filename);
    }

    /**
     *
     * @param template
     * @param idHeader
     * @param values
     * @param response
     * @param filename
     * @throws Exception
     */
    @Deprecated
    public static void exportXLS(File template, String idHeader, List<Map<String, Object>> values, HttpServletResponse response, String filename) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(template);
        exportXLS(doc, idHeader, values, response, filename);
    }

    /**
     *
     * @param template
     * @param idHeader
     * @param values
     * @param response
     * @param filename
     * @throws Exception
     */
    @Deprecated
    public static void exportXLS(URL template, String idHeader, List<Map<String, Object>> values, HttpServletResponse response, String filename) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(template);
        exportXLS(doc, idHeader, values, response, filename);
    }

    /**
     *
     * @param key
     * @param idHeader
     * @param values
     * @param response
     * @param filename
     * @throws Exception
     */
    @Deprecated
    public static void exportXLS(String key, String idHeader, List<Map<String, Object>> values, HttpServletResponse response, String filename) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(is(key));
        exportXLS(doc, idHeader, values, response, filename);
    }

    /**
     * 
     * @param <TEntity>
     * @param values
     * @param template
     * @param idHeader
     * @param response
     * @param filename
     * @throws Exception
     */
    public static <TEntity> void exportXLS(List<TEntity> values, File template, String idHeader, HttpServletResponse response, String filename) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(template);
        exportXLS(values, doc, idHeader, response, filename);
    }

    /**
     * 
     * @param <TEntity>
     * @param values
     * @param template
     * @param idHeader
     * @param response
     * @param filename
     * @throws Exception
     */
    public static <TEntity> void exportXLS(List<TEntity> values, URL template, String idHeader, HttpServletResponse response, String filename) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(template);
        exportXLS(values, doc, idHeader, response, filename);
    }

    /**
     * 
     * @param <TEntity>
     * @param values
     * @param key
     * @param idHeader
     * @param response
     * @param filename
     * @throws Exception
     */
    public static <TEntity> void exportXLS(List<TEntity> values, String key, String idHeader, HttpServletResponse response, String filename) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(is(key));
        exportXLS(values, doc, idHeader, response, filename);
    }

    /**
     *
     * @param key
     * @param rsc
     * @return
     * @throws Exception
     */
    public static InputStream is(String key, String rsc) throws Exception {
        ResourceBundle rb = ResourceBundle.getBundle("commons-property");
        return (new URL(rb.getString(key) + rsc)).openStream();
    }

    /**
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static InputStream is(String key) throws Exception {
        ResourceBundle rb = ResourceBundle.getBundle("commons-property");
        return (new URL(rb.getString(key))).openStream();
    }

    /**
     *
     * @param response
     * @param book
     * @param filename
     * @throws java.lang.Exception
     */
    public static void exportXLS(HttpServletResponse response, HSSFWorkbook book, String filename) throws Exception {
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Content-disposition", "attachment;filename=\"" + filename + ".xls\"");
        response.setHeader("Pragma", "public");
        response.setContentType("application/vnd.ms-excel");
        OutputStream out = response.getOutputStream();
        book.write(out);
        out.close();
    }
}
