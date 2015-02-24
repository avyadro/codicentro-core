/**
 * Author: Alexander Villalobos Yadr
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Mar 09, 2009, 03:08:26 AM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro©
 * Web: http://www.codicentro.net
 * Class Name: Encryption.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Mar 09, 2006           Alexander Villalobos Yadró           1. New class.
 **/
package net.codicentro.core.security;


import net.codicentro.core.TypeCast;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Encryption {

    private Logger log = LoggerFactory.getLogger(Encryption.class);
    private String data;

    /**
     *
     * @param data
     */
    public Encryption(String data) {
        this.data = data;
    }

    /**
     *
     * @param algorithm
     * @return
     */
    private String EXEC(String algorithm) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(data.getBytes());
        } catch (Exception ex) {
            log.error(ex.getCause().getMessage(), ex);
        }
        return TypeCast.toString(md.digest());
    }

    /**
     *
     * @return
     */
    public String SHA1() {
        return EXEC("SHA-1");
    }

    /**
     * 
     * @return
     */
    public String MD5() {
        return EXEC("MD5");
    }
}
