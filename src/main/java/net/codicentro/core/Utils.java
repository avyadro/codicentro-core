/*
 * Author: Alexander Villalobos Yadr
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Mar 09, 2009, 03:08:26 AM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro©
 * Web: http://www.codicentro.net
 * Class Name: FileTools.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Mar 09, 2006           Alexander Villalobos Yadró           1. New class.
 **/
package net.codicentro.core;

import net.codicentro.core.Types.EncrypType;
import net.codicentro.core.annotation.CWColumn;
import net.codicentro.core.security.Encryption;
import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
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
        String result;
        Encryption encryption;
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
        int day;
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
     * @param begin
     * @param end
     * @return
     */
    public static Long days(Date begin, Date end) {
        return ((end.getTime() - begin.getTime()) / (1000 * 60 * 60 * 24));
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

    public static String xmlFormat(final Boolean pretty, String xml) {
        try {
            if (TypeCast.isBlank(xml)) {
                return xml;
            }
            Document doc = DocumentHelper.parseText(xml);
            StringWriter sw = new StringWriter();
            OutputFormat format = pretty ? OutputFormat.createPrettyPrint() : OutputFormat.createCompactFormat();
            XMLWriter xw = new XMLWriter(sw, format);
            xw.write(doc);
            return sw.toString();
        } catch (DocumentException e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        } catch (IOException e) {
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

    public static <TEntity> Map<String, Object> toMap(TEntity source) {
        Map<String, Object> rs = new HashMap<String, Object>();
        try {
            for (Field field : source.getClass().getDeclaredFields()) {
                Boolean accessible = field.isAccessible();
                field.setAccessible(true);
                rs.put(field.getName(), field.get(source));
                field.setAccessible(accessible);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    public static <TEntity> String toJasper(Class<TEntity> clazz, String[] excludeFields) {

        StringBuilder xmlField = new StringBuilder();
        StringBuilder xmlVariable = null;
        StringBuilder xmlColumnHeader = new StringBuilder("<columnHeader><band height=\"20\">");
        StringBuilder xmlDetail = new StringBuilder("<detail><band height=\"15\">");
        StringBuilder xmlSummary = null;
        Long x = 0L;
        for (Field field : clazz.getDeclaredFields()) {
            /**
             * FIELDS
             */
            CWColumn cwc = field.getAnnotation(CWColumn.class);
            if (cwc != null && (excludeFields == null || excludeFields.length < 1 || !ArrayUtils.contains(excludeFields, field.getName()))) {
                Class<?> type = field.getType();
                xmlField.append("<field name=\"").append(field.getName()).append("\" class=\"").append(type.getName()).append("\"/>");
                Long width = TypeCast.toLong(cwc.width() * 96.0000000000011);// 1 inch [in] = 96.0000000000011 pixel (X or Y)
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

                xmlColumnHeader.append("<textElement textAlignment=\"").append(TypeCast.isBlank(cwc.headerAlign()) ? "Center" : TypeCast.toFirtUpperCase(cwc.headerAlign())).append("\" verticalAlignment=\"Middle\">");
                xmlColumnHeader.append("<font isBold=\"true\"/>");
                xmlColumnHeader.append("</textElement>");
                xmlColumnHeader.append("<text><![CDATA[").append(header).append("]]></text>");
                xmlColumnHeader.append("</staticText>");
                /**
                 * DETAILS
                 */
                xmlDetail.append("<textField").append(TypeCast.isBlank(cwc.format()) ? "" : " pattern=\"" + cwc.format() + "\"").append(" isBlankWhenNull=\"true\">");
                xmlDetail.append("<reportElement x=\"").append(x).append("\" y=\"0\" width=\"").append(width).append("\" height=\"15\"/>");
                xmlDetail.append("<textElement textAlignment=\"").append(TypeCast.isBlank(cwc.align()) ? "Left" : TypeCast.toFirtUpperCase(cwc.align())).append("\">");
                xmlDetail.append("</textElement>");
                xmlDetail.append("<textFieldExpression class=\"").append(type.getName()).append("\"><![CDATA[$F{").append(field.getName()).append("}").append(cwc.expression()).append("]]></textFieldExpression>");
                xmlDetail.append("</textField>");

                if (!TypeCast.isBlank(cwc.summaryType())) {
                    if (xmlVariable == null) {
                        xmlVariable = new StringBuilder();
                    }
                    xmlVariable.append("<variable name=\"var_").append(field.getName()).append("\" class=\"").append(type.getName()).append("\" calculation=\"").append(TypeCast.toFirtUpperCase(cwc.summaryType())).append("\">");
                    xmlVariable.append("<variableExpression><![CDATA[$F{").append(field.getName()).append("}]]></variableExpression>");
                    xmlVariable.append("</variable>");
                    if (xmlSummary == null) {
                        xmlSummary = new StringBuilder("<summary><band height=\"20\">");
                    }
                    xmlSummary.append("<textField").append(TypeCast.isBlank(cwc.format()) ? "" : " pattern=\"" + cwc.format() + "\"").append(" isBlankWhenNull=\"true\">");
                    xmlSummary.append("<reportElement x=\"").append(x).append("\" y=\"0\" width=\"").append(width).append("\" height=\"15\"/>");
                    xmlSummary.append("<textElement textAlignment=\"").append(TypeCast.isBlank(cwc.align()) ? "Left" : TypeCast.toFirtUpperCase(cwc.align())).append("\">");
                    xmlSummary.append("<font isBold=\"true\"/>");
                    xmlSummary.append("</textElement>");
                    xmlSummary.append("<textFieldExpression class=\"").append(type.getName()).append("\"><![CDATA[$V{var_").append(field.getName()).append("}").append(cwc.expression()).append("]]></textFieldExpression>");
                    xmlSummary.append("</textField>");
                }
                x += width;
            }
        }
        xmlColumnHeader.append("</band></columnHeader>");
        xmlDetail.append("</band></detail>");
        if (xmlSummary != null) {
            xmlSummary.append("</band></summary>");
        }

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
        if (xmlVariable != null) {
            xmlJasper.append(xmlVariable);
        }

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

        if (xmlSummary != null) {
            xmlJasper.append(xmlSummary);
        }

        xmlJasper.append("</jasperReport>");
        return xmlJasper.toString();
    }

    public static <TEntity> String toJasper(Class<TEntity> clazz) {
        return toJasper(clazz, null);
    }

    /**
     *
     * @param <TEntity>
     * @param xml
     * @param type
     * @return
     * @deprecated Used to class... types
     */
    public static <TEntity> TEntity convertToEntity(String xml, Class<TEntity> type) {
        try {
            if (TypeCast.isBlank(xml)) {
                return null;
            } else {
                JAXBContext context = JAXBContext.newInstance(type);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return type.cast(unmarshaller.unmarshal(new StringReader(xml)));
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <TEntity> TEntity convertToEntity(String xml, Class... types) {
        try {
            if (TypeCast.isBlank(xml)) {
                return null;
            } else {
                JAXBContext context = JAXBContext.newInstance(types);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                return (TEntity) unmarshaller.unmarshal(new StringReader(xml));
            }
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
            if (field.getAnnotation(net.codicentro.core.annotation.CWColumn.class) != null) {
                net.codicentro.core.annotation.CWColumn obj = field.getAnnotation(net.codicentro.core.annotation.CWColumn.class);
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

        return net.codicentro.core.Utils.xmlPrettyFormat(out.toString());
    }

    /**
     *
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Long lineCount(final String fileName) throws FileNotFoundException, IOException {
        return lineCount(new File(fileName));
    }

    public static Long lineCount(final File file) throws FileNotFoundException, IOException {
        return lineCount(new FileInputStream(file));
    }

    public static Long lineCount(final InputStream input) throws FileNotFoundException, IOException {
        Long count = 0L;
        BufferedReader bf = new BufferedReader(new InputStreamReader(input));
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

    public static Object searchSet(Set ss, Object searchFor) {
        Iterator it = ss.iterator();
        while (it.hasNext()) {
            Object s = it.next();
            if (s != null && s.equals(searchFor)) {
                return s;
            }
        }
        return null;
    }

    public static <K, V> K getKey(Map<K, V> mp, V value) {
        K rs = null;
        for (Iterator<K> it = mp.keySet().iterator(); rs == null && it.hasNext();) {
            if (true) {
                K current = it.next();
                if (mp.get(current).equals(value)) {
                    rs = current;
                }
            }
        }
        return rs;
    }

    public static <T> T getElement(Set<T> set, T element) {
        T result = null;
        if (set instanceof TreeSet<?>) {
            T floor = ((TreeSet<T>) set).floor(element);
            if (floor != null && floor.equals(element)) {
                result = floor;
            }
        } else {
            boolean found = false;
            for (Iterator<T> it = set.iterator(); !found && it.hasNext();) {
                if (true) {
                    T current = it.next();
                    if (current.equals(element)) {
                        result = current;
                        found = true;
                    }
                }
            }
        }
        return result;
    }

    public static <T> T getElement(Collection<T> collection, T element) {
        T result = null;
        boolean found = false;
        for (Iterator<T> it = collection.iterator(); !found && it.hasNext();) {
            if (true) {
                T current = it.next();
                if (current.equals(element)) {
                    result = current;
                    found = true;
                }
            }
        }
        return result;
    }

    /**
     *
     * @param <T>
     * @param collection
     * @param element
     * @param field
     * @return
     */
    public static <T> T getElement(Collection<T> collection, Object value, String key) throws CDCException {
        if (collection == null || collection.isEmpty()) {
            throw new CDCException("Collection is null or empty.");
        }
        if (TypeCast.isBlank(key)) {
            throw new CDCException("The key is null or empty.");
        }
        String[] keys = key.split("\\.");
        T result = null;
        boolean found = false;
        for (Iterator<T> it = collection.iterator(); !found && it.hasNext();) {
            if (true) {
                T current = it.next();
                result = current;
                try {
                    for (String fieldName : keys) {
                        Field field = result.getClass().getDeclaredField(fieldName);
                        Boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        result = (T) field.get(result);
                        field.setAccessible(accessible);
                    }
                } catch (NoSuchFieldException ex) {
                    throw new RuntimeException(ex);
                } catch (IllegalArgumentException ex) {
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
                if (result != null && result.equals(value)) {
                    result = current;
                    found = true;
                } else {
                    result = null;
                }
            }
        }
        return result;
    }
}
