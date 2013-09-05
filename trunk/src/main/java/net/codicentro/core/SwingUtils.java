/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Sep 2, 2013, 5:08:22 PM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: SwingUtils.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class SwingUtils {

    public static Boolean contains(DefaultTableModel model, Integer column, Object element) throws CDCException {
        return contains(model, new Integer[]{column}, new Object[]{element});
    }

    public static Boolean contains(DefaultTableModel model, Integer[] column, Object[] element) throws CDCException {
        return getData(model, column, element) != null;
    }

    public static Vector getData(DefaultTableModel model, Integer column, Object element) throws CDCException {
        return getData(model, new Integer[]{column}, new Object[]{element});
    }

    public static Vector getData(DefaultTableModel model, Integer[] column, Object[] element) throws CDCException {
        if (column == null || element == null || column.length != element.length) {
            throw new CDCException("Column or element is not valid.");
        }
        Vector rs = null;
        int idx = 0;
        while (rs == null && idx < model.getRowCount()) {
            Boolean check = true;
            for (int idxCe = 0; idxCe < column.length; idxCe++) {
                check = check && model.getValueAt(idx, column[idxCe]) != null && model.getValueAt(idx, column[idxCe]).equals(element[idxCe]);
            }
            rs = check ? (Vector) model.getDataVector().get(idx) : null;
            idx++;
        }
        return rs;
    }
}
