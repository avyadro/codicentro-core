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
import com.codicentro.core.json.JSONSerializer;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Tree implements Serializable {

    private String[] idName = null;
    private String[] parentIdName = null;
    private String textField = null;
    private String iconClsField = null;
    private String checkedField = null;
    private String scriptField = null;
    private String scriptPathField = null;
    private String handlerField = null;
    private List<?> tree = null;
    private JSONSerializer json = null;

    public Tree(List<?> tree, String id, String parentId, String textField) {
        this.tree = tree;
        /*** ID ***/
        String[] idField = id.split("\\.");
        idName = new String[idField.length];
        for (int i = 0; i < idField.length; i++) {
            idName[i] = "get" + TypeCast.toFirtUpperCase(idField[i]);
        }
        /*** PARENT ID ***/
        String[] parentIdField = parentId.split("\\.");
        parentIdName = new String[parentIdField.length];
        for (int i = 0; i < parentIdField.length; i++) {
            parentIdName[i] = "get" + TypeCast.toFirtUpperCase(parentIdField[i]);
        }
        /*** ***/
        this.textField = textField;


        json = new JSONSerializer();
    }

    public void include(String path) {
        json.include(path);
    }

    public void include(String path, String alias) {
        json.include(path, alias);
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
        String childEmpty = "";
        String itemName = "";
        switch (rt) {
            case EXTJS_TREE:
                childEmpty = "children:[]";
                itemName = "children:";
                break;
            case EXTJS_MENU:
                childEmpty = "menu:{items:[]}";
                itemName = "menu:";
                break;
        }
        /*** INIT SERIALIZER ***/
        StringBuilder sb = new StringBuilder();
        StringBuilder item = null;
        String cc = "";////Contains childs
        int idx = 0;
        int ln = 0;
        int od = 0;

        Object idValue = null;
        Object parentValue = null;
        String textName = "get" + TypeCast.toFirtUpperCase(textField);
        Object textValue = null;
        /** OPTIONAL **/
        String iconClsName = (iconClsField == null) ? null : "get" + TypeCast.toFirtUpperCase(iconClsField);
        String checkedName = (checkedField == null) ? null : "get" + TypeCast.toFirtUpperCase(checkedField);
        String handlerName = (handlerField == null) ? null : "get" + TypeCast.toFirtUpperCase(handlerField);

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
            parentValue = TypeCast.GN(entity, parentIdName[0]);

            for (int i = 1; i < parentIdName.length; i++) {
                parentValue = TypeCast.GN(parentValue, parentIdName[i]);
            }

            /*** ***/
            textValue = TypeCast.GN(entity, textName);
            item = new StringBuilder();
            item.append("{");
            item.append("id:\"").append(idValue).append("\"");
            item.append(",").append(childEmpty);
            switch (rt) {
                case EXTJS_MENU:
                    if (handlerField != null) {
                        item.append(",handler:function(){");
                        item.append(TypeCast.GN(entity, handlerName));
                        item.append("}");
                    }
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
            item.append(",text:\"").append(textValue).append("\"");
            /** **/
            if (iconClsField != null) {
                item.append(",iconCls:\"").append(TypeCast.GN(entity, iconClsName)).append("\"");
            }
            /** **/
            if (checkedField != null) {
                item.append(",checked:").append(TypeCast.GN(entity, checkedName));
            }
            item.append(",").append(json.toJSON(entity));
            item.append("}");
            if (idValue.equals(parentValue)) {
                if (sb.toString().equals("")) {
                    sb.append(item);
                } else {
                    sb.append(",").append(item);
                }
            } else {
                idx = sb.indexOf("id:\"" + parentValue + "\",");
                ln = ("id:\"" + parentValue + "\",").length();
                if (idx != -1) {
                    if (sb.indexOf("id:\"" + parentValue + "\"," + childEmpty) != -1) {
                        sb.insert(idx + ln + itemName.length() + 1, item);
                    } else {
                        sb.insert(idx + ln + itemName.length() + 1, item + ",");
                    }
                }
            }
        }

        switch (rt) {
            case EXTJS_TREE:
                return "[" + sb.toString().replaceAll(Pattern.quote(childEmpty), "leaf:true") + "]";
            case EXTJS_MENU:
                return "[" + sb.toString() + "]";
            default:
                return null;
        }
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

    /**
     * @return the scriptField
     */
    public String getScriptField() {
        return scriptField;
    }

    /**
     * @param scriptField the scriptField to set
     */
    public void setScriptField(String scriptField) {
        this.scriptField = scriptField;
    }

    /**
     * @return the scriptPathField
     */
    public String getScriptPathField() {
        return scriptPathField;
    }

    /**
     * @param scriptPathField the scriptPathField to set
     */
    public void setScriptPathField(String scriptPathField) {
        this.scriptPathField = scriptPathField;
    }

    /**
     * @return the handlerField
     */
    public String getHandlerField() {
        return handlerField;
    }

    /**
     * @param handlerField the handlerField to set
     */
    public void setHandlerField(String handlerField) {
        this.handlerField = handlerField;
    }
}
