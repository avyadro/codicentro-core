/**
 * Author: Alexander Villalobos Yadr�
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Apr 23, 2006, 10:27:26 AM
 * Place: Quer�taro, Quer�taro, M�xico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: ParamsDB.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0      Apr 23, 2006      Alexander Villalobos Yadr�          New class.
 * 1.0.1      May 28, 2008      Alexander Villalobos Yadr�          Add new 2 constructors
 **/
package com.codicentro.core.model;


import com.codicentro.core.CDCException;
import com.codicentro.core.TypeCast;
import com.codicentro.core.Types.SQLType;
import com.codicentro.core.Types.TableType;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParamsDB {

    private String schema = null;
    private String pkg = null;
    private String spName = null;
    private ArrayList<Map<Object, Object>> IN = null;
    private ArrayList<Map<Object, Object>> OUT = null;
    private ArrayList<Column> columns = null;
    private TableType tableType = TableType.NORMAL;
    private boolean isDefineColumn = false;

    /**
     *
     */
    public ParamsDB() {
        IN = new ArrayList<Map<Object,Object>>();
        OUT = new ArrayList<Map<Object,Object>>();
        columns = new ArrayList<Column>();
    }

    /**
     * Clear all params and columns
     */
    public void clear() {
        IN.clear();
        OUT.clear();
        columns.clear();
        isDefineColumn = false;
    }

    public int outCount() {
        return OUT.size();
    }

    public int inCount() {
        return IN.size();
    }

    public int size() {
        return outCount() + inCount();
    }

    /**
     * 
     * @return
     */
    @Override
    public String toString() {
        String result = "";
        Object value = null;
        for (int i = 0; i < OUT.size(); i++) {
            value = OUT.get(i).get("VALUE");
            try {
                result += (result.equals("")) ? TypeCast.toString(value) : "," + TypeCast.toString(value);
            } catch (Exception ex) {
            }
        }

        for (int i = 0; i < IN.size(); i++) {
            value = IN.get(i).get("VALUE");
            try {
                result += (result.equals("")) ? TypeCast.toString(value) : "," + TypeCast.toString(value);
            } catch (Exception ex) {
            }
        }

        return "Routine name: " + spName + "(" + result + ")";
    }

    public void addOut(SQLType type, String idOut) throws CDCException {
        try {
            HashMap<Object, Object> hm = new HashMap<Object, Object>();
            hm.put("TYPE", type);
            hm.put("ID-OUT", ((idOut == null) ? "idOut" + OUT.size() + 1 : idOut));
            OUT.add(hm);
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    public void add(SQLType type, Object value) throws CDCException {
        try {
        	HashMap<Object, Object> hm = new HashMap<Object, Object>();
            hm.put("TYPE", type);
            hm.put("VALUE", value);
            IN.add(hm);
        } catch (Exception ex) {
            throw new CDCException(ex);
        }

    }
    /*
     *
     *
     */

    public void addDate(Object value, String f) throws CDCException {
        try {
        	HashMap<Object, Object> hm = new HashMap<Object, Object>();
            hm.put("TYPE", SQLType.DATE);
            hm.put("VALUE", TypeCast.toSqlDate(value, f));
            IN.add(hm);
        } catch (Exception ex) {
            throw new CDCException(ex);
        }

    }

    public ArrayList<Map<Object, Object>> getOUT() {
        return OUT;
    }

    public ArrayList<Map<Object, Object>> get() {
        return IN;
    }

    public HashMap<Object, Object> getOUT(int index) {
        return (HashMap<Object, Object>) OUT.get(index);
    }

    public HashMap<Object, Object> get(int index) {
        return  (HashMap<Object, Object>) IN.get(index);
    }

    public Object getValueOUT(int index) {
        return getOUT(index).get("VALUE");
    }

    public Object getValue(int index) {
        return get(index).get("VALUE");
    }

    public String getValueVarcharOUT(int index) throws CDCException {
        return TypeCast.toString(getValueOUT(index));
    }

    public String getValueVarchar(int index) throws CDCException {
        return TypeCast.toString(getValue(index));
    }

    public Integer getValueIntegerOUT(int index) throws CDCException {
        return TypeCast.toInteger(getValueOUT(index));
    }

    public Integer getValueInteger(int index) throws CDCException {
        return TypeCast.toInteger(getValue(index));
    }

    public BigDecimal getValueNumericOUT(int index) throws CDCException {
        return TypeCast.toBigDecimal(getValueOUT(index));
    }

    public BigDecimal getValueNumeric(int index) throws CDCException {
        return TypeCast.toBigDecimal(getValue(index));
    }

    public boolean getValueBooleanOUT(int index) throws CDCException {
        return TypeCast.toBoolean(getValueOUT(index));
    }

    public boolean getValueBoolean(int index) throws CDCException {
        return TypeCast.toBoolean(getValue(index));
    }

    public Date getValueDate(int index) throws CDCException {
        return (Date) getValue(index);
    }

    public String getStringOUT(int index, String key) throws CDCException {
        return TypeCast.toString(getOUT(index).get(key));
    }

    public String getString(int index, String key) throws CDCException {
        return TypeCast.toString(get(index).get(key));
    }

    public String getStringOUT(int index, String key, String r) throws CDCException {
        return TypeCast.toString(getOUT(index).get(key), r);
    }

    public String getString(int index, String key, String r) throws CDCException {
        return TypeCast.toString(get(index).get(key), r);
    }

    public SQLType getSQLTypeOUT(int index) throws CDCException {
        return SQLType.valueOf(getStringOUT(index, "TYPE", "OTHER"));
    }

    public SQLType getSQLType(int index) throws CDCException {
        return SQLType.valueOf(getString(index, "TYPE", "OTHER"));
    }

    public String getIdOut(int index) throws CDCException {
        return TypeCast.toString(getOUT(index).get("ID-OUT"));
    }

    /**
     *
     * @return
     */
    public String getPkg() {
        return (pkg == null) ? "" : pkg;
    }

    public String getCallName() {
        return (!getPkg().equals("") ? "\"" + getPkg() + "\"." : "") +
                (!getSchema().equals("") ? "\"" + getSchema() + "\"." : "") +
                (!getSpName().equals("") ? "\"" + getSpName() + "\"" : "");
    }

    /**
     *
     * @return
     */
    public String getSchema() {
        return (schema == null) ? "" : schema;
    }

    /**
     *
     * @return
     */
    public String getSpName() {
        return (spName == null) ? "" : spName;
    }

    /**
     *
     * @param string
     */
    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    /**
     *
     * @param schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     *
     * @param spName
     */
    public void setSpName(String spName) {
        this.spName = spName;
    }

    /**
     *
     * @param resultType
     */
    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    /**
     *
     * @return
     */
    public TableType getTableType() {
        return tableType;
    }

    /**
     *
     * @param column
     */
    public void addColumn(Column column) {
        isDefineColumn = true;
        columns.add(column);
    }

    /**
     * 
     * @return
     */
    public ArrayList<Column> getColumns() {
        return ((isDefineColumn) ? columns : null);
    }

    public boolean isColumnDefineByUser() {
        return isDefineColumn;
    }
}
