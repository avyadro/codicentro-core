/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Oct 31, 2012 at 8:15:46 AM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: Utils.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core.extjs;

import com.codicentro.core.TypeCast;
import com.codicentro.core.annotation.CWColumn;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.WordUtils;

public class Utils {

    public static Map<String, StringBuilder> toGridColumn(Class c, Map<String, StringBuilder> write, String mapping) {
        boolean close = false;
        if (write == null || write.isEmpty()) {
            write = new HashMap<String, StringBuilder>();
            write.put("column", new StringBuilder());
            write.put("field", new StringBuilder());
            write.put("model", new StringBuilder("this.cm = new Ext.grid.ColumnModel([\n"));
            write.put("filter", new StringBuilder("this.flt = new Ext.ux.grid.GridFilters({\nfilters:[\n"));
            close = true;
        }
        for (Field field : c.getDeclaredFields()) {
            if (field.getAnnotation(CWColumn.class) != null) {
                CWColumn cwc = field.getAnnotation(CWColumn.class);
                Long width = TypeCast.toLong(cwc.width() * 100);
                String header;
                if (!TypeCast.isBlank(cwc.header())) {
                    header = cwc.header();
                } else {
                    javax.persistence.Column clmn = field.getAnnotation(javax.persistence.Column.class);
                    header = (clmn != null) ? clmn.name() : field.getName();
                    header = WordUtils.capitalizeFully(header).replace("_", " ");
                }
                write.get("column").append("this.cm").append(TypeCast.toFirtUpperCase(field.getName())).append("={\n");                
                write.get("column").append("header:'").append(header).append("',\n");
                write.get("column").append("headerAlign:'").append(cwc.headerAlign()).append("',\n");
                write.get("column").append("align:'").append(cwc.align()).append("',\n");
                write.get("column").append("dataIndex:'").append(field.getName()).append("',\n");
                write.get("column").append("sortable: true,\n");
                if (!TypeCast.isBlank(cwc.summaryType())) {
                    write.get("column").append("summaryType:'").append(cwc.summaryType()).append("',\n");
                }
                if (!TypeCast.isBlank(cwc.summaryRenderer())) {
                    write.get("column").append("summaryRenderer:function(){ return '").append(cwc.summaryRenderer()).append("'; },\n");
                }

                if (!TypeCast.isBlank(cwc.renderer())) {
                    write.get("column").append("renderer:").append(cwc.renderer()).append(",\n");
                }

                write.get("column").append("width:").append(width).append("\n");
                write.get("column").append("};\n\n");
                if (!TypeCast.isBlank(mapping)) {
                    write.get("field").append("{name:'").append(field.getName()).append("',mapping:'").append(mapping).append('.').append(field.getName()).append("'},\n");
                } else {
                    write.get("field").append("'").append(field.getName()).append("',\n");
                }
                write.get("model").append("this.cm").append(TypeCast.toFirtUpperCase(field.getName())).append(",\n");
                if (cwc.filter()) {
                    write.get("filter").append("{type: 'string',dataIndex: '").append(field.getName()).append("',param:'").append(field.getName()).append("'},\n");
                }
            } else if (field.getAnnotation(javax.persistence.EmbeddedId.class) != null) {
                toGridColumn(field.getType(), write, field.getName());
            }
        }
        if (close) {
            write.get("model").append("]);");
            write.get("filter").append("]});");
        }
        return write;
    }
}
