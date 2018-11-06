package com.gsgd.live.utils;

import com.jiongbull.jlog.JLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by andy on 2017/12/25.
 */
public class DateUtils {

    public static final String YY_MM_DD = " yyyy-MM-dd ";
    public static final String HH_MM = " HH:mm ";
    public static final String YY_MM_DD_HH_MM = " yyyy-MM-dd HH:mm ";

    public static long parse(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDay(long time) {
        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time));
        int disDay = cal.get(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH);
        if (disDay == 0) {
            return "今天";
        } else if (disDay == 1) {
            return "明天";
        } else {
            return "今天";
        }
    }
}
