/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Oct 7, 2014 at 9:21:22 AM
 * @place: Ciudad de México, México
 * @company: Codicentro
 * @web: http://www.codicentro.net
 * @className: CsvException.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package net.codicentro.core.exceptions;

public class CsvException extends Exception {

    public CsvException() {
    }

    public CsvException(String message) {
        super(message);
    }

    public CsvException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvException(Throwable cause) {
        super(cause);
    }

    public CsvException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
