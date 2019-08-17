/*
 * Copyright (c) 2012,  All rights reserved.
 */
package com.github.zenframework.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Date/time utility
 * @author Zed 2012-4-13
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils  {
	
	/**
	 * Get unix time
	 */
	public static long getUnixTime() {
		return System.currentTimeMillis() / 1000;
	}
	
	/**
	 * 转换成timestamp
	 * @param date
	 * @return
	 */
	public static Timestamp toTimestamp(Date date) {
		return new Timestamp(date.getTime());
	}
	
	/**
	 * 获取当天的开始时间
	 * @return
	 */
	public static Date getTodayBeginning() {
		return getDayBeginning(new Date());
	}
	
	public static Date getTodayEnding() {
		return getDayEnding(new Date());
	}
	
	/**
	 * 获取一天的开始 00:00:00:000
	 * @param date
	 * @return
	 */
	public static Date getDayBeginning(Date date) {
		return getDayBeginning(toCalendar(date));
	}
	
	/**
	 * 获取一天最后时间
	 * @param date
	 * @return
	 */
	public static Date getDayEnding(Date date) {
		return getDayEnding(toCalendar(date));
	}
	
	/**
	 * 获取一天的开始 00:00:00:000
	 * @param date
	 * @return
	 */
	public static Date getDayBeginning(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * 获取一天最后的一刻
	 * @param cal
	 * @return
	 */
	public static Date getDayEnding(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}
	
	/**
	 * 获取下一天的开始
	 * @param date
	 * @return
	 */
	public static Date getNextDayBeginning(Date date) {
		Calendar cal = toCalendar(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return getDayBeginning(cal);
	}
	
	/**
	 * 获取上一天的开始
	 * @param cal
	 * @return
	 */
	public static Date getYesterdayBeginning(Date date){
		Calendar cal = toCalendar(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return getDayBeginning(cal);
	}
	
	public static int getYear(Calendar cal) {
		return cal.get(Calendar.YEAR);
	}
	
	public static int getYear(Date date) {
		return getYear(toCalendar(date));
	}
	
	public static int getCurrentYear() {
		return getYear(Calendar.getInstance());
	}
	
	public static int getCurrentWeekDay() {
		return getWeekDay(Calendar.getInstance());
	}
	
	public static int getMonth(Date date) {
		return getMonth(toCalendar(date));
	}
	
	public static int getMonth(Calendar cal) {
		return cal.get(Calendar.MONTH) + 1;
	}
	
	public static int getDate(Calendar cal) {
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getDate(Date date) {
		return getDate(toCalendar(date));
	}
	
	public static int getWeekDay(Calendar cal) {
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	public static int getWeekDay(Date date) {
		return getWeekDay(toCalendar(date));
	}
	
	public static int getHour(Calendar cal) {
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	
	public static int getHour(Date date) {
		Calendar cal = toCalendar(date);
		return getHour(cal);
	}
	
	/**
	 * 设置时间
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @param milSec
	 * @return
	 */
	public static Date setTime(int year, int month, int day, int hour, int minute, int second, int milSec) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, milSec);
		return cal.getTime();
	}
	
	
	public static Timestamp getCurrentTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	

	public static Date getMonthEnding(Date date){
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		cal.set( Calendar.DATE, 1 );  
        cal.roll(Calendar.DATE, - 1 );
        Date beginTime=cal.getTime();
        
        return getDayEnding(beginTime);
	}
	
	public static Date getMonthBegining(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		cal.set( Calendar.DATE, 1 );  
        cal.roll(Calendar.DATE, - 1 );
        cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        Date endTime=cal.getTime();
		return getDayBeginning(endTime);
	}
	
	public static Date getWeekBeginning(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return getDayBeginning(cal);
	}
	
	public static Date getWeekEnding(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		return getDayEnding(cal);
	}
	
	public static boolean inSameWeek(Date date1, Date date2) {
		Calendar cal = toCalendar(date1);
		int year1 = cal.get(Calendar.YEAR);
		int week1 = cal.get(Calendar.WEEK_OF_YEAR);
		cal.setTime(date2);
		int year2 = cal.get(Calendar.YEAR);
		int week2 = cal.get(Calendar.WEEK_OF_YEAR);
		return year1 == year2 && week1 == week2;
	}
	
	public static boolean inSameMonth(Date date1, Date date2) {
		Calendar cal = toCalendar(date1);
		int year1 = cal.get(Calendar.YEAR);
		int month1 = cal.get(Calendar.MONTH);
		cal.setTime(date2);
		int year2 = cal.get(Calendar.YEAR);
		int month2 = cal.get(Calendar.MONTH);
		return year1 == year2 && month1 == month2;
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws Exception {
		System.out.println(getWeekEnding(DateUtils.addDays(new Date(), -7)));
		
	}


}
