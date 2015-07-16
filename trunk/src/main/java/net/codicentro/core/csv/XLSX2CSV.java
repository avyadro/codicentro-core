/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Jan 27, 2015 at 11:38:45 AM
 * @place: Ciudad de México, México
 * @company: Planet Media México
 * @web: http://www.planetmedia.com.mx
 * @className: XLSX2CSV.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package net.codicentro.core.csv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.codicentro.core.TypeCast;
import net.codicentro.core.exceptions.CsvException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A rudimentary XLSX -> CSV processor modeled on the POI sample program
 * XLS2CSVmra by Nick Burch from the package
 * org.apache.poi.hssf.eventusermodel.examples. Unlike the HSSF version, this
 * one completely ignores missing rows.
 * <p/>
 * Data sheets are read using a SAX parser to keep the memory footprint
 * relatively small, so this should be able to read enormous workbooks. The
 * styles table and the shared-string table must be kept in memory. The standard
 * POI styles table class is used, but a custom (read-only) class is used for
 * the shared string table because the standard POI SharedStringsTable grows
 * very quickly with the number of unique strings.
 * <p/>
 * Thanks to Eric Smith for a patch that fixes a problem triggered by cells with
 * multiple "t" elements, which is how Excel represents different formats (e.g.,
 * one word plain and one word bold).
 *
 * @author Chris Lott
 */
public class XLSX2CSV {

    private final static Logger logger = LoggerFactory.getLogger(XLSX2CSV.class);

    /**
     * The type of the data value is indicated by an attribute on the cell. The
     * value is usually in a "v" element within the cell.
     */
    enum xssfDataType {

        BOOL,
        ERROR,
        FORMULA,
        INLINESTR,
        SSTINDEX,
        NUMBER,
    }

    /**
     * Derived from http://poi.apache.org/spreadsheet/how-to.html#xssf_sax_api
     * <p/>
     * Also see Standard ECMA-376, 1st edition, part 4, pages 1928ff, at
     * http://www.ecma-international.org/publications/standards/Ecma-376.htm
     * <p/>
     * A web-friendly version is http://openiso.org/Ecma/376/Part4
     */
    class MyXSSFSheetHandler extends DefaultHandler {

        /**
         * Table with styles
         */
        private final StylesTable stylesTable;

        /**
         * Table with unique strings
         */
        private final ReadOnlySharedStringsTable sharedStringsTable;

        /**
         * Destination for data
         */
        private final PrintStream myOutput;

        /**
         * Number of columns to read starting with leftmost
         */
        private final int minColumnCount;

        // Set when V start element is seen
        private boolean vIsOpen;

        // Set when cell start element is seen;
        // used when cell close element is seen.
        private xssfDataType nextDataType;

        // Used to format numeric cell values.
        private short formatIndex;
        private String formatString;
        private final DataFormatter formatter;

        private int thisColumn = -1;
        // The last column printed to the output stream
        private int lastColumnNumber = -1;

        // Gathers characters as they are seen.
        private final StringBuffer value;

        private Long rowCount;

        /**
         * Accepts objects needed while parsing.
         *
         * @param styles Table of styles
         * @param strings Table of shared strings
         * @param cols Minimum number of columns to show
         * @param target Sink for output
         */
        public MyXSSFSheetHandler(
                StylesTable styles,
                ReadOnlySharedStringsTable strings,
                int cols,
                PrintStream target) {
            this.stylesTable = styles;
            this.sharedStringsTable = strings;
            this.minColumnCount = cols;
            this.myOutput = target;
            this.value = new StringBuffer();
            this.nextDataType = xssfDataType.NUMBER;
            this.formatter = new DataFormatter();
            rowCount = 0L;
        }

        /**
         *
         * @return
         */
        public Long getRowCount() {
            return rowCount;
        }

        /*
         * (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if ("inlineStr".equals(name) || "v".equals(name)) {
                vIsOpen = true;
                // Clear contents cache
                value.setLength(0);
            } // c => cell
            else if ("c".equals(name)) {
                // Get the cell reference
                String r = attributes.getValue("r");
                int firstDigit = -1;
                for (int c = 0; c < r.length(); ++c) {
                    if (Character.isDigit(r.charAt(c))) {
                        firstDigit = c;
                        break;
                    }
                }
                thisColumn = nameToColumn(r.substring(0, firstDigit));
                // Set up defaults.
                this.nextDataType = xssfDataType.NUMBER;
                this.formatIndex = -1;
                this.formatString = null;
                String cellType = attributes.getValue("t");
                String cellStyleStr = attributes.getValue("s");
                if ("b".equals(cellType)) {
                    nextDataType = xssfDataType.BOOL;
                } else if ("e".equals(cellType)) {
                    nextDataType = xssfDataType.ERROR;
                } else if ("inlineStr".equals(cellType)) {
                    nextDataType = xssfDataType.INLINESTR;
                } else if ("s".equals(cellType)) {
                    nextDataType = xssfDataType.SSTINDEX;
                } else if ("str".equals(cellType)) {
                    nextDataType = xssfDataType.FORMULA;
                } else if (cellStyleStr != null) {
                    // It's a number, but almost certainly one
                    //  with a special style or format 
                    int styleIndex = Integer.parseInt(cellStyleStr);
                    XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                    this.formatIndex = style.getDataFormat();
                    this.formatString = style.getDataFormatString();
                    if (this.formatString == null) {
                        this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                    }
                }
            }
        }

        /*
         * (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            String thisStr = null;
            // v => contents of a cell
            if ("v".equals(name)) {
                // Process the value contents as required.
                // Do now, as characters() may be called more than once
                switch (nextDataType) {
                    case BOOL:
                        char first = value.charAt(0);
                        thisStr = first == '0' ? "FALSE" : "TRUE";
                        break;
                    case ERROR:
                        thisStr = "\"ERROR:" + value.toString() + '"';
                        break;
                    case FORMULA:
                        // A formula could result in a string value,
                        // so always add double-quote characters.
                        thisStr = '"' + value.toString() + '"';
                        break;
                    case INLINESTR:
                        // TODO: have seen an example of this, so it's untested.
                        XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                        thisStr = '"' + rtsi.toString() + '"';
                        break;
                    case SSTINDEX:
                        String sstIndex = value.toString();
                        try {
                            int idx = Integer.parseInt(sstIndex);
                            XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                            thisStr = '"' + rtss.toString() + '"';
                        } catch (NumberFormatException ex) {
                            myOutput.println("Failed to parse SST index '" + sstIndex + "': " + ex.toString());
                        }
                        break;
                    case NUMBER:
                        String n = value.toString();
                        if (this.formatString != null) {
                            thisStr = DateUtil.isADateFormat(0, this.formatString) ? TypeCast.toString(DateUtil.getJavaDate(Double.parseDouble(n)), dateFormat) : formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex, this.formatString);
                        } else {
                            thisStr = n;
                        }
                        break;
                    default:
                        thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
                        break;
                }

                // Output after we've seen the string contents
                // Emit commas for any fields that were missing on this row
                if (lastColumnNumber == -1) {
                    lastColumnNumber = 0;
                }
                for (int i = lastColumnNumber; i < thisColumn; ++i) {
                    myOutput.print(',');
                }

                // Might be the empty string.
                myOutput.print(thisStr);

                // Update column
                if (thisColumn > -1) {
                    lastColumnNumber = thisColumn;
                }

            } else if ("row".equals(name)) {

                // Print out any missing commas if needed
                if (minColumns > 0) {
                    // Columns are 0 based
                    if (lastColumnNumber == -1) {
                        lastColumnNumber = 0;
                    }
                    for (int i = lastColumnNumber; i < (this.minColumnCount); i++) {
                        myOutput.print(',');
                    }
                }

                // We're onto a new row
                myOutput.println();
                lastColumnNumber = -1;
                rowCount++;
            }
        }

        /**
         * Captures characters only if a suitable element is open. Originally
         * was just "v"; extended for inlineStr also.
         */
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (vIsOpen) {
                value.append(ch, start, length);
            }
        }

        /**
         * Converts an Excel column name like "C" to a zero-based index.
         *
         * @param name
         * @return Index corresponding to the specified name
         */
        private int nameToColumn(String name) {
            int column = -1;
            for (int i = 0; i < name.length(); ++i) {
                int c = name.charAt(i);
                column = (column + 1) * 26 + c - 'A';
            }
            return column;
        }

    }

    ///////////////////////////////////////
    private OPCPackage pkg;
    private PrintStream output;
    private int minColumns;
    private String dateFormat = "dd/MM/yyyy";

    /**
     * Creates a new XLSX -> CSV converter
     *
     * @param pkg The XLSX package to process
     * @param output The PrintStream to output the CSV to
     * @param minColumns The minimum number of columns to output, or -1 for no
     * minimum
     */
    public XLSX2CSV(OPCPackage pkg, PrintStream output, int minColumns) {
        this.pkg = pkg;
        this.output = new PrintStream(output);
        this.minColumns = minColumns;
    }

    public XLSX2CSV(InputStream input, OutputStream output) throws CsvException {
        try {
            pkg = OPCPackage.open(input);
            this.output = new PrintStream(output);
            minColumns = -1;
        } catch (InvalidFormatException ex) {
            throw new CsvException(ex);
        } catch (FileNotFoundException ex) {
            throw new CsvException(ex);
        } catch (IOException ex) {
            throw new CsvException(ex);
        }
    }

    /**
     * Parses and shows the content of one sheet using the specified styles and
     * shared-strings tables.
     *
     * @param styles
     * @param strings
     * @param sheetInputStream
     * @return
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     */
    public Long processSheet(
            StylesTable styles,
            ReadOnlySharedStringsTable strings,
            InputStream sheetInputStream)
            throws IOException, ParserConfigurationException, SAXException {
        InputSource sheetSource = new InputSource(sheetInputStream);
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader sheetParser = saxParser.getXMLReader();
        MyXSSFSheetHandler handler = new MyXSSFSheetHandler(styles, strings, minColumns, output);
        sheetParser.setContentHandler((ContentHandler) handler);
        sheetParser.parse(sheetSource);
        return handler.getRowCount();
    }

    private Long totalRowProcess;

    public Long getTotalRowProcess() {
        return totalRowProcess;
    }

    /**
     * Initiates the processing of the XLS workbook file to CSV.
     *
     * @throws CsvException
     */
    public void process() throws CsvException {
        try {
            logger.info("Initiates the processing of the XLS workbook file to CSV");
            totalRowProcess = 0L;
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            XSSFReader xssfReader = new XSSFReader(pkg);
            StylesTable styles = xssfReader.getStylesTable();
            XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
            while (iter.hasNext()) {
                InputStream stream = iter.next();
                totalRowProcess += processSheet(styles, strings, stream);
                stream.close();
            }
            logger.info("Finalize the processing of the XLS workbook file to CSV, " + totalRowProcess + " row(s) process");
        } catch (IOException ex) {
            throw new CsvException(ex);
        } catch (SAXException ex) {
            throw new CsvException(ex);
        } catch (OpenXML4JException ex) {
            throw new CsvException(ex);
        } catch (ParserConfigurationException ex) {
            throw new CsvException(ex);
        }
    }

}
