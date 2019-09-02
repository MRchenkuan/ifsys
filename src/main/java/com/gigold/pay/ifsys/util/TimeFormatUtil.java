package com.gigold.pay.ifsys.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenkuan
 * on 16/7/20.
 */
public class TimeFormatUtil {

    /**
     * 时间转时刻
     * @param ts
     * @param format "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    private static String timeToMoment(String ts, String format){
        DateFormat df = new SimpleDateFormat(format);
        try

        {

            Date d1 = new Date();
            Date d2 = df.parse(ts);
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            if(days>=7){
                return ts.substring(0,10);
            }

            if(days<7 && days>=1){
                return days+" 天前";
            }

            if(days<1 && hours>=1){
                return hours+" 小时前";
            }

            if(days<1 && hours<1 && minutes>=1){
                return minutes+" 分钟前";
            }

            if(days<1 && hours<1 && minutes<1){
                long second = diff/1000;

                return second>=10?(second + " 秒前"):"刚刚";
            }
            return String.valueOf(diff);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * @重载 时间转时刻
     * @param ts
     * @return
     */
    public static String timeToMoment(String ts){
        return timeToMoment(ts,"yyyy-MM-dd HH:mm:ss");
    }


    public static void main(String[] args){
        System.out.println(timeToMoment("2016-07-13 15:27:44.0"));
    }
}
