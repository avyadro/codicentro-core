/*
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Tree implements Serializable {

    public enum TOperator {

        EQ,
        ISNULL,
        ISEMPTY,
        ISNULLOREMPTY,
        ISTRUE,
        ISFALSE
    };
    private String[] idName = null;
    private String[] parentIdName = null;
    private String[] expName = null;
    private String expCompare = null;
    private TOperator expOperator = null;
    private String handlerField = null;
    private String separatorField = null;
    private List<?> tree = null;
    private JSONSerializer json = null;
    private boolean leaf = true;
    private String leafExpression = null;
    private StringBuilder properties = null;

    public Tree(String id, String parentId, String textField) {

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
        json = new JSONSerializer();
        json.include(textField, "text");
    }

    public Tree(List<?> tree, String id, String parentId, String textField) {
        this(id, parentId, textField);
        this.tree = tree;
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
        if (tree == null) {
            return "[]";
        }
        String result;
        /** **/
        String childEmpty = "";
        String itemName = "";
        /*** INIT SERIALIZER ***/
        StringBuilder sb = new StringBuilder();
        StringBuilder item;
        int idx;
        int ln;

        Object idValue;
        Object parentValue;
        Object value;
        /** OPTIONAL **/        
        String handlerName = (handlerField == null) ? null : "get" + TypeCast.toFirtUpperCase(handlerField);
        Iterator<?> iTree = tree.iterator();
        Object entity;
        String entityValue;
        while (iTree.hasNext()) {            
            entity = iTree.next();
            switch (rt) {
                case EXTJS_TREE:
                    if (!TypeCast.isBlank(leafExpression)) {
                        Object expValue = TypeCast.GN(entity, expName[0]);
                        if (expValue != null) {
                            for (int i = 1; i < expName.length; i++) {
                                expValue = TypeCast.GN(expValue, expName[i]);
                            }
                        }
                        if (expValue == null) {
                            leaf = false;
                        } else {
                            switch (expOperator) {
                                case ISNULL:
                                    leaf = (expValue == null);
                                    break;
                                case ISNULLOREMPTY:
                                    leaf = ((expValue == null) || (((Collection) expValue).isEmpty()));
                                    break;
                                case ISTRUE:
                                    leaf = TypeCast.toBoolean(expValue);
                                    break;
                                case ISFALSE:
                                    leaf = !TypeCast.toBoolean(expValue);
                                    break;
                                default:
                                    leaf = TypeCast.toString(expValue).equals(expCompare);
                                    break;
                            }
                        }
                        childEmpty = "children:[],leaf:" + leaf;
                    } else {
                        childEmpty = "children:[],leaf:default";
                    }
                    itemName = "children:";
                    break;
                case EXTJS_MENU:
                    childEmpty = "menu:{items:[]}";
                    itemName = "menu:{items:";
                    break;
            }


            /*** ***/
            idValue = TypeCast.GN(entity, idName[0]);
            for (int i = 1; i < idName.length; i++) {
                idValue = TypeCast.GN(idValue, idName[i]);
            }
            /*** ***/
            parentValue = TypeCast.GN(entity, parentIdName[0]);
            if (parentValue != null) {
                for (int i = 1; i < parentIdName.length; i++) {
                    parentValue = TypeCast.GN(parentValue, parentIdName[i]);
                }
            } else {
                parentValue = idValue;
            }

            /*** ***/
            item = new StringBuilder();
            if (getSeparatorField() != null) {
                value = TypeCast.GN(entity, "get" + TypeCast.toFirtUpperCase(getSeparatorField()));
                if (value != null) {
                    item.append("\"").append(value).append("\",");
                }
            }
            item.append("{");
            item.append("id:\"").append(idValue).append("\"");
            item.append(",").append(childEmpty);
            switch (rt) {
                case EXTJS_MENU:
                    if (handlerField != null) {
                        value = TypeCast.GN(entity, handlerName);
                        if (value != null) {
                            item.append(",handler:function(){");
                            item.append(value);
                            item.append("}");
                        }
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
            /** **/
            entityValue = json.toJSON(entity);
            if (!TypeCast.isBlank(entityValue)) {
                item.append(",").append(entityValue);
            }
            if (properties != null) {
                item.append(",").append(properties.toString());
            }
            /*
            
             */
            item.append("}");

            /*** ***/
            idx = sb.indexOf("id:\"" + parentValue + "\",");
            if ((idx == -1) || (idValue.equals(parentValue))) {
                if (sb.toString().equals("")) {
                    sb.append(item);
                } else {
                    sb.append(",").append(item);
                }
            } else {
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
        result = sb.toString();
        switch (rt) {
            case EXTJS_TREE:
                /**
                 * 
                 */
                childEmpty = "children:\\[],leaf:true";
                result = result.replaceAll(childEmpty, "leaf:true");
                childEmpty = "children:\\[],leaf:default";
                result = result.replaceAll(childEmpty, "leaf:true");
                childEmpty = "leaf:default";
                result = result.replaceAll(childEmpty, "leaf:false");
                childEmpty = "children:\\[],leaf:false";
                result = result.replaceAll(childEmpty, "leaf:false");
                return "[" + result + "]";
            case EXTJS_MENU:
                result = result.replaceAll(Pattern.quote(childEmpty), "submenu:false");
                return "[" + result + "]";
            default:
                return null;
        }
    }

    /**
     * @param iconClsField the iconClsField to set
     */
    public void setIconClsField(String iconClsField) {
        include(iconClsField, "iconCls");
    }

    /**
     * @param checkedField the checkedField to set
     */
    public void setCheckedField(String checkedField) {
        include(checkedField, "checked");
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

    /**
     * @return the separatorField
     */
    public String getSeparatorField() {
        return separatorField;
    }

    /**
     * @param separatorField the separatorField to set
     */
    public void setSeparatorField(String separatorField) {
        this.separatorField = separatorField;
    }

    /**
     * @return the leaf
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * @param leaf the leaf to set
     */
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    /**
     * @return the leafExpression
     */
    public String getLeafExpression() {
        return leafExpression;
    }

    /**
     * @param leafExpression the leafExpression to set
     */
    public void setLeafExpression(String leafExpression) {
        String tk = "equal";
        leafExpression = leafExpression.replaceAll("==", "equal");
        expOperator = TOperator.EQ;
        if (leafExpression.indexOf("isBlank") != -1) {
            expOperator = TOperator.ISNULLOREMPTY;
            tk = "isBlank";
        } else if (leafExpression.indexOf("isNull") != -1) {
            expOperator = TOperator.ISNULL;
            tk = "isNull";
        } else if (leafExpression.indexOf("isEmpty") != -1) {
            expOperator = TOperator.ISEMPTY;
            tk = "isEmpty";
        } else if (leafExpression.indexOf("isTrue") != -1) {
            expOperator = TOperator.ISTRUE;
            tk = "isTrue";
        } else if (leafExpression.indexOf("isFalse") != -1) {
            expOperator = TOperator.ISFALSE;
            tk = "isFalse";
        }
        String[] exp = leafExpression.split(tk);
        expName = exp[0].split("\\.");
        for (int i = 0; i < expName.length; i++) {
            expName[i] = "get" + TypeCast.toFirtUpperCase(expName[i].trim());
        }
        expCompare = ((exp != null) && (exp.length == 2)) ? exp[1] : null;
        this.leafExpression = leafExpression;
    }

    /**
     * 
     * @param key
     * @param value
     * @throws CDCException 
     */
    public void addProperty(String key, Object value) throws CDCException {
        if (properties == null) {
            properties = new StringBuilder();
        } else {
            properties.append(",");
        }
        properties.append(key).append(":").append(value);
    }
}
