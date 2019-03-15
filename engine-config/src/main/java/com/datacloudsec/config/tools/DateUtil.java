package com.datacloudsec.config.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final Locale[] locales = {Locale.CHINA, Locale.ENGLISH};

    /**
     * 格式化日期
     *
     * @param df      SimpleDateFormat
     * @param dateStr dateStr
     * @return Date
     */
    public static Date format(SimpleDateFormat df, String dateStr) {
        Calendar c = Calendar.getInstance();
        String pattern = df.toPattern();
        StringBuilder dateBuilder = new StringBuilder(dateStr);
        StringBuilder formatBuilder = new StringBuilder(pattern);
        if (!pattern.contains("yy")) {
            formatBuilder.append(" yyyy");
            dateBuilder.append(" ").append(c.get(Calendar.YEAR));
        }

        if (!pattern.contains("M")) {
            formatBuilder.append(" MM");
            dateBuilder.append(" ").append(c.get(Calendar.MONTH) + 1);
        }

        if (!pattern.contains("d")) {
            formatBuilder.append(" dd");
            dateBuilder.append(" ").append(c.get(Calendar.DAY_OF_MONTH));
        }
        //当使用hKk格式时，避免多追加HH
        if (!pattern.contains("H") && !pattern.contains("h") && !pattern.contains("K") && !pattern.contains("k")) {
            formatBuilder.append(" HH");
            dateBuilder.append(" ").append(c.get(Calendar.HOUR_OF_DAY));
        }

        if (!pattern.contains("m")) {
            formatBuilder.append(" mm");
            dateBuilder.append(" ").append(c.get(Calendar.MINUTE));
        }

        Date d = null;
        for (Locale locale : locales) {
            try {
                DateFormat sdf = new SimpleDateFormat(formatBuilder.toString(), locale);
                sdf.setTimeZone(df.getTimeZone());
                d = sdf.parse(dateBuilder.toString());
                break;
            } catch (Exception e) {
            }
        }
        return d;
    }
}
