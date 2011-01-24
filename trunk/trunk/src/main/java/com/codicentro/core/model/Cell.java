/**
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 15/12/2010 at 10:13:20 AM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: Cell.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core.model;

public class Cell {

    private String name = null;
    private String formula = null;
    private String dataFormat = null;
    private boolean summary = false;
    private String summaryFormula = null;

    public Cell(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the formula
     */
    public String getFormula() {
        return formula;
    }

    /**
     * @param formula the formula to set
     */
    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     * @return the dataFormat
     */
    public String getDataFormat() {
        return dataFormat;
    }

    /**
     * @param dataFormat the dataFormat to set
     */
    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    /**
     * @return the summary
     */
    public boolean isSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(boolean summary) {
        this.summary = summary;
    }

    /**
     * @return the summaryFormula
     */
    public String getSummaryFormula() {
        return summaryFormula;
    }

    /**
     * @param summaryFormula the summaryFormula to set
     */
    public void setSummaryFormula(String summaryFormula) {
        this.summaryFormula = summaryFormula;
    }
}
