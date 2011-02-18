/**
 * Author: Alexander Villalobos Yadr�
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Mar 25, 2008, 10:58:26 AM
 * Place: Quer�taro, Quer�taro, M�xico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: JSONObject.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Mar 25, 2008           Alexander Villalobos Yadr�           1. New class.
 **/
package com.codicentro.core;

import java.util.HashMap;
import java.util.Iterator;

public class JSONObject {

    private HashMap o = null;

    /**
     *
     */
    public JSONObject() {
        o = new HashMap();
    }

    public void clear() {
        o = new HashMap();
    }

    /**
     *
     * @param key
     * @param b
     */
    public void put(String key, boolean b) {
        this.o.put(key, Boolean.toString(b));
    }

    /**
     *
     * @param key
     * @param s
     */
    public void put(String key, String s) {
        this.o.put(key, (s == null) ? "null" : s);
    }

    /**
     *
     * @param key
     * @param s
     */
    public void putqt(String key, String s) {
        this.o.put(key, (s == null) ? "null" : quote(s));
    }

    /**
     *
     * @param key
     * @param i
     */
    public void put(String key, int i) {
        this.o.put(key, TypeCast.toInteger(i));
    }

    /**
     *
     * @param key
     * @param o
     */
    public void put(String key, Object o) {
        this.o.put(key, o);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        Iterator keys = this.o.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        String key = null;
        sb.append("{ver:1.0");
        while (keys.hasNext()) {
            key = keys.next().toString();
            sb.append(",").append(key).append(":").append(this.o.get(key));
        }
        sb.append("}");
        return charSpecial(sb.toString());
    }

    private String charSpecial(String r) {
        r = r.replaceAll("\r\n", "\\\\n");
        r = r.replaceAll("ñ", "\\\\361");
        r = r.replaceAll("Ñ", "\\\\321");
        r = r.replaceAll("á", "\\\\341");
        r = r.replaceAll("Á", "\\\\301");
        r = r.replaceAll("é", "\\\\361");
        r = r.replaceAll("É", "\\\\311");
        r = r.replaceAll("í", "\\\\351");
        r = r.replaceAll("Í", "\\\\315");
        r = r.replaceAll("ó", "\\\\363");
        r = r.replaceAll("Ó", "\\\\323");
        r = r.replaceAll("ú", "\\\\372");
        r = r.replaceAll("Ú", "\\\\332");
        return r;

    }

    /**
     * 
     * @param string
     * @return
     */
    private String quote(String string) {
        if ((string == null) || (string.length() == 0)) {
            return "\"\"";
        }

        int len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        sb.append('"');
        for (int i = 0; i < len; ++i) {
            char c = string.charAt(i);
            switch (c) {
                case '"':
                case '/':
                case '\\':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if ((c < ' ') || (c >= 128)) {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
