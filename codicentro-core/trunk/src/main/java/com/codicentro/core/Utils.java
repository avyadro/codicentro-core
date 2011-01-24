/**
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
import com.codicentro.core.security.Encryption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

    /**
     * Get the last business day of the month for a given month / year combination
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
}
