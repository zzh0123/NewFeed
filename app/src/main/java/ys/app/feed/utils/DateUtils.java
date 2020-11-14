package ys.app.feed.utils;

import java.text.SimpleDateFormat;

public class DateUtils {
    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(new Long(s));
        return d;
    }

    public static String stampToDate_Day(String s){
        SimpleDateFormat format =  new SimpleDateFormat("yyyy.MM.dd");
        String d = format.format(new Long(s));
        return d;
    }
}