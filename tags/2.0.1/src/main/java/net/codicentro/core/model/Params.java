/*
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Apr 23, 2006, 10:27:26 AM
 * Place: Querétaro, Querétaro, México.
 * Company: Codicentro©
 * Web: http://www.codicentro.net
 * Class Name: Params.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------ 
 **/
package net.codicentro.core.model;

import net.codicentro.core.Types.WrapperType;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author avillalobos
 */
public class Params implements Serializable {

    private List IN = null;
    private List OUT = null;
    private List columns = null;
    private WrapperType wrapperType = WrapperType.NORMAL;
    private boolean isDefineColumn = false;
}
