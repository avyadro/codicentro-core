/*
 * Author: Alexander Villalobos Yadr�
 * E-Mail: avyadro@yahoo.com.mx
 * Created on May 19, 2008, 10:27:26 AM
 * Place: Quer�taro, Quer�taro, M�xico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: Types.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core;

public class Types {

    public enum SQLType {

        SET, LIST, ARRAY, VARCHAR, INTEGER, CURSOR, DATE, DATETIME, NUMERIC, BOOLEAN, OTHER, BIGINT, DECIMAL, DOUBLE, FLOAT, INT, LVARCHAR
    }

    public enum RenderType {

        EXTJS_TREE,
        EXTJS_MENU
    }

    public enum XLSDataType {

        TEXT, NUMERIC, INTEGER,
        DATE,
        TEXT_CLS,
        NUMERIC_TEXT,
        INTEGER_TEXT,
        TEXT_CAST, NUMERIC_CAST, INTEGER_CAST, TEXT_CLS_CAST
    }

    public enum SQLParamType {

        IN, OUT, INOUT
    }

    public enum TableType {

        EXECUTE, EXECUTE_ROWS_AFFECTED, NORMAL, JSON, ARRAY, IFX_MULTISET
    }

    public enum WrapperType {

        NORMAL, JSON, ARRAY
    }

    public enum AlignmentType {

        LEFT, CENTER, RIGHT
    }

    public enum EncrypType {

        SHA1, MD5, NONE
    }

    public enum LoggerOutType {

        ALL, CONSOLE, FILE
    }

    public enum DBProtocolType {

        POSTGRESQL, MYSQL, ORACLE, SQLSERVER, DB2, INFORMIX
    }

    public enum SummaryType {

        NONE, SUM, COUNT
    }
}
