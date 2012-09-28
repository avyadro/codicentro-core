/*
 * Author: Alexander Villalobos Yadr
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Mar 09, 2009, 03:08:26 AM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: FileTools.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Mar 09, 2006           Alexander Villalobos Yadró           1. New class.
 **/
package com.codicentro.core;

import com.codicentro.core.Types.EncrypType;
import com.codicentro.core.annotation.CWColumn;
import com.codicentro.core.security.Encryption;
import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Utils {

    /**
     *
     * @return
     */
    public static String makeId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        return sdf.format(new Date());
    }

    /**
     *
     * @return
     */
    public static String makeId(EncrypType et) {
        String result = null;
        Encryption encryption = null;
        Random random = new Random(1000000);
        switch (et) {
            case SHA1:
                encryption = new Encryption(makeId() + TypeCast.toString(random.nextInt()));
                result = encryption.SHA1();
                break;
            default:
                result = makeId() + TypeCast.toString(random.nextInt());
                break;
        }
        return result;
    }

    public static int getLastDayOfMonth(final int month, final int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getLastDayOfMonth(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Date getLastDateOfMonth(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * Get the last business day of the month for a given month / year
     * combination
     *
     * @param month The month
     * @param year The year
     * @return The last business day
     */
    public static int getLastBusinessDayOfMonth(final int month, final int year, final List<Date> holidays) {
        int day = -1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        // Keep looking backwards until the day is not a weekend or a holiday
        while (true) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                continue;
            } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                calendar.add(Calendar.DAY_OF_MONTH, -2);
                continue;
            } else if ((holidays != null) && (!holidays.isEmpty()) && (holidays.contains(calendar.getTime()))) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                continue;
            }
            break;
        } // End while
        day = calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    public static int getBusinessDayOfMonth(final int month, final int year, final int day, final int nDay, final List<Date> holidays) {
        int cDay = 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        while (cDay < nDay) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                calendar.add(Calendar.DAY_OF_MONTH, -2);
            } else if ((holidays != null) && (!holidays.isEmpty()) && (holidays.contains(calendar.getTime()))) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                cDay++;
            }
        }
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     *
     * @param amount
     * @return
     */
    public static Date amountDay(int amount) {
        return amountDay(new Date(), amount);
    }

    /**
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date amountDay(Date date, int amount) {
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, amount);
        return c.getTime();
    }

    public static String xmlPrettyFormat(String xml) {
        try {
            if (TypeCast.isBlank(xml)) {
                return xml;
            }
            Document doc = DocumentHelper.parseText(xml);
            StringWriter sw = new StringWriter();
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter xw = new XMLWriter(sw, format);
            xw.write(doc);
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }

    /**
     *
     * @param source
     * @param pf, PrettyFormat
     * @param rh, Remove <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     * @param types
     * @return
     */
    public static <TEntity> String convertToXml(TEntity source, boolean pf, boolean rh, Class... types) {
        String result;
        try {
            JAXBContext context = JAXBContext.newInstance(types);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, pf);
            StringWriter sw = new StringWriter();
            marshaller.marshal(source, sw);
            result = sw.toString();
            if (rh) {
                result = result.substring("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>".length() + ((pf) ? 1 : 0));
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static <TEntity> String toJasper(Class<TEntity> clazz) {
        StringBuilder xmlField = new StringBuilder();

        StringBuilder xmlColumnHeader = new StringBuilder();
        xmlColumnHeader.append("<columnHeader>");
        xmlColumnHeader.append("<band height=\"20\">");

        StringBuilder xmlDetail = new StringBuilder();
        xmlDetail.append("<detail>");
        xmlDetail.append("<band height=\"15\">");

        Long x = 0L;
        for (Field field : clazz.getDeclaredFields()) {
            /**
             * FIELDS
             */            
            CWColumn cwc = field.getAnnotation(CWColumn.class);
            if (cwc != null) {
                Class<?> type = field.getType();
                xmlField.append("<field name=\"").append(field.getName()).append("\" class=\"").append(type.getName()).append("\"/>");
                Long width = TypeCast.toLong(cwc.width() * 100);
                String header;
                if (!TypeCast.isBlank(cwc.header())) {
                    header = cwc.header();
                } else {
                    javax.persistence.Column clmn = field.getAnnotation(javax.persistence.Column.class);
                    header = (clmn != null) ? clmn.name() : field.getName();
                }
                /**
                 * HEADERS
                 */
                xmlColumnHeader.append("<staticText>");
                xmlColumnHeader.append("<reportElement mode=\"Opaque\" x=\"").append(x).append("\" y=\"0\" width=\"").append(width).append("\" height=\"20\" backcolor=\"").append(cwc.backcolor()).append("\"/>");
                xmlColumnHeader.append("<textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">");
                xmlColumnHeader.append("<font isBold=\"true\"/>");
                xmlColumnHeader.append("</textElement>");
                xmlColumnHeader.append("<text><![CDATA[").append(header).append("]]></text>");
                xmlColumnHeader.append("</staticText>");
                /**
                 * DETAILS
                 */
                xmlDetail.append("<textField").append(TypeCast.isBlank(cwc.format()) ? "" : " pattern=\"" + cwc.format() + "\"").append(" isBlankWhenNull=\"true\">");
                xmlDetail.append("<reportElement x=\"").append(x).append("\" y=\"0\" width=\"").append(width).append("\" height=\"15\"/>");
                xmlDetail.append("<textElement/>");
                xmlDetail.append("<textFieldExpression class=\"").append(type.getName()).append("\"><![CDATA[$F{").append(field.getName()).append("}").append(cwc.expression()).append("]]></textFieldExpression>");
                xmlDetail.append("</textField>");
                x += width;
            }
        }
        xmlColumnHeader.append("</band>");
        xmlColumnHeader.append("</columnHeader>");

        xmlDetail.append("</band>");
        xmlDetail.append("</detail>");

        StringBuilder xmlJasper = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?>");
        xmlJasper.append("<jasperReport");
        xmlJasper.append(" xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"");
        xmlJasper.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        xmlJasper.append(" xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\"");
        xmlJasper.append(" name=\"").append(clazz.getSimpleName()).append("\"");
        xmlJasper.append(" pageWidth=\"").append(x).append("\"");
        xmlJasper.append(" pageHeight=\"842\"");
        xmlJasper.append(" orientation=\"Landscape\"");
        xmlJasper.append(" columnWidth=\"").append(x).append("\"");
        xmlJasper.append(" leftMargin=\"0\"");
        xmlJasper.append(" rightMargin=\"0\"");
        xmlJasper.append(" topMargin=\"0\" ");
        xmlJasper.append(" bottomMargin=\"0\"");
        xmlJasper.append(" isIgnorePagination=\"true\"");
        xmlJasper.append(">");        
        xmlJasper.append("<property name=\"ireport.zoom\" value=\"1.0\"/>");
        xmlJasper.append("<property name=\"ireport.x\" value=\"0\"/>");
        xmlJasper.append("<property name=\"ireport.y\" value=\"0\"/>");
        xmlJasper.append("<property name=\"net.sf.jasperreports.print.keep.full.text\" value=\"true\"/>");
        /**
         *
         */
        xmlJasper.append(xmlField);

        CWColumn cwcTitle = clazz.getAnnotation(CWColumn.class);
        if (cwcTitle != null) {
            StringBuilder xmlTitle = new StringBuilder();
            xmlTitle.append("<title>");
            xmlTitle.append("<band height=\"20\">");
            xmlTitle.append("<staticText>");
            xmlTitle.append("<reportElement mode=\"Opaque\" x=\"0\" y=\"0\" width=\"").append(x).append("\" height=\"20\" backcolor=\"").append(cwcTitle.backcolor()).append("\"/>");
            xmlTitle.append("<textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">");
            xmlTitle.append("<font isBold=\"true\"/>");
            xmlTitle.append("</textElement>");
            xmlTitle.append("<text><![CDATA[").append(cwcTitle.header()).append("]]></text>");
            xmlTitle.append("</staticText>");
            xmlTitle.append("</band>");
            xmlTitle.append("</title>");
            xmlJasper.append(xmlTitle);
        }

        /**
         *
         */
        xmlJasper.append(xmlColumnHeader);
        /**
         *
         */
        xmlJasper.append(xmlDetail);

        xmlJasper.append("</jasperReport>");
        return xmlJasper.toString();
    }

    public static <TEntity> TEntity convertToEntity(String xml, Class<TEntity> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return type.cast(unmarshaller.unmarshal(new StringReader(xml)));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param <C>
     * @param source
     * @param pf, PrettyFormat
     * @param types
     * @return
     */
    public static <C> String convertToXml(Collection<C> source, boolean pf, Class... types) {
        StringBuilder sb = new StringBuilder();
        for (Object src : source) {
            sb.append(convertToXml(src, pf, true, types));
        }
        return sb.toString();
    }

    /**
     *
     * @param c
     * @return
     */
    public static String convertToCliserTemplateExcel(Class c) {
        StringBuilder out = new StringBuilder();
        out.append("<header name=\"").append(c.getSimpleName()).append("\" sheetname=\"Hoja 1\">");
        for (Field field : c.getDeclaredFields()) {
            if (field.getAnnotation(com.codicentro.core.annotation.CWColumn.class) != null) {
                com.codicentro.core.annotation.CWColumn obj = field.getAnnotation(com.codicentro.core.annotation.CWColumn.class);
                out.append("<column");
                /**
                 * * ATRIBUTOS DE LA ETIQUETA COLUMN **
                 */
                if (!TypeCast.isBlank(obj.name())) {
                    out.append(" name=\"").append(obj.name()).append("\"");
                } else {
                    out.append(" name=\"").append(TypeCast.toFirtUpperCase(field.getName())).append("\"");
                }
                out.append(" alignment=\"alCenter\"");
                out.append(" valignment=\"alCenter\"");
                out.append(" wrap=\"true\"");
                out.append(" width=\"").append(obj.width()).append("\"");
                if (!TypeCast.isBlank(obj.format())) {
                    out.append(" format=\"").append(obj.format()).append("\"");
                }
                out.append(" background=\"0x16\"");
                out.append(">");
                /**
                 * * VALUE COLUMN **
                 */
                if (!TypeCast.isBlank(obj.header())) {
                    out.append("<![CDATA[").append(obj.header()).append("]]>");
                } else {
                    out.append(TypeCast.toFirtUpperCase(field.getName()));
                }
                out.append("</column>");
            }
        }
        out.append("</header>");


        return com.codicentro.core.Utils.xmlPrettyFormat(out.toString());
    }

    public static Long lineCount(final String fileName) throws FileNotFoundException, IOException {
        Long count = 0L;
        FileReader fr = new FileReader(fileName);
        BufferedReader bf = new BufferedReader(fr);
        while (bf.ready()) {
            bf.readLine();
            count++;
        }
        bf.close();
        return count;
    }

    /**
     * Only file type text.
     *
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<String> toStringList(final String fileName) throws FileNotFoundException, IOException {
        List<String> rs = new ArrayList<String>();
        FileReader fr = new FileReader(fileName);
        BufferedReader bf = new BufferedReader(fr);
        while (bf.ready()) {
            rs.add(bf.readLine());
        }
        bf.close();
        return rs;
    }
}
