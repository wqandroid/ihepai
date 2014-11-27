package com.wq.letpapa.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具类(未特别说明的均为系统默认时区下的时间)
 * */
public class DateUtil {
	/** 1s==1000ms */
	private final static int TIME_MILLISECONDS = 1000;
	/** 时间中的分、秒最大值均为60 */
	private final static int TIME_NUMBERS = 60;
	/** 时间中的小时最大值 */
	private final static int TIME_HOURSES = 24;
	/** 格式化日期的标准字符串 */
	private final static String FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 获取时区信息
	 * */
	public static TimeZone getTimeZone() {
		return TimeZone.getDefault();
	}
	
	public long dateToLong (String in) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        Date date;
		try {
		    	date = format.parse(in);
			   Calendar cal = Calendar.getInstance();
		        cal.setTime(date);
		        return cal.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
     return 0;
}
	

	/**
	 * 将日期字符串转换为Date对象
	 * 
	 * @param date
	 *            日期字符串，必须为"yyyy-MM-dd HH:mm:ss"
	 * @return 日期字符串的Date对象表达形式
	 * */
	public static Date parseDate(String date) {
		return parseDate(date, FORMAT);
	}

	/**
	 * 将日期字符串转换为Date对象
	 * 
	 * @param date
	 *            日期字符串，必须为"yyyy-MM-dd HH:mm:ss"
	 * @param format
	 *            格式化字符串
	 * @return 日期字符串的Date对象表达形式
	 * */
	public static Date parseDate(String date, String format) {
		Date dt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			dt = dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return dt;
	}

	/**
	 * 将Date对象转换为指定格式的字符串
	 * 
	 * @param date
	 *            Date对象
	 * @return Date对象的字符串表达形式"yyyy-MM-dd HH:mm:ss"
	 * */
	public static String formatDate(Date date) {
		return formatDate(date, FORMAT);
	}

	/**
	 * 将Date对象转换为指定格式的字符串
	 * 
	 * @param date
	 *            Date对象
	 * @param String
	 *            format 格式化字符串
	 * @return Date对象的字符串表达形式
	 * */
	public static String formatDate(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	/**
	 * 格式化日期
	 * 
	 * @param long unixTime unix时间戳
	 * @return 日期字符串"yyyy-MM-dd HH:mm:ss"
	 * */
	public static String formatUnixTime(long unixTime) {
		return formatUnixTime(unixTime, FORMAT);
	}

	/**
	 * 格式化日期
	 * 
	 * @param long unixTime unix时间戳
	 * @param String
	 *            format 格式化字符串
	 * @return 日期字符串
	 * */
	public static String formatUnixTime(long unixTime, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(unixTime);
	}

	/**
	 * 将GMT日期格式化为系统默认时区的日期字符串表达形式
	 * 
	 * @param gmtUnixTime
	 *            GTM时间戳
	 * @return 日期字符串"yyyy-MM-dd HH:mm:ss"
	 * */
	public static String formatGMTUnixTime(long gmtUnixTime) {
		return formatGMTUnixTime(gmtUnixTime, FORMAT);
	}

	/**
	 * 将GMT日期格式化为系统默认时区的日期字符串表达形式
	 * 
	 * @param gmtUnixTime
	 *            GTM时间戳
	 * @param format
	 *            格式化字符串
	 * @return 日期字符串"yyyy-MM-dd HH:mm:ss"
	 * */
	public static String formatGMTUnixTime(long gmtUnixTime, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(gmtUnixTime
				+ TimeZone.getDefault().getRawOffset());
	}

	/**
	 * 获取时间戳的Date表示形式
	 * 
	 * @param unixTime
	 *            unix时间戳
	 * @return Date对象
	 * */
	public static Date getDate(long unixTime) {
		return new Date(unixTime);
	}

	/**
	 * 获取GMT时间戳的Date表示形式（转换为Date表示形式后，为系统默认时区下的时间）
	 * 
	 * @param gmtUnixTime
	 *            GMT Unix时间戳
	 * @return Date对象
	 * */
	public static Date getGMTDate(long gmtUnixTime) {
		return new Date(gmtUnixTime + TimeZone.getDefault().getRawOffset());
	}

	/**
	 * 将系统默认时区的Unix时间戳转换为GMT Unix时间戳
	 * 
	 * @param unixTime
	 *            unix时间戳
	 * @return GMT Unix时间戳
	 * */
	public static long getGMTUnixTime(long unixTime) {
		return unixTime - TimeZone.getDefault().getRawOffset();
	}

	/**
	 * 将GMT Unix时间戳转换为系统默认时区的Unix时间戳
	 * 
	 * @param gmtUnixTime
	 *            GMT Unix时间戳
	 * @return 系统默认时区的Unix时间戳
	 * */
	public static long getCurrentTimeZoneUnixTime(long gmtUnixTime) {
		return gmtUnixTime + TimeZone.getDefault().getRawOffset();
	}

	/**
	 * 获取当前时间的GMT Unix时间戳
	 * 
	 * @return 当前的GMT Unix时间戳
	 * */
	public static long getGMTUnixTimeByCalendar() {
		Calendar calendar = Calendar.getInstance();
		// 获取当前时区下日期时间对应的时间戳
		long unixTime = calendar.getTimeInMillis();
		// 获取标准格林尼治时间下日期时间对应的时间戳
		long unixTimeGMT = unixTime - TimeZone.getDefault().getRawOffset();
		return unixTimeGMT;
	}

	/**
	 * 获取当前时间的Unix时间戳
	 * 
	 * @return 当前的Unix时间戳
	 * */
	public static long getUnixTimeByCalendar() {
		Calendar calendar = Calendar.getInstance();
		// 获取当前时区下日期时间对应的时间戳
		long unixTime = calendar.getTimeInMillis();
		return unixTime;
	}

	/**
	 * 获取更改时区后的时间
	 * 
	 * @param date
	 *            时间
	 * @param oldZone
	 *            旧时区
	 * @param newZone
	 *            新时区
	 * @return 时间
	 */
	public static Date changeTimeZone(Date date, TimeZone oldZone,
			TimeZone newZone) {
		Date dateTmp = null;
		if (date != null) {
			int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
			dateTmp = new Date(date.getTime() - timeOffset);
		}
		return dateTmp;
	}

	/**
	 * 将总秒数转换为时分秒表达形式
	 * 
	 * @param seconds
	 *            任意秒数
	 * @return %s小时%s分%s秒
	 */
	public static String formatTime(long seconds) {
		long hh = seconds / TIME_NUMBERS / TIME_NUMBERS;
		long mm = (seconds - hh * TIME_NUMBERS * TIME_NUMBERS) > 0 ? (seconds - hh
				* TIME_NUMBERS * TIME_NUMBERS)
				/ TIME_NUMBERS
				: 0;
		long ss = seconds < TIME_NUMBERS ? seconds : seconds % TIME_NUMBERS;
		return (hh == 0 ? "" : (hh < 10 ? "0" + hh : hh) + "小时")
				+ (mm == 0 ? "" : (mm < 10 ? "0" + mm : mm) + "分")
				+ (ss == 0 ? "" : (ss < 10 ? "0" + ss : ss) + "秒");
	}

	/**
	 * 获取当前时间距离指定日期时差的大致表达形式
	 * 
	 * @param long date 日期
	 * @return 时差的大致表达形式
	 * */
	public static String getDiffTime(long date) {
		String strTime = "很久以前";
		long time = Math.abs(new Date().getTime() - date);
		// 一分钟以内
		if (time < TIME_NUMBERS * TIME_MILLISECONDS*3) {
			strTime = "刚刚";
		} else {
			int min = (int) (time / TIME_MILLISECONDS / TIME_NUMBERS);
			if (min < TIME_NUMBERS) {
				if (min < 15) {
					strTime = "分钟前";
				} else if (min < 30) {
					strTime = "30分钟前";
				} else {
					strTime = "1小时前";
				}
			} else {
				int hh = min / TIME_NUMBERS;
				if (hh < TIME_HOURSES) {
					strTime = hh + "小时前";
				} else {
					int days = hh / TIME_HOURSES;
					if (days <= 6) {
						strTime = days + "天前";
					} else {
						int weeks = days / 7;
						if (weeks < 3) {
							strTime = weeks + "周前";
						}
					}
				}
			}
		}

		return strTime;
	}
}