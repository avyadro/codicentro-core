/**
 * Author: Alexander Villalobos Yadro
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Dec 23, 2008, 10:27:26 AM
 * Place: Monterrey, Nuevo Leon, Mexico.
 * Company: Codicentro©
 * Web: http://www.codicentro.net
 * Class Name: TransportContext.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0       Dec 23, 2008           Alexander Villalobos Yadró      New class.
 **/
package net.codicentro.core.security;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransportContext {

    private Logger log = LoggerFactory.getLogger(TransportContext.class);

    public static void transport(HttpServletRequest request, Object value) {
        request.getSession().getServletContext().setAttribute(request.getSession().getId(), value);
    }

    public static Object recovery(HttpServletRequest request, String crossContextName, String id) {
        ServletContext sc = request.getSession().getServletContext().getContext("/" + crossContextName);
        return sc.getAttribute(id);
    }

    public static void destroy(HttpServletRequest request) {
    }
}
