/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Oct 7, 2014 at 11:44:51 AM
 * @place: Ciudad de México, México
 * @company: Planet Media México
 * @web: http://www.planetmedia.com.mx
 * @className: CsvExcel.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package net.codicentro.core.csv;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import net.codicentro.core.TypeCast;
import net.codicentro.core.exceptions.CsvException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DateUtil;

public class CsvExcel {

    private final static Character FIELD_DELIMITER = ',';
    private final static Character TEXT_DELIMITER = '"';
    private String dateFormat = "dd/MM/yyyy";
    private final int BUFFER_SIZE = 1024;
    private InputStream excel;

    public CsvExcel(InputStream excel) throws CsvException {
        this.excel = new BufferedInputStream(excel, BUFFER_SIZE);
    }

    public CsvExcel(InputStream excel, String dateFormat) throws CsvException {
        this(excel);
        this.dateFormat = dateFormat;
    }

    public CsvExcel(File excel) throws CsvException {
        try {
            this.excel = new BufferedInputStream(new FileInputStream(excel), BUFFER_SIZE);
        } catch (IOException ex) {
            throw new CsvException(ex);
        }
    }

    public CsvExcel(File excel, String dateFormat) throws CsvException {
        this(excel);
        this.dateFormat = dateFormat;
    }

    public void excelToCsv(File output) throws CsvException {
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = org.apache.poi.ss.usermodel.WorkbookFactory.create(excel);
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
                            String _value;
                            if (DateUtil.isCellDateFormatted(cell)) {
                                _value = TypeCast.toString(cell.getDateCellValue(), dateFormat);
                            } else {
                                String[] _cellValue = TypeCast.toString(cell.getNumericCellValue()).split("\\.");
                                Long integerPart = TypeCast.toLong(_cellValue[0]);
                                Long decimalPart = _cellValue.length > 1 ? TypeCast.toLong(_cellValue[1]) : 0L;
                                if (decimalPart > 0) {
                                    _value = integerPart + "." + decimalPart;
                                } else {
                                    _value = "" + integerPart;
                                }
                            }
                            csv.append(_value);
                            break;
                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                            csv.append(TEXT_DELIMITER).append(cell.getStringCellValue().replaceAll("\n", " ")).append(TEXT_DELIMITER);
                            break;
                        case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK:
                            csv.append("");
                            break;
                        default:
                            csv.append(cell);
                    }// Switch
                    if (cell.getColumnIndex() + 1 < row.getLastCellNum()) {
                        csv.append(FIELD_DELIMITER);
                    }
                }// Cell iterator
            }// Row iterator
            if (csv == null) {
                csv = new StringBuilder("<file is empty>");
            }
            org.apache.commons.io.FileUtils.writeStringToFile(output, csv.toString());
        } catch (IOException ex) {
            throw new CsvException(ex);
        } catch (InvalidFormatException ex) {
            throw new CsvException(ex);
        }
    }

}
