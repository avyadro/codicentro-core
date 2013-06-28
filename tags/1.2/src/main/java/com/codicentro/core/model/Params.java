/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codicentro.core.model;

import com.codicentro.core.Types.WrapperType;
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
