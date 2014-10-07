/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Oct 7, 2014 at 9:21:22 AM
 * @place: Ciudad de México, México
 * @company: Codicentro
 * @web: http://www.codicentro.net
 * @className: FileUtilException.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package net.codicentro.core.exceptions;

public class FileUtilException extends Exception {

    public FileUtilException() {
    }

    public FileUtilException(String message) {
        super(message);
    }

    public FileUtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileUtilException(Throwable cause) {
        super(cause);
    }

    public FileUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
