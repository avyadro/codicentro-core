/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Apr 10, 2012 at 12:59:38 PM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: CWColumn.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CWColumn {

    /**
     * Support: Ext.grid.ColumnModel, Jasper
     */
    public String name() default "";

    /**
     * Support: Ext.grid.ColumnModel, Jasper
     */
    public double width() default 1.0;

    /**
     * Support: Ext.grid.ColumnModel, Jasper.
     */
    public String format() default "";

    /**
     * Support: Ext.grid.ColumnModel, Jasper.
     */
    public String header() default "";

    /**
     * Support: Ext.grid.ColumnModel, Jasper.
     */
    public String headerAlign() default "center";

    /**
     * Support: Ext.grid.ColumnModel, Jasper.
     */
    public String summaryType() default "";

    /**
     * Support: Ext.grid.ColumnModel.
     */
    public String summaryRenderer() default "";

    /**
     * Support: Ext.grid.ColumnModel.
     */
    public String renderer() default "";

    /**
     * Support: Jasper.
     */
    public String backcolor() default "#CCCCCC";

    /**
     * Support: Jasper.
     */
    public String expression() default "";

    /**
     * Support: Ext.ux.grid.GridFilters.
     */
    public boolean filter() default false;

    /**
     * Support: Ext.grid.ColumnModel, Jasper.
     */
    public String align() default "left";
        
}
