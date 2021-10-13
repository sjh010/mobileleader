package com.mobileleader.edoc.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 날짜 함수 유틸
 */
public class DateUtil {

	/**
	 * 현재날짜조회(yyyyMMdd)
	 *
	 * @return String 현재날짜
	 */
	public static String getCurrentDate() {
		return getCurrentDate("yyyyMMdd");
	}

	/**
	 * 포맷된현재날짜조회
	 *
	 * @param String format 날짜포맷
	 * @return String 포맷된현재날짜
	 */
	public static String getCurrentDate(String format) {
		return formatTimestamp(getCurrentTimeStamp(), format, Locale.KOREA);
	}

	/**
	 * 현재시간조회(HHmmss)
	 *
	 * @return String 현재시간
	 */
	public static String getCurrentTime() {
		return getCurrentDate("HHmmss");
	}

	/**
	 * 포맷된현재시간조회
	 *
	 * @param String format 날짜포맷
	 * @return String 포맷된현재시간
	 */
	public static String getCurrentTime(String format) {
		return getCurrentDate(format);
	}

	/**
	 * 현재타임스탬프조회(HHmmss)
	 *
	 * @return String 타임스탬프
	 */
	public static Timestamp getCurrentTimeStamp() {
		return new Timestamp(new GregorianCalendar().getTime().getTime());
	}

	/**
	 * 포맷된현재타임스탬프조회
	 *
	 * @param Timestamp timestamp
	 * @param String format 날짜포맷
	 * @param Locale locale
	 * @return String 포맷된타임스탬프
	 */
	private static String formatTimestamp(Timestamp timestamp, String format, Locale locale) {
		SimpleDateFormat formatter = new SimpleDateFormat(format, locale);
		return formatter.format(timestamp);
	}

	/**
	 * 포맷된현재시간조회
	 *
	 * @param long julian
	 * @param String format 날짜포맷. 예 : 년월일시분초 -> "yyyyMMddHHmmss"
	 * @return String 포맷된현재시간
	 */
	public static String getDateString(long julian, String format) {
		Date date = new Date(julian);
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);
		return formatter.format(date);
	}

	/**
	 * "yyyy/MM/dd HH:mm:ss:SSS" 포맷 문자열로 현재 시간을 돌려준다.
	 * 
	 * @return String 포맷된현재시간
	 */
	public static String getYMDHMSM() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		String timeString = timeFormat.format(System.currentTimeMillis());
		return timeString;
	}
	
	/**
	 * 현재일시 시분초밀리언초
	 * @return  String  현재일시시분초세컨밀리문자열
	 */
	public static String getDateShortMillisecondTimeString() {
		return getCurrentDate("yyyyMMddHHmmssSSS");
	}
	
	/**
	 * 현재일시시분초
	 * 
	 * @return String 현재일시시분초
	 */
	public static String getDateTimeString() {
		return getCurrentDate("yyyyMMddHHmmss");
	}
	
	/**
	 * 시간을 HHmmssSSS 형태의 문자열로 출력
	 * @return  String  시분초세컨밀리문자열
	 */
	public static String getShortMillisecondTimeString() {
		SimpleDateFormat formatter = new SimpleDateFormat("HHmmssSSS", Locale.KOREA);
		return formatter.format(new Date());
	}
	
	/**
	 * 특정시간GMT형식
	 */
	public static String getGMTCurTimeString(Calendar cal_now){
		cal_now.setTime(new Date());
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return timeFormat.format(cal_now.getTime());
	}
	
	/**
	 * 특정시간 GMT date계산
	 */
	public static String getCalGMTTimeString(Calendar cal_now, int prioDate){
		cal_now.add(Calendar.DATE, prioDate);
		Date date = cal_now.getTime();
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return timeFormat.format(date);
	}
}
