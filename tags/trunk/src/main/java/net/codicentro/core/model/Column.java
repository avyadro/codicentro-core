/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Oct 23, 2008, 10:27:26 AM
 * Place: Querétaro, Querétaro, México.
 * Company: Codicentro©
 * Web: http://www.codicentro.net
 * Class Name: Column.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0       Oct 23, 2008           Alexander Villalobos Yadró      New class.
 **/
package net.codicentro.core.model;

import net.codicentro.core.CDCException;
import net.codicentro.core.TypeCast;
import net.codicentro.core.Types.XLSDataType;



public class Column {

    private String type = "undefined";
    private String typeName = "undefined";
    private String name = "undefined";
    private String alias = "undefined";

    public Column() {
    }

    public Column(String name) {
        this.name = name;
        this.alias = name;
    }

    public Column(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public Column(String name, XLSDataType type) {
        this.name = name;
        this.type = type.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(Object name) throws CDCException {
        this.name = TypeCast.toString(name);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setType(int type) {
        this.type = TypeCast.toString(type);
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
