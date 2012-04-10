/**
 * @author: Alexander Villalobos Yadró @user: avillalobos @email:
 * avyadro@yahoo.com.mx @created: 15/12/2010 at 10:13:20 AM @place: Toluca,
 * Estado de México, México @company: AdeA México S.A. de C.V. @web:
 * http://www.adea.com.mx @className: CCell.java @purpose: Revisions: Ver Date
 * Author Description --------- ---------------
 * ----------------------------------- ------------------------------------
 *
 */
package com.codicentro.core.model;

import com.codicentro.core.TypeCast;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;

public class CCell {

    private String name = null;
    private String formula = null;
    private String dataFormat = null;
    private boolean summary = false;
    private boolean render = true;
    /**
     * rValue, Render value; is fixed value from template.
     */
    private Object rValue = false;
    private boolean calculateValue = false;
    private String summaryFormula = null;
    private String bean = null;
    private String beanOperation = null;
    private String beanFormula = null;
    private Map<String, String> beanFormulaNames = null;
    private Cell cell = null;

    public CCell(String name) {
        this.name = name;
    }

    public CCell(String name, boolean render) {
        this(name);
        this.render = render;
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

    /**
     * @return the render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * @param render the render to set
     */
    public void setRender(boolean render) {
        this.render = render;
    }

    /**
     * @return the calculateValue
     */
    public boolean isCalculateValue() {
        return calculateValue;
    }

    /**
     * @param calculateValue the calculateValue to set
     */
    public void setCalculateValue(boolean calculateValue) {
        this.calculateValue = calculateValue;
    }

    /**
     * @return the bean
     */
    public String getBean() {
        return bean;
    }

    /**
     * @param bean the bean to set
     */
    public void setBean(String bean) {
        this.bean = bean;
    }

    /**
     * @return the cell
     */
    public Cell getCell() {
        return cell;
    }

    /**
     * @param cell the cell to set
     */
    public void setCell(Cell cell) {
        this.cell = cell;
    }

    /**
     * @return the beanOperation
     */
    public String getBeanOperation() {
        return beanOperation;
    }

    /**
     * @param beanOperation the beanOperation to set
     */
    public void setBeanOperation(String beanOperation) {
        this.beanOperation = beanOperation;
    }

    public Object getRValue() {
        return rValue;
    }

    public void setRValue(Object rValue) {
        this.rValue = rValue;
    }

    public String getBeanFormula() {
        return beanFormula;
    }

    public Map<String, String> getBeanFormulaNames() {
        return beanFormulaNames;
    }

    public void setBeanFormula(String beanFormula) {
        this.beanFormula = beanFormula;
        if (!TypeCast.isNullOrEmpty(beanFormula)) {
            beanFormulaNames = new HashMap<String, String>();
            String[] tks = beanFormula.split("}");
            for (String tk : tks) {
                String key = tk.substring(tk.indexOf('{') + 1);
                if (!beanFormulaNames.containsKey(key)) {
                    beanFormulaNames.put(key, "\\{" + key + "}");
                }
            }
        } else {
            beanFormulaNames = null;
        }
    }
}
