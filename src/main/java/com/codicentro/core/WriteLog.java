/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Apr 25, 2012 at 3:58:47 PM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: WriteLog.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteLog {

    private String filePath = null;
    private String fileName = null;
    private FileWriter fw = null;

    /**
     *
     * @param filePath
     * @param processType
     */
    public WriteLog(String filePath, String processType) {
        this.filePath = filePath + TypeCast.toString(new Date(), "yyyyMMdd") + File.separator;// + processType + "/";
        fileName = this.filePath + processType + TypeCast.toString(new Date(), "-HHmmss");
    }

    /**
     *
     * @param filePath
     * @param processType
     * @param fileName
     */
    public WriteLog(String filePath, String processType, String fileName) {
        this.filePath = filePath;
        this.fileName = filePath + processType + fileName;
    }

    /**
     *
     * @param message
     */
    private void saveLog(String message, String fileType) {
        try {
            checkTreeDir(filePath);
            fw = new FileWriter(getFileName() + ((fileType == null) ? ".log" : fileType), true);
            fw.write(TypeCast.toString(new Date(), "dd/MM/yyyy HH:mm:ss") + ": " + message + "\n");
            fw.close();
        } catch (Exception ex) {
            Logger.getLogger(WriteLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param text
     */
    private void saveText(String text, String fileType) {
        try {
            checkTreeDir(filePath);
            fw = new FileWriter(getFileName() + ((fileType == null) ? ".txt" : fileType), true);
            fw.write(text + "\n");
            fw.close();
        } catch (Exception ex) {
            Logger.getLogger(WriteLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param message
     */
    public void log(String message) {
        saveLog(message, null);
    }

    /**
     *
     * @param message
     * @param fileType
     */
    public void log(String message, String fileType) {
        saveLog(message, fileType);
    }

    /**
     *
     * @param text
     */
    public void text(String text) {
        saveText(text, null);
    }

    /**
     *
     * @param text
     * @param fileType
     */
    public void text(String text, String fileType) {
        saveText(text, fileType);
    }

    /**
     *
     * @param path
     */
    public static void checkTreeDir(String path) {
        try {
            File file = new File(path);
            file.setReadable(true, false);
            file.setWritable(true, false);
            file.setExecutable(true, false);
            if (!file.isDirectory()) {
                file.mkdirs();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
}
