/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on 22/07/2009, 03:24:13 PM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: Tree.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0      22/07/2009           Alexander Villalobos Yadró           New class.
 **/
package com.codicentro.core.extjs;

import com.codicentro.core.CDCException;
import com.codicentro.core.TypeCast;
import com.codicentro.core.Types.RenderType;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Tree implements Serializable {

    private String[] idField = null;
    private String[] parentField = null;
    private String textField = null;
    private String iconClsField = null;
    private String checkedField = null;
    private List<?> tree = null;

    public Tree(List<?> tree, String idField, String parentField, String textField) {
        this.tree = tree;
        this.idField = idField.split("\\.");
        this.parentField = parentField.split("\\.");
        this.textField = textField;
    }

    public String renderTree() throws CDCException {
        return make(RenderType.EXTJS_TREE);
    }

    public String renderMenu() throws CDCException {
        return make(RenderType.EXTJS_MENU);
    }

    /**
     * 
     * @return
     * @throws CDCException
     */
    private String make(RenderType rt) throws CDCException {
        /** **/
        String itemName = "";
        String wrapChild = "";
        switch (rt) {
            case EXTJS_TREE:
                itemName = "children:";
                wrapChild = ",children:[--WRAP--]";
                break;
            case EXTJS_MENU:
                itemName = "menu:";
                wrapChild = ",menu:{items:[--WRAP--]}";
                break;
        }

        StringBuilder sb = new StringBuilder();
        StringBuilder item = null;
        String cc = "";////Contains childs
        int idx = 0;
        int ln = 0;
        int od = 0;
        String[] idName = new String[idField.length];
        for (int i = 0; i < idField.length; i++) {
            idName[i] = "get" + TypeCast.toFirtUpperCase(idField[i]);
        }
        Object idValue = null;

        String[] parentName = new String[parentField.length];
        for (int i = 0; i < parentField.length; i++) {
            parentName[i] = "get" + TypeCast.toFirtUpperCase(parentField[i]);
        }

        Object parentValue = null;
        String textName = "get" + TypeCast.toFirtUpperCase(textField);
        Object textValue = null;
        /** OPTIONAL **/
        String iconClsName = (iconClsField == null) ? null : "get" + TypeCast.toFirtUpperCase(iconClsField);
        String checkedName = (checkedField == null) ? null : "get" + TypeCast.toFirtUpperCase(checkedField);

        Iterator<?> iTree = tree.iterator();
        Object entity = null;
        while (iTree.hasNext()) {
            entity = iTree.next();
            /*** ***/
            idValue = TypeCast.GN(entity, idName[0]);
            for (int i = 1; i < idName.length; i++) {
                idValue = TypeCast.GN(idValue, idName[i]);
            }
            /*** ***/
            parentValue = TypeCast.GN(entity, parentName[0]);
            for (int i = 1; i < parentName.length; i++) {
                parentValue = TypeCast.GN(parentValue, parentName[i]);
            }
            /*** ***/
            textValue = TypeCast.GN(entity, textName);
            item = new StringBuilder();
            item.append("{");
            item.append("mid:\"").append(idValue).append("\"");
            item.append(",id:\"").append(idValue).append("\"");
            item.append(",text:\"").append(textValue).append("\"");
            switch (rt) {
                case EXTJS_TREE:
                    item.append(",children:[]");
                    break;
                case EXTJS_MENU:
                    /*   scriptName = menu.getStringValue("SCRIPT_NAME", "");
                    scriptPath = menu.getStringValue("SCRIPT_PATH", "");
                    params = menu.getStringValue("PARAMS", "");
                    script = menu.getStringValue("SCRIPT", "");
                    script = script.replaceAll("\n", "");
                    if ((!scriptName.equals("")) || (!scriptPath.equals("")) || (!script.equals(""))) {
                    item.append(",handler:function(){");
                    item.append("ctrlWaitingStart();");
                    item.append(script);
                    if (!scriptName.equals("")) {
                    item.append("new File({url:\"").append(scriptPath).append(scriptName).append(".js\",method:\"include\"});");
                    item.append(TypeCast.toFirtLowerCase(scriptName)).append("=new ").append(scriptName).append("(").append(params).append(");");
                    }
                    item.append("}");
                    } */
                    break;
            }

            if (iconClsField != null) {
                item.append(",iconCls:\"").append(TypeCast.GN(entity, iconClsName)).append("\"");
            }

            if (checkedField != null) {
                item.append(",checked:").append(TypeCast.GN(entity, checkedName));
            }
            //item.append("data:{").append(tree.getJsonValue()).append("}");
            item.append("}");
            if (idValue.equals(parentValue)) {
                if (sb.toString().equals("")) {
                    sb.append(item);
                } else {
                    sb.append(",").append(item);
                }
            } else {
                idx = sb.indexOf("{mid:\"" + parentValue + "\",");
                ln = ("{mid:\"" + parentValue + "\",").length() - 1;
                if (idx != -1) {
                    if (sb.indexOf("{mid:\"" + parentValue + "\"," + itemName) == -1) {
                        sb.insert(idx + ln, wrapChild.replaceFirst("--WRAP--", item.toString()));
                    } else {
                        sb.insert(idx + ln + 11, item);
                    }
                }
            }
        }

        switch (rt) {
            case EXTJS_TREE:
                return "[" + sb.toString().replaceAll("children:\\[]", "leaf:true") + "]";
            case EXTJS_MENU:
                return "[" + sb.toString() + "]";
            default:
                return null;
        }
    }

    /**
     * @return the idField
     */
    public String[] getIdField() {
        return idField;
    }

    /**
     * @param idField the idField to set
     */
    public void setIdField(String[] idField) {
        this.idField = idField;
    }

    /**
     * @return the parentField
     */
    public String[] getParentField() {
        return parentField;
    }

    /**
     * @param parentField the parentField to set
     */
    public void setParentField(String[] parentField) {
        this.parentField = parentField;
    }

    /**
     * @return the textField
     */
    public String getTextField() {
        return textField;
    }

    /**
     * @param textField the textField to set
     */
    public void setTextField(String textField) {
        this.textField = textField;
    }

    /**
     * @return the iconClsField
     */
    public String getIconClsField() {
        return iconClsField;
    }

    /**
     * @param iconClsField the iconClsField to set
     */
    public void setIconClsField(String iconClsField) {
        this.iconClsField = iconClsField;
    }

    /**
     * @return the checkedField
     */
    public String getCheckedField() {
        return checkedField;
    }

    /**
     * @param checkedField the checkedField to set
     */
    public void setCheckedField(String checkedField) {
        this.checkedField = checkedField;
    }

    /**
     * @return the tree
     */
    public List getTree() {
        return tree;
    }

    /**
     * @param tree the tree to set
     */
    public void setTree(List tree) {
        this.tree = tree;
    }
}
