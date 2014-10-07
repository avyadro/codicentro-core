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
import java.io.IOException;
import java.util.Iterator;
import net.codicentro.core.exceptions.FileUtilException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

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
    private final static Character CSV_SEPARATOR = ',';

    public static void excelToCSV(File input, File output) throws FileUtilException {
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(input);
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.iterator();
            StringBuilder csv = null;
            while (rowIterator.hasNext()) {
                org.apache.poi.ss.usermodel.Row row = rowIterator.next();
                Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator();
                if (csv == null) {
                    csv = new StringBuilder();
                } else {
                    csv.append("\r\n");
                }
                while (cellIterator.hasNext()) {
                    org.apache.poi.ss.usermodel.Cell cell = cellIterator.next();
                    switch (cell.getCellType()) {
                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
                            csv.append(cell.getBooleanCellValue());
                            break;
                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                            csv.append(cell.getNumericCellValue());
                            break;
                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                            csv.append(cell.getStringCellValue());
                            break;
                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK:
                            csv.append("");
                            break;
                        default:
                            csv.append(cell);
                    }// Switch
                    if (cell.getColumnIndex() + 1 < row.getLastCellNum()) {
                        csv.append(CSV_SEPARATOR);
                    }
                }// Cell iterator
            }// Row iterator
            if (csv == null) {
                csv = new StringBuilder("<file is empty>");
            }
            org.apache.commons.io.FileUtils.writeStringToFile(output, csv.toString());
        } catch (IOException ex) {
            throw new FileUtilException(ex);
        } catch (InvalidFormatException ex) {
            throw new FileUtilException(ex);
        }
    }

}
