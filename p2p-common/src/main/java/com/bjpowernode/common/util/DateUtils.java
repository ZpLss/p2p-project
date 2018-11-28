package com.bjpowernode.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * ClassName:DateUtils
 * Package:com.bjpowernode.common.util
 * Description:
 * Date:2018/3/16 22:05
 * Author:13651027050
 */
public class DateUtils {

    /**
     * 通过指定日期添加天数,返回日期对象
     * @param date
     * @param days
     * @return
     */
    public static Date getDateByAddDays(Date date,Integer days){

        //创建日期处理对象
        Calendar calendar = Calendar.getInstance();

        //将当前日期处理对象时间添加一个天数
        calendar.setTime(date);

        //将当前日期添加天数
        calendar.add(Calendar.DAY_OF_MONTH,days);
        return calendar.getTime();

    }

    /**
     * 通过指定日期添加月数,返回日期对象
     * @param date
     * @param days
     * @return
     */
    public static Date getDateByAddMonths(Date date,Integer months){

        //创建日期处理对象
        Calendar calendar = Calendar.getInstance();

        //将当前日期处理对象时间添加一个天数
        calendar.setTime(date);

        //将当前日期添加天数
        calendar.add(Calendar.MONTH,months);
        return calendar.getTime();

    }

    public static Integer getDaysByYear(Integer year){
        int days = 0;

        //闰年
        if(year % 4 == 0 && year % 100 != 0 || year % 400 == 0){
            days = 366;
        }else {
            days = 365;
        }
        return days;
    }

    /**
     * 获取两个日期之间的天数差
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDistanceOfDate(Date startDate,Date endDate){
        long distance = endDate.getTime() - startDate.getTime();
        long distanceOfDate = distance / (24 * 60 * 60 * 1000);

        return (int) distanceOfDate;
    }

    /**
     * 获取时间戳
     * @param args
     */
    public static String getTime(){

        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }


    public static void main(String[] args) {
        System.out.println(getDateByAddDays(new Date(),1));
    }

}
