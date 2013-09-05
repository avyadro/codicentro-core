/*
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Apr 06, 2009, 11:01:56 AM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro©
 * Web: http://www.codicentro.net
 * Class Name: CDCException.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Apr 06, 2009      Alexander Villalobos Yadró           1. New class.
 **/
package net.codicentro.core;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Messages {

    private int code;
    private ArrayList params;

    /**
     *
     * @param str String format
     * <code>[|param1|param2|param3...|paramn]
     */
    public Messages(String str) throws CDCException {
        StringTokenizer st = new StringTokenizer(str, "|");
        int i = 0;
        params = null;
        while (st.hasMoreTokens()) {
            if (i == 0) {
                code = TypeCast.toInt(st.nextToken());
            } else {
                if (params == null) {
                    params = new ArrayList();
                    params.add(st.nextToken());
                } else {
                    params.add(st.nextToken());
                }
            }
            i++;
        }
    }

    /**
     *
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     *
     * @return
     */
    public Object[] getParams() {
        return params.toArray();
    }
}
