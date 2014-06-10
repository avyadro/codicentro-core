/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Jul 4, 2011 at 3:05:19 PM
 * @place: Toluca, Estado de México, México
 * @company: Codicentro©
 * @web: http://www.codicentro.net
 * @className: FileUtils.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package net.codicentro.core;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     *
     * @param directory
     * @return
     */
    public static boolean deleteAllFiles(String directory) {
        File f = new File(directory);
        if (!(f.exists())) {
            f.mkdirs();
        }
        String[] dirs = f.list();
        if ((dirs != null) && (dirs.length > 0)) {
            for (int i = 0; i < dirs.length; ++i) {
                f = new File(directory + File.separator + dirs[i]);
                f.delete();
            }
        }
        return true;
    }
}
