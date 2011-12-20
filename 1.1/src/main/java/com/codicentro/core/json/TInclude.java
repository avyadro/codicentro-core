/**
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 18/02/2011 at 10:14:19 AM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: TInclude.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core.json;

public class TInclude {

    private String[] path = null;
    private String alias = null;

    public TInclude(String propertyName) {
        path = propertyName.split("\\.");
        alias = path[path.length - 1];
    }

    public TInclude(String propertyName, String alias) {
        path = propertyName.split("\\.");
        this.alias = alias;
    }

    /**
     * @return the alias
     */
    public String field() {
        return alias;
    }

    public String path(int idx) {
        return path[idx];
    }

    public int size() {
        return path.length;
    }
}
