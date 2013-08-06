/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Jan 24, 2012 at 4:22:04 PM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: TreeGrid.java
 * @purpose: Revisions: 
 * Ver Date Author Description 
 * ----------------------------------- ------------------------------------
 *
 */
package com.codicentro.core.extjs;

import com.codicentro.core.CDCException;
import com.codicentro.core.TypeCast;
import com.codicentro.core.json.JSONSerializer;
import java.io.Serializable;
import java.util.*;

public class TreeGrid implements Serializable {

    private List<?> tree;
    private String[] id;
    private String[] parent;
    private String[] level;
    private Integer levelValue;
    private JSONSerializer json;
    private Tree.TOperator expOperator;
    private boolean leaf = false;
    private String leafExpression;
    private String[] expName;
    private String expCompare;

    public TreeGrid(String idField, String parentField) {
        id = factory(idField);
        parent = factory(parentField);
        json = new JSONSerializer();
    }

    public TreeGrid(String idField, String parentField, String levelField) {
        this(idField, parentField);
        level = factory(levelField);
    }

    private String[] factory(String value) {
        String[] field = value.split("\\.");
        String[] factory = new String[field.length];
        for (int i = 0; i < field.length; i++) {
            factory[i] = "get" + TypeCast.toFirtUpperCase(field[i]);
        }
        return factory;
    }

    public void include(String path) {
        json.include(path);
    }

    public void include(String path, String alias) {
        json.include(path, alias);
    }

    public String render() throws CDCException {
        if (tree == null || tree.isEmpty()) {
            return "[]";
        }
        Map<String, Integer> mpLevel = new HashMap<String, Integer>();
        StringBuilder rs = new StringBuilder("[");
        String comma = "";
        Iterator<?> iTree = tree.iterator();
        while (iTree.hasNext()) {
            Object entity = iTree.next();
            Map<String, Object> obj = new HashMap<String, Object>();

            /**
             * * ID **
             */
            Object idValue = TypeCast.GN(entity, id[0]);
            for (int i = 1; i < id.length; i++) {
                idValue = TypeCast.GN(idValue, id[i]);
            }
            obj.put("id", idValue);
            /**
             * * PARENT **
             */
            Object parentValue = TypeCast.GN(entity, parent[0]);
            for (int i = 1; i < parent.length; i++) {
                parentValue = TypeCast.GN(parentValue, parent[i]);
            }
            obj.put("parentId", parentValue);

            /**
             ** LEVEL ** El nivel en el arbol es determinado en el orde de la
             * consulta.Si se define un levelField, entonces este tiene
             * prioridad sobre todos los demas, luego el levelValue que se
             * define por el usuario y por último el que puede determinarse de
             * forma automática.
             *
             */
            if (level != null) {
                Object lvlValue = TypeCast.GN(entity, level[0]);
                for (int i = 1; i < level.length; i++) {
                    lvlValue = TypeCast.GN(lvlValue, level[i]);
                }
                obj.put("level", lvlValue);
            } else if (levelValue != null) {
                obj.put("level", levelValue);
            } else {
                if (mpLevel.containsKey("ID-" + parentValue)) {
                    obj.put("level", mpLevel.get("ID-" + parentValue) + 1);
                } else {
                    mpLevel.put("ID-" + parentValue, 1);
                    obj.put("level", 1);
                }
            }

            /**
             * * LEAF **
             */
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
                obj.put("leaf", leaf);
            } else {
                obj.put("leaf", leaf);
            }
            rs.append(comma).append(json.toJSON(obj, entity));
            comma = ",";
        }
        rs.append("]");
        return rs.toString();
    }

    public List<?> getTree() {
        return tree;
    }

    public void setTree(List<?> tree) {
        this.tree = tree;
    }

    public Integer getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(Integer levelValue) {
        this.levelValue = levelValue;
    }

    /**
     * @param iconClsField the iconClsField to set
     */
    public void setIconClsField(String iconClsField) {
        include(iconClsField, "iconCls");
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
        expOperator = Tree.TOperator.EQ;
        if (leafExpression.indexOf("isBlank") != -1) {
            expOperator = Tree.TOperator.ISNULLOREMPTY;
            tk = "isBlank";
        } else if (leafExpression.indexOf("isNull") != -1) {
            expOperator = Tree.TOperator.ISNULL;
            tk = "isNull";
        } else if (leafExpression.indexOf("isEmpty") != -1) {
            expOperator = Tree.TOperator.ISEMPTY;
            tk = "isEmpty";
        } else if (leafExpression.indexOf("isTrue") != -1) {
            expOperator = Tree.TOperator.ISTRUE;
            tk = "isTrue";
        } else if (leafExpression.indexOf("isFalse") != -1) {
            expOperator = Tree.TOperator.ISFALSE;
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
}
