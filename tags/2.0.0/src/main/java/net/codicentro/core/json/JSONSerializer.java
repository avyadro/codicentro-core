/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 18/02/2011 at 09:10:54 AM
 * @place: Toluca, Estado de México, México
 * @company: Codicentro©
 * @web: http://www.codicentro.net
 * @className: JSONSerializer.java
 * @purpose: Revisions: Ver Date Author Description --------- ---------------
 * ----------------------------------- ------------------------------------
 *
 */
package net.codicentro.core.json;

import net.codicentro.core.CDCException;
import net.codicentro.core.TypeCast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONSerializer implements Serializable {

    private List<TInclude> includes = null;

    public JSONSerializer() {
        includes = new ArrayList<TInclude>();
    }

    public void include(String propertyName) {
        includes.add(new TInclude(propertyName));
    }

    public void include(String propertyName, String alias) {
        includes.add(new TInclude(propertyName, alias));
    }

    public <TEntity> String toJSON(List<TEntity> entities) throws CDCException {
        StringBuilder sb = new StringBuilder();
        for (TEntity entity : entities) {
            sb.append(",{").append(toJSON(entity)).append("}");
        }
        if (TypeCast.isBlank(sb.toString())) {
            return "[]";
        } else {
            return "[" + sb.toString().substring(1) + "]";
        }
    }

    public <TEntity> String toJSON(TEntity entity) throws CDCException {
        StringBuilder sb = new StringBuilder();
        String comma = "";
        for (TInclude include : includes) {
            Object value = TypeCast.GN(entity, "get" + TypeCast.toFirtUpperCase(include.path(0)));
            if ((value != null) && (include.size() > 1)) {
                for (int i = 1; ((i < include.size()) && (value != null)); i++) {
                    value = TypeCast.GN(value, "get" + TypeCast.toFirtUpperCase(include.path(i)));
                }
            }
            sb.append(comma).append("\"").append(include.field()).append("\":");
            sb.append(factoryValue(value));
            comma = ",";
        }
        return sb.toString();
    }

    private Object factoryValue(Object value) {
        if (value == null) {
            return null;
        } else {
            if ((value instanceof Boolean) || (value instanceof Number)) {
                return value;
            } else {
                return "\"" + TypeCast.toString(value).replaceAll("\\\"", "\\\\\"") + "\"";
            }
        }
    }

    public <TEntity> String toJSON(Map<String, ?> map, TEntity entity) throws CDCException {
        StringBuilder sb = new StringBuilder("{");
        String comma = "";
        for (String key : map.keySet()) {
            /**
             * KEY
             */
            sb.append(comma).append("\"").append(key).append("\":");
            /**
             * VALUE
             */
            sb.append(factoryValue(map.get(key)));
            comma = ",";
        }
        sb.append(comma).append(toJSON(entity));
        sb.append("}");
        return sb.toString();
    }
}
