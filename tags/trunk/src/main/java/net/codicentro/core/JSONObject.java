/*
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Mar 25, 2008, 10:58:26 AM
 * Place: Querétaro, Querétaro, México.
 * Company: Codicentro©
 * Web: http://www.codicentro.net
 * Class Name: JSONObject.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Mar 25, 2008           Alexander Villalobos Yadró           1. New class.
 **/
package net.codicentro.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONObject implements Serializable {

    private Map<String, Object> json = null;

    /**
     *
     */
    public JSONObject() {
        json = new HashMap<String, Object>();
    }

    public void clear() {
        json = new HashMap<String, Object>();
    }

    /**
     *
     * @param key
     * @param o
     * @throws CDCException
     */
    public void put(String key, Object o) throws CDCException {
        if (o == null) {
            json.put(key, null);
        } else if (TypeCast.ifNumber(o) || o instanceof Boolean) {
            json.put(key, o);
        } else {
            json.put(key, quote(TypeCast.toString(o)));
        }
    }

    public static String encodeValue(Object value) throws CDCException {
        if (value == null) {
            return null;
        } else if (TypeCast.ifNumber(value) || value instanceof Boolean) {
            return TypeCast.toString(value);
        } else {
            return "\""+ quote(TypeCast.toString(value))+"\"";
        }
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
    private static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        int len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        //  sb.append('"');
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
                    if (c < ' ' || c >= 128) {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        //   sb.append('"');
        return sb.toString();
    }

    public Map<String, Object> getJson() {
        return json;
    }

    public void setJson(Map<String, Object> json) {
        this.json = json;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        Iterator<String> keys = json.keySet().iterator();
        StringBuilder sb = null;
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            if (sb == null) {
                sb = new StringBuilder("{");
                sb.append(key).append(":").append(json.get(key));
            } else {
                sb.append(",").append(key).append(":").append(json.get(key));
            }
        }
        if (sb == null) {
            return null;
        } else {
            sb.append("}");
            return charSpecial(sb.toString());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JSONObject other = (JSONObject) obj;
        if (this.json != other.json && (this.json == null || !this.json.equals(other.json))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.json != null ? this.json.hashCode() : 0);
        return hash;
    }
}
