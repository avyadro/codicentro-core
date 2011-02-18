/**
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 18/02/2011 at 09:10:54 AM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: JSONSerializer.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core.json;

import com.codicentro.core.CDCException;
import com.codicentro.core.TypeCast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public <TEntity> String toJSON(List<TEntity> entities) {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    public <TEntity> String toJSON(TEntity entity) throws CDCException {
        StringBuilder sb = new StringBuilder();
        String comma = "";
        Object value = null;
        for (TInclude include : includes) {
            value = TypeCast.GN(entity, "get" + TypeCast.toFirtUpperCase(include.path(0)));
            if ((value != null) && (include.size() > 1)) {
                for (int i = 1; ((i < include.size()) && (value != null)); i++) {
                    value = TypeCast.GN(value, "get" + TypeCast.toFirtUpperCase(include.path(i)));
                }
            }
            sb.append(comma).append(include.field()).append(":\"").append(value).append("\"");
            comma = ",";
        }
        return sb.toString();
    }
}
