/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Sep 20, 2005, 10:27:26 AM
 * Place: Querétaro, Querétaro, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: Table.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Sep 20, 2005           Alexander Villalobos Yadró           1. New class.
 **/
package com.codicentro.core.model;

import com.codicentro.core.CDCException;
import com.codicentro.core.TypeCast;
import com.codicentro.core.Types.SQLType;
import com.codicentro.core.Types.TableType;
import com.codicentro.core.Types.XLSDataType;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class Table implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ArrayList table = null;
    private ArrayList columns = null;
    private int index = -1;
    private int start;
    private int limit;
    private TableType tableType = TableType.NORMAL;

    /**
     *
     * @param start
     */
    public void setPagingStart(int start) {
        this.start = start;
    }

    public int getPage() {
        return start;
    }

    /**
     *
     * @param limit
     */
    public void setPagingLimit(int limit) {
        this.limit = limit;
    }

    public int getPageSize() {
        return limit;
    }

    /**
     *
     */
    public Table() {
        table = null;
        columns = null;
        index = -1;
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public int search(String key, Integer value) throws CDCException {
        int res = -1;
        try {
            for (int i = 0; (i < table.size()) && (res == -1); ++i) {
                res = (getValue(i, key).equals(value)) ? i : -1;
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return res;
    }

    /**
     * Search and remove no found
     *
     * @param key
     * @param value
     * @return new Object table
     */
    public Table filter(String key, Integer value) throws CDCException {
        Table result = new Table();
        try {
            result.setColumns(columns);
            result.setValues(new ArrayList());
            for (int i = 0; (i < table.size()); ++i) {
                if (getValue(i, key).equals(value)) {
                    result.addValue(table.get(i));
                }
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     *
     * @param keys
     * @param values
     * @return
     */
    public int search(String[] keys, Object[] values) throws CDCException {
        int res = -1;
        try {
            for (int i = 0; (i < table.size()) && (res == -1); ++i) {
                boolean b = true;
                for (int j = 0; (j < keys.length) && (b); ++j) {
                    b = getStringValue(i, keys[j]).trim().equals(
                            TypeCast.toString(values[j]).trim());
                }
                res = (b) ? i : -1;
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return res;
    }

    /**
     *
     * @param key1
     * @param key2
     * @param value1
     * @param value2
     * @return
     */
    public int search(String key1, String key2, String value1, Integer value2) throws CDCException {
        int res = -1;
        try {
            for (int i = 0; (i < table.size()) && (res == -1); ++i) {
                boolean b = (getStringValue(i, key1).trim().equals(value1)) && (getIntegerValue(i, key2).equals(value2));
                res = (b) ? i : -1;
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return res;
    }

    public int search(String key, String value) throws CDCException {
        int res = -1;
        try {
            for (int i = 0; (i < table.size()) && (res == -1); ++i) {
                res = (TypeCast.toString(getValue(i, key)).trim().equals(value)) ? i : -1;
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return res;
    }

    /*
     * public Table(HSSFSheet sheet, TableType tt, ArrayList columns) throws
     * CDCException { tableType = tt; try { switch (tableType) { case
     * IFX_MULTISET: TableIFX_MULTISET(sheet, columns); break; } } catch
     * (Exception ex) { throw new CDCException(ex); } }
     */
    public Table(HSSFSheet sheet, TableType tt, ArrayList columns) throws CDCException {
        tableType = tt;
        this.columns = columns;
        try {
            switch (tableType) {
                case NORMAL:
                    TableNORMAL(sheet);
                    break;
                case JSON:
                    TableJSON(sheet);
                    break;
                case ARRAY:
                    TableARRAY(sheet);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    public Table(HSSFSheet sheet, TableType tt) throws CDCException {
        tableType = tt;
        columns = null;
        table = null;
        try {
            switch (tableType) {
                case NORMAL:
                    TableNORMAL(sheet);
                    break;
                case JSON:
                    TableJSON(sheet);
                    break;
                case ARRAY:
                    TableARRAY(sheet);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @param rs
     * @param tt
     *            Table Type
     * @throws com.codicentro.tls.CDCException
     */
    public Table(ResultSet rs, TableType tt) throws CDCException {
        tableType = tt;
        try {
            switch (tableType) {
                case NORMAL:
                    TableNORMAL(rs, null);
                    break;
                case JSON:
                    setColumns(rs, null, false);
                    TableJSON(rs);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /******************************************************************************************************************
     *                         BEGIN
     *******************************************************************************************************************/
    public Table(ResultSet rs, TableType tt, ArrayList c) throws CDCException {
        tableType = tt;
        try {
            switch (tableType) {
                case NORMAL:
                    TableNORMAL(rs, c);
                    break;
                case JSON:
                    columns = c;
                    TableJSON(rs);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    private void TableNORMAL(ResultSet rs, ArrayList c) throws CDCException {
        if (rs != null) {
            setColumns(rs, c, false);
            setValues(rs, true);
        }
    }

    /******************************************************************************************************************
     *                     END
     *******************************************************************************************************************/
    private String getCt(int t) {
        String ct = "\"";
        switch (t) {
            case Types.BOOLEAN:
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
                ct = "";
                break;
        }
        return ct;
    }

    private String getCt(String t) {
        String ct = "\"";
        switch (SQLType.valueOf(t.toUpperCase())) {
            case BOOLEAN:
            case BIGINT:
            case DECIMAL:
            case DOUBLE:
            case FLOAT:
            case INTEGER:
            case INT:
            case NUMERIC:
                ct = "";
                break;
        }
        return ct;
    }

    private String makeJSON(String str, String ct, String key, Object value) throws CDCException {
        if (!ct.equals("")) {
            value = TypeCast.cuotes(TypeCast.toString(value));
        }
        if (value != null) {
            str = (str.equals("")) ? key + ct + value + ct : "," + key + ct + value + ct;
        } else {
            str = (str.equals("")) ? key + value : "," + key + value;
        }
        return str;
    }

    /**
     *
     * @param rs
     * @throws com.codicentro.tls.CDCException
     */
    private void TableJSON(ResultSet rs) throws CDCException {
        try {
            table = new ArrayList();
            index = -1;
            String ct = "";
            String key = "";
            Object value = null;
            String str = null;
            while ((rs != null) && (rs.next())) {
                str = "";
                key = "";
                for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; ++i) {
                    ct = getCt(rs.getMetaData().getColumnType(i));
                    key = ((Column) columns.get(i - 1)).getName() + ":";
                    value = rs.getObject(i);
                    str += makeJSON(str, ct, key, value);
                }
                table.add("{" + str + "}");
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                throw new CDCException(ex);
            }
        }
    }

    /**
     *
     * @param sheet
     * @throws com.codicentro.tls.CDCException
     */
    private void TableJSON(HSSFSheet sheet) throws CDCException {
        try {
            table = new ArrayList();
            if (columns == null) {
                setColumns(sheet.getRow(sheet.getFirstRowNum()));
            }
            index = -1;
            if (sheet != null) {
                String ct = "";
                String key = "";
                Iterator e = sheet.rowIterator();
                e.next();
                while (e.hasNext()) {
                    String str = "";
                    key = "";
                    HSSFRow row = (HSSFRow) e.next();
                    for (int i = 0; i < columns.size(); ++i) {
                        ct = "";
                        if (!((Column) columns.get(i)).getType().equals("0")) {
                            ct = "\"";
                        }

                        String o = TypeCast.toString(row.getCell(i)).trim();
                        key = ((Column) columns.get(i)).getName();
                        key = "\"" + ((key.equals("(expression)")) ? "column" + i
                                : key) + "\":";
                        str += (str.equals("")) ? key + ct + o + ct : "," + key + ct + o + ct;
                    }
                    table.add("{" + str.trim() + "}");
                }
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @param sheet
     * @throws com.codicentro.tls.CDCException
     */
    private void TableARRAY(HSSFSheet sheet) throws CDCException {
        try {
            table = new ArrayList();
            if (columns == null) {
                setColumns(sheet.getRow(sheet.getFirstRowNum()));
            }
            index = -1;
            if (sheet != null) {
                String ct = "";
                Iterator e = sheet.rowIterator();
                e.next();
                while (e.hasNext()) {
                    StringBuffer str = new StringBuffer();
                    HSSFRow row = (HSSFRow) e.next();
                    for (int i = 0; i < columns.size(); ++i) {
                        ct = "'";
                        String o = TypeCast.toString(row.getCell(i), "").trim();
                        str.append("," + ct + o.trim() + ct);
                    }
                    table.add("[" + str.toString() + "]");
                }
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @param rs
     * @throws com.codicentro.tls.CDCException
     */
    /*  private void TableNORMAL(ResultSet rs) throws CDCException {
    if (rs != null) {
    setColumns(rs, false);
    setValues(rs, true);
    }
    }*/
    /**
     *
     * @param sheet
     * @throws com.codicentro.tls.CDCException
     */
    private void TableNORMAL(HSSFSheet sheet) throws CDCException {
        if (sheet != null) {
            if (columns == null) {
                setColumns(sheet.getRow(sheet.getFirstRowNum()));
            }
            setValues(sheet);
        }
    }

    /**
     *
     * @param l
     * @throws com.codicentro.tls.CDCException
     */
    public Table(List l) throws CDCException {
        table = new ArrayList();
        setValues(l);
    }

    /**
     *
     * @param xml
     * @param elementNameByNs
     * @param nameSpaces
     * @throws com.codicentro.tls.CDCException
     */
    public Table(String xml, String[] elementNameByNs, String[] nameSpaces) throws CDCException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new StringReader(xml));
            Element e = doc.getRootElement();
            for (int i = 0; i < nameSpaces.length - 1; ++i) {
                e = e.getChild(elementNameByNs[i], doc.getRootElement().getNamespace(nameSpaces[i]));
            }

            table = new ArrayList();
            setValues(e.getChildren(
                    elementNameByNs[(elementNameByNs.length - 1)], doc.getRootElement().getNamespace(
                    nameSpaces[(nameSpaces.length - 1)])));
        } catch (Exception e) {
            throw new CDCException(e);
        }
    }

    /**
     *
     * @param l
     * @throws com.codicentro.tls.CDCException
     */
    private void setValues(List l) throws CDCException {
        HashMap hm = null;
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            List l1 = ((Element) iter.next()).getAttributes();
            hm = new HashMap();
            for (int i = 0; i < l1.size(); ++i) {
                Attribute att = (Attribute) l1.get(i);
                hm.put(att.getName().toUpperCase(), att.getValue());
            }
            table.add(hm);
        }
    }

    /**
     *
     */
    public void reset() {
        table = new ArrayList();
        columns = new ArrayList();
    }

    /**
     *
     * @return
     */
    public ArrayList getColumns() {
        return columns;
    }

    /**
     *
     * @param columns
     * @throws com.codicentro.tls.CDCException
     */
    public void setColumns(ArrayList columns) throws CDCException {
        try {
            this.columns = columns;
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @param rs Result
     * @param crs Close result set
     * @throws com.codicentro.tls.CDCException
     */
    private void setColumns(ResultSet rs, ArrayList cl, boolean crs) throws CDCException {
        columns = new ArrayList();
        Column c = null;
        try {
            if (cl == null) {
                for (int i = 0; i < rs.getMetaData().getColumnCount(); ++i) {
                    c = new Column();
                    String key = rs.getMetaData().getColumnName(i + 1);
                    key = (key.equals("(expression)")) ? "column" + (i + 1) : key;
                    c.setName(key);
                    c.setType(rs.getMetaData().getColumnType(i + 1));
                    c.setTypeName(rs.getMetaData().getColumnTypeName(i + 1));
                    columns.add(c);
                }
            } else {
                for (int i = 0; i < rs.getMetaData().getColumnCount(); ++i) {
                    c = (Column) cl.get(i);
                    c.setType(rs.getMetaData().getColumnType(i + 1));
                    c.setTypeName(rs.getMetaData().getColumnTypeName(i + 1));
                    columns.add(c);
                }
            }


        } catch (Exception ex) {
            throw new CDCException(ex);
        } finally {
            if (crs) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    throw new CDCException(ex);
                }
            }
        }
    }

    /**
     *
     * @param c
     * @throws com.codicentro.tls.CDCException
     */
    public void setColumns(Column[] c) throws CDCException {
        columns = new ArrayList();
        try {
            for (int i = 0; i < c.length; ++i) {
                // String key = c[i].getName();
                columns.add(c[i]);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @param firstRow
     * @throws com.codicentro.tls.CDCException
     */
    private void setColumns(HSSFRow firstRow) throws CDCException {
        columns = new ArrayList();
        try {
            for (int i = 0; i < firstRow.getPhysicalNumberOfCells(); ++i) {
                Column c = new Column();
                String key = TypeCast.NVL(TypeCast.toString(firstRow.getCell(i)), "(expression)");
                key = (key.equals("(expression)")) ? "column" + (i + 1) : key.trim();
                c.setName(key.trim().toUpperCase());
                c.setType(firstRow.getCell(i).getCellType());
                columns.add(c);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @param index
     * @return
     */
    public HashMap getValue(int index) throws CDCException {
        HashMap result = null;
        try {
            if ((!(table.isEmpty())) && (index < table.size())) {
                result = ((HashMap) table.get(index));
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     *
     * @param index
     * @param key
     * @return
     */
    public Object getValue(int index, String key) throws CDCException {
        Object result = null;
        try {
            if ((getValue(index) != null) && (getValue(index).containsKey(key))) {
                result = getValue(index).get(key);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    public Object getValue(String key) throws CDCException {
        Object result = null;
        try {
            if ((getValue(index) != null) && (getValue(index).containsKey(key))) {
                result = getValue(index).get(key);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     *
     * @param index
     * @param key
     * @return
     */
    public boolean getBooleanValue(int index, String key) throws CDCException {
        return TypeCast.toBoolean(getValue(index, key));
    }

    /**
     *
     * @param index
     * @param key
     * @return
     */
    public Float getFloatValue(int index, String key) throws CDCException {
        Float result = null;
        try {
            if ((getValue(index) != null) && (getValue(index).containsKey(key))) {
                result = TypeCast.toFloat(getValue(index).get(key));
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     *
     * @param index
     * @param key
     * @return
     */
    public String getStringValue(int index, String key) throws CDCException {
        String result = null;
        try {
            if ((getValue(index) != null) && (getValue(index).containsKey(key))) {
                result = TypeCast.toString(getValue(index).get(key), "");
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    public String getDateValue(int index, String key) throws CDCException {
        String result = null;
        try {
            if ((getValue(index) != null) && (getValue(index).containsKey(key))) {
                result = TypeCast.toString(getValue(index).get(key), "");
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    public String getStringValue(String key) throws CDCException {
        String result = null;
        try {
            if ((getValue(index) != null) && (getValue(index).containsKey(key))) {
                result = TypeCast.toString(getValue(index).get(key), "");
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    public HashMap getValue() throws CDCException {
        HashMap result = null;
        try {
            if ((getValue(index) != null)) {
                result = getValue(index);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }
    /*
     * Return current row value in JSON
     */

    public String getJsonValue() throws CDCException {
        String result = null;
        try {
            HashMap hm = getValue();
            Column cl = null;
            result = "";
            String ct = "";
            String value = null;
            for (int i = 0; i < colCount(); i++) {
                ct = "";
                cl = (Column) columns.get(i);
                value = TypeCast.toString(hm.get(cl.getName()));
                if (!TypeCast.ifNumber(value)) {
                    ct = "\"";
                }
                if ((value == null) || (value.equals(""))) {
                    value = "null";
                    ct = "";
                } else {
                    value = TypeCast.cuotes(value);
                }
                result += (result.equals("")) ? cl.getName() + ":" + ct + value + ct : "," + cl.getName() + ":" + ct + value + ct;
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }
    /*
     * Return all data in NORMAL table to JSON
     */

    public StringBuffer getJsonValues() throws CDCException {
        StringBuffer rs = new StringBuffer();
        try {
            String ct = "";
            String key = "";
            Object value = null;
            String str = null;
            for (int j = 0; j < rowCount(); j++) {
                str = "";
                key = "";
                for (int i = 1; i < colCount(); i++) {
                    ct = getCt(((Column) columns.get(i)).getTypeName());
                    key = ((Column) columns.get(i)).getName();
                    value = getValue(j, key);
                    key += ":";
                    str += makeJSON(str, ct, key, value);
                }
                rs.append(((j == 0) ? "" : ",") + "{" + str + "}");
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return rs;
    }

    public String getStringValue(String key, String replace) throws CDCException {
        String result = getStringValue(key);
        if ((result == null) || (result.trim().equals(""))) {
            result = replace;
        }
        return result;
    }

    public boolean hasNext() throws CDCException {
        return ((index + 1) < rowCount());
    }

    public void removeRow() {
        table.remove(index);
        index = -1;
    }

    public void next() {
        index++;
    }

    public void first() {
        index = 0;
    }

    /**
     *
     * @param index
     * @param key
     * @return
     */
    public Integer getIntegerValue(int index, String key) throws CDCException {
        Integer result = new Integer(0);
        try {
            if ((getValue(index) != null) && (getValue(index).containsKey(key))) {
                result = TypeCast.toInteger(getValue(index).get(key));
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     *
     * @param index
     * @param key
     * @return
     */
    public int getIntValue(int index, String key) throws CDCException {
        int result = 0;
        try {
            if ((getValue(index) != null) && (getValue(index).containsKey(key))) {
                result = TypeCast.toInt(getValue(index).get(key));
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    public int getIntValue(String key) throws CDCException {
        int result = 0;
        try {
            if ((getValue(index) != null) && (getValue(index).containsKey(key))) {
                result = TypeCast.toInt(getValue(index).get(key));
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     *
     * @return
     */
    public ArrayList getValues() {
        return table;
    }

    public String getStringValues() {
        switch (tableType) {
            case IFX_MULTISET:
                return table.toString().replaceAll("\\[", "").replaceAll("\\]", "");
            default:
                return table.toString();
        }
    }

    /**
     *
     * @return
     */
    public ArrayList getValues(String key) throws CDCException {
        ArrayList r = new ArrayList();
        for (int i = 0; i < rowCount(); i++) {
            r.add(getValue(i, key));
        }
        return r;
    }

    /**
     *
     * @param key
     * @param regex
     * @param replacement
     * @return
     */
    public ArrayList getValues(String key, String regex, String replacement) throws CDCException {
        ArrayList r = new ArrayList();
        for (int i = 0; i < rowCount(); i++) {
            r.add(getStringValue(i, key).replaceAll(regex, replacement));
        }
        return r;
    }

    /**
     *
     * @param key
     * @param regex
     * @param replacement
     * @return
     */
    public String getStringValues(String key, String regex, String replacement) throws CDCException {
        return getValues(key, regex, replacement).toString().replaceAll("\\[",
                "").replaceAll("\\]", "");
    }

    /**
     *
     * @param columns
     * @return
     */
    public HSSFWorkbook getWorkBook(Column[] columns) throws CDCException {
        HSSFWorkbook book = null;
        try {
            int rowNum = 0;
            book = new HSSFWorkbook();
            HSSFSheet sheet = book.createSheet("Export");

            HSSFRow row = sheet.createRow(rowNum);

            HSSFCell cell = null;
            HSSFRichTextString text = null;
            for (int i = 0; i < columns.length; i++) {
                cell = row.createCell(i);
                text = new HSSFRichTextString(TypeCast.toString(
                        columns[i].getName(), ""));
                cell.setCellValue(text);
            }

            for (int i = 0; i < rowCount(); i++) {
                rowNum++;
                row = sheet.createRow(rowNum);
                for (int c = 0; c < columns.length; c++) {
                    cell = row.createCell(c);
                    text = new HSSFRichTextString(TypeCast.NVL(getStringValue(
                            i, columns[c].getAlias()), ""));
                    cell.setCellValue(text);
                }
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return book;
    }

    /**
     *
     * @return
     */
    public ArrayList getValuesPaging() throws CDCException {
        ArrayList result = null;
        try {
            start = ((start < 0) ? 0 : start);
            limit = limit + start;
            limit = (limit > rowCount()) ? rowCount() : limit;
            result = new ArrayList(table.subList(start, limit));
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     *
     * @param value
     * @throws com.codicentro.tls.CDCException
     */
    public void addValue(HashMap value) throws CDCException {
        try {
            table.add(value);
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @param value
     * @throws com.codicentro.tls.CDCException
     */
    public void addValue(Object value) throws CDCException {
        try {
            table.add(value);
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @param table
     * @throws com.codicentro.tls.CDCException
     */
    public void setValues(ArrayList table) throws CDCException {
        try {
            this.table = table;
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @param rs Result
     * @param crs Close result set
     * @throws com.codicentro.tls.CDCException
     */
    private void setValues(ResultSet rs, boolean crs) throws CDCException {
        table = new ArrayList();
        try {
            HashMap hm = null;
            while (rs.next()) {
                hm = new HashMap();
                for (int i = 0; i < colCount(); ++i) {
                    hm.put(((Column) columns.get(i)).getName(), rs.getObject(i + 1));
                }
                table.add(hm);
            }
        } catch (SQLException ex) {
            throw new CDCException(ex);
        } finally {
            if (crs) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    throw new CDCException(ex);
                }
            }
        }

    }

    /**
     *
     * @param sheet
     * @throws com.codicentro.tls.CDCException
     */
    private void setValues(HSSFSheet sheet) throws CDCException {
        Iterator e;
        Column clm = null;
        String ct = "";
        HSSFCell cell = null;
        Object value = null;
        HashMap hm = null;
        try {
            e = sheet.rowIterator();
            table = new ArrayList();
            boolean exit = false;
            String out = null;
            if (e.hasNext()) {
                e.next();
            }
            while (e.hasNext() && !exit) {
                out = "";
                hm = new HashMap();
                HSSFRow row = (HSSFRow) e.next();
                for (int i = 0; i < columns.size(); ++i) {
                    clm = (Column) columns.get(i);
                    value = TypeCast.toString(row.getCell(i));
                    out += TypeCast.NVL(value, "");
                    switch (XLSDataType.valueOf(clm.getType())) {
                        case NUMERIC:
                            ct = "";
                            value = (TypeCast.ifNumber(value)) ? TypeCast.toBigDecimal(value) : new BigDecimal(0);
                            break;
                        case NUMERIC_TEXT:
                            ct = "'";
                            value = (TypeCast.ifNumber(value)) ? TypeCast.toBigDecimal(value) : new BigDecimal(0);
                            break;
                        case INTEGER:
                            ct = "";
                            value = (TypeCast.ifNumber(value)) ? TypeCast.toBigDecimal(value).intValue() : 0;
                            break;
                        case INTEGER_TEXT:
                            ct = "'";
                            value = (TypeCast.ifNumber(value)) ? TypeCast.toBigDecimal(value).intValue() : 0;
                            break;
                        case TEXT_CLS:
                            ct = "'";
                            value = TypeCast.toString(value, "").replaceAll("\\.0", "").trim();
                            break;
                        case TEXT:
                            ct = "";
                            value = TypeCast.toString(value, "").replaceAll("\\.0", "").trim();
                            break;
                        case DATE:
                            ct = "";
                            value = TypeCast.toString(value, "").replaceAll("\\.0", "").trim();
                            break;
                        default:
                            ct = "'";
                            value = TypeCast.toString(value, "");
                            break;
                    }
                    hm.put(clm.getName(), ct + value + ct);
                }
                exit = out.trim().equals("");
                if (!exit) {
                    table.add(hm);
                }
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @return
     */
    public int rowCount() throws CDCException {
        int result = -1;
        try {
            result = table.size();
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     *
     * @return
     */
    public int colCount() throws CDCException {
        int result = -1;
        try {
            result = columns.size();
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }
}
