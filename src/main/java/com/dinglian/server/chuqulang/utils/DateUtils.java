package com.dinglian.server.chuqulang.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

public class DateUtils {

	private static final SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");

	private static final SimpleDateFormat sqlDataFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final SimpleDateFormat dataFormatWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private static final SimpleDateFormat dataFormatWithTimeNoSecond = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private static final SimpleDateFormat testFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final SimpleDateFormat millSecondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-SSS");

	private static final SimpleDateFormat sqlDateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private static final SimpleDateFormat dataFormatString = new SimpleDateFormat("yyMMdd");

	private static final SimpleDateFormat dataFormatExtJs = new SimpleDateFormat("MMM dd yyyy");

	private static final SimpleDateFormat dataFormatUS = new SimpleDateFormat("MM/dd/yy");

	private static final SimpleDateFormat dataFormatToUS = new SimpleDateFormat("MMM-dd-yyyy", Locale.US);

	private static final SimpleDateFormat dataFormatCombo = new SimpleDateFormat("yy/mm/dd HH:mm");

	public static final SimpleDateFormat dateFormatString = new SimpleDateFormat("yyMMddHHmmss");

	public static final SimpleDateFormat dateFormatYear = new SimpleDateFormat("yy");

	public final static String defaultFormatStr = "dd/MM/yyyy";

	public final static String dMyHm = "dd/MM/yyyy HH:mm";
	public final static String dMyHms = "dd/MM/yyyy HH:mm:ss";
	public final static String dMy = "dd/MM/yyyy";
	public final static String yMdHms = "yyyy-MM-dd HH:mm:ss";
	public final static String yMdHm = "yyyy-MM-dd HH:mm";

	public final static String yMd = "yyyy-MM-dd";
	public final static String yyyyMMddHM = "yyyyMMdd HH:mm";
	public final static String yyyyMMddHmsMs = "yyyyMMdd HHmmssSSS";
	public final static String yyyyMMddHmsMsWithNoSpace = "yyyyMMdd:HHmmssSSS";

	public final static String SUNDAY = "sun";
	public final static String MONDAY = "mon";
	public final static String TUESDAY = "tue";
	public final static String WEDNESDAY = "wed";
	public final static String THURSDAY = "thu";
	public final static String FRIDAY = "fri";
	public final static String SATURDAY = "sat";

	public final static String JANUARY = "January";
	public final static String FEBRUARY = "February";
	public final static String MARCH = "March";
	public final static String APRIL = "April";
	public final static String MAY = "May";
	public final static String JUNE = "June";
	public final static String JULY = "July";
	public final static String AUGUST = "August";
	public final static String SEPTEMBER = "September";
	public final static String OCTOBER = "October";
	public final static String NOVEMBER = "November";
	public final static String DECEMBER = "December";
	
	public final static String extDateFormat = "yyyy/MM/dd";

	public static final String[] MONTH = { "January", "February", "March", "April", "May", "June", "July", "August",
			"September", "October", "November", "December" };

	/** Creates a new instance of DateUtils */
	public DateUtils() {
	}

	public static String format(Date date, String format) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat dataFormat = new SimpleDateFormat(format);
		return dataFormat.format(date);
	}

	public static String format(Calendar calendar, String format) {
		if (calendar == null) {
			return "";
		}
		SimpleDateFormat dataFormat = new SimpleDateFormat(format);
		return dataFormat.format(calendar.getTime());
	}

	public static Date parse(String dateStr, String format) {
		if (StringUtils.isBlank(dateStr)) {
			return null;
		}
		Date date = null;
		SimpleDateFormat dataFormat = new SimpleDateFormat(format);
		try {
			date = dataFormat.parse(dateStr);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return date;
	}

	public static String transeferFormat(String dateStr, String fromFormat, String toFormat) {
		if (StringUtils.isBlank(dateStr)) {
			return "";
		}
		Date date = parse(dateStr, fromFormat);
		return format(date, toFormat);
	}

	public static Date parse(String dateStr, SimpleDateFormat format) {
		if (StringUtils.isBlank(dateStr)) {
			return null;
		}
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return date;
	}

	public static Calendar parseCalendar(String dateStr, String format) {
		Date date = parse(dateStr, format);
		if (date == null) {
			return null;
		}
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static String format(Date date) {
		return dataFormat.format(date);
	}

	public static String simpleFormat(Date date) {
		return dataFormatString.format(date);
	}

	public static String sqlDataFormat(Date date) {
		return sqlDataFormat.format(date);

	}

	public static Date dataFormat(String date) throws ParseException {
		return dataFormatCombo.parse(date);
	}

	public static Date sqlDataFormat(String date) throws ParseException {
		return sqlDataFormat.parse(date);
	}

	public static String sqlDataStringFormat(String date) {
		return sqlDataFormat.format(date);
	}

	public static String formatSqlWithTime(Date date) {
		return sqlDateFormatWithTime.format(date);
	}

	public static String formatWithTime(Date date) {
		return dataFormatWithTime.format(date);
	}

	public static String format(Calendar calendar) {
		if (calendar == null) {
			return "";
		}
		return format(calendar.getTime());
	}

	public static String formatSql(Calendar calendar) {
		if (calendar == null) {
			return "";
		}
		return sqlDataFormat(calendar.getTime());
	}

	public static String formatSqlWithTime(Calendar calendar) {
		if (calendar == null) {
			return "";
		}
		return formatSqlWithTime(calendar.getTime());
	}

	public static String formatSqlDataFormat(Calendar calendar) {
		if (calendar == null) {
			return "";
		}
		return sqlDataFormat(calendar.getTime());
	}

	public static String formatWithTime(Calendar calendar) {
		if (calendar == null) {
			return "";
		}
		return formatWithTime(calendar.getTime());
	}

	public static Date parse(String dateInStr) throws ParseException {
		return dataFormat.parse(dateInStr);
	}

	public static Date parseWithTime(String dateInStr) throws ParseException {
		return dataFormatWithTime.parse(dateInStr);
	}

	public static Date parseForExtJs(String dateInStr) throws ParseException {
		return dataFormatExtJs.parse(dateInStr);
	}

	public static Date parseForUS(String dateInStr) throws ParseException {
		return dataFormatUS.parse(dateInStr);
	}

	public static Calendar parseCalendar(String dateInStr, TimeZone timeZone) throws ParseException {
		if (StringUtils.isBlank(dateInStr)) {
			return null;
		}

		Date date = parse(dateInStr);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static Calendar parseCalendarWithTime(String dateInStr, TimeZone timeZone) throws ParseException {
		if (StringUtils.isBlank(dateInStr)) {
			return null;
		}

		Date date = parseWithTime(dateInStr);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static Calendar parseCalendarForExtJs(String dateInStr, TimeZone timeZone) throws ParseException {
		if (StringUtils.isBlank(dateInStr)) {
			return null;
		}

		Date date = parseForExtJs(dateInStr);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static Calendar parseCalendarUS(String dateInStr, TimeZone timeZone) throws ParseException {
		if (StringUtils.isBlank(dateInStr)) {
			return null;
		}

		Date date = parseForUS(dateInStr);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static Calendar getCalendar() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		return calendar;
	}

	public static Date getSystemDate() throws ParseException {
		// return DateUtils.sqlDataFormat(DateUtils.sqlDataFormat(new Date()));
		String dateInStr = testFormatWithTime.format(new Date().getTime());
		return sqlDateFormatWithTime.parse(dateInStr);

	}

	public static Date getSystemTime() throws ParseException {
		Date systemTime = new Date();
		String dateInStr = sqlDateFormatWithTime.format(new Date().getTime());
		systemTime = sqlDateFormatWithTime.parse(dateInStr);
		return systemTime;

	}

	public static String getSystemTimeStr() throws ParseException {
		String dateInStr = sqlDateFormatWithTime.format(new Date().getTime());
		return dateInStr;

	}

	public static String getCurrentTimeWithSecond() throws ParseException {
		String dateInStr = testFormatWithTime.format(new Date().getTime());
		return dateInStr;
	}

	public static long getDiffTimeWithSecond(String startTime, String endTime) throws ParseException {
		Date startDate = testFormatWithTime.parse(startTime);
		Date endDate = testFormatWithTime.parse(endTime);
		long between = (endDate.getTime() - startDate.getTime()) / 1000;// return
																		// second
		return between;
	}

	public static String getYearMonth(Date date) {
		return dataFormatString.format(date).substring(0, 4);
	}

	public static String getDataFormatToUS(String date) {
		return dataFormatToUS.format(date);
	}

	public static String getDataFormatToUS(Date date) throws ParseException {
		return dataFormatToUS.format(date);
	}

	public static String getDataFormatWithTimeNoSecond(Date date) throws ParseException {
		return dataFormatWithTimeNoSecond.format(date);
	}

	public static String getTimeStr() {
		Calendar date = GregorianCalendar.getInstance();
		String timeStr = dataFormatWithTime.format(date.getTime());
		return timeStr;
	}

	public static String getTomorrowDateStr() {
		Calendar tomorrowCalendar = GregorianCalendar.getInstance();
		tomorrowCalendar.add(Calendar.DATE, 1);
		String tomorrowDateStr = dataFormat.format(tomorrowCalendar.getTime());
		return tomorrowDateStr;
	}

	public static String getCurrentDateStr() {
		Calendar currentCalendar = GregorianCalendar.getInstance();
		String currentDateStr = dataFormat.format(currentCalendar.getTime());
		return currentDateStr;
	}

	public static String getDefaultCreateOrderDateFrom() {
		Calendar nowDate = GregorianCalendar.getInstance();
		nowDate.add(Calendar.MONTH, -1);
		nowDate.set(Calendar.DAY_OF_MONTH, 1);
		return dataFormat.format(nowDate.getTime());
	}

	public static String getDefaultCreateOrderDateTo() {
		Calendar nowDate = GregorianCalendar.getInstance();
		return dataFormat.format(nowDate.getTime());
	}

	public static String getPreviousSixMonthDate() {
		Calendar nowDate = GregorianCalendar.getInstance();
		nowDate.add(Calendar.MONTH, -6);
		nowDate.set(Calendar.DAY_OF_MONTH, 1);
		String date = dataFormat.format(nowDate.getTime());
		return date;
	}

	public static String getPreviousThreeMonthDate() {
		Calendar nowDate = GregorianCalendar.getInstance();
		nowDate.add(Calendar.MONTH, -3);
		nowDate.set(Calendar.DAY_OF_MONTH, 1);
		String date = dataFormat.format(nowDate.getTime());
		return date;
	}

	public static String getPreviousMonthDate(int beforeMonth) {
		Calendar nowDate = GregorianCalendar.getInstance();
		nowDate.add(Calendar.MONTH, beforeMonth);
		nowDate.set(Calendar.DAY_OF_MONTH, 1);
		String date = dataFormat.format(nowDate.getTime());
		return date;
	}

	public static int getYearsSoFar() {
		Calendar nowTime = GregorianCalendar.getInstance();
		return nowTime.get(Calendar.YEAR);
	}

	public static Calendar getRequestShipDate(int day) {
		Calendar currentCalendar = GregorianCalendar.getInstance();
		currentCalendar.add(Calendar.DAY_OF_YEAR, day);
		return currentCalendar;
	}

	public static Date getRangeDate(int day) {
		Calendar currentCalendar = GregorianCalendar.getInstance();
		currentCalendar.add(Calendar.DAY_OF_YEAR, day);
		Date date = currentCalendar.getTime();
		return date;
	}

	public static String millSecondFormat(Calendar calendar) {
		if (calendar == null) {
			return "";
		}
		return millSecondFormat.format(calendar.getTime());
	}

	public static boolean isDateField(String value) {
		boolean isDateField = false;
		SimpleDateFormat dMyDateFormat = new SimpleDateFormat(DateUtils.dMy);
		dMyDateFormat.setLenient(false);
		try {
			dMyDateFormat.parse(value);
			isDateField = true;
		} catch (Exception e) {
			isDateField = false;
		}
		return isDateField;
	}

	public static boolean isInTime(String periodTime) {
		if (StringUtils.isBlank(periodTime)) {
			return false;
		}
		String[] periodTimeArray = periodTime.split("-");
		if (periodTimeArray == null || periodTimeArray.length < 2) {
			return false;
		}
		String[] beginTimeArray = periodTimeArray[0].split(":");
		String[] endTimeArray = periodTimeArray[1].split(":");
		if (beginTimeArray == null || endTimeArray == null || beginTimeArray.length < 2 || endTimeArray.length < 2) {
			return false;
		}
		int beginTime = Integer.parseInt(beginTimeArray[0]) * 60 + Integer.parseInt(beginTimeArray[1]);
		int endTime = Integer.parseInt(endTimeArray[0]) * 60 + Integer.parseInt(endTimeArray[1]);

		Calendar currentDateTime = Calendar.getInstance();
		int hour = currentDateTime.get(Calendar.HOUR_OF_DAY);
		int minute = currentDateTime.get(Calendar.MINUTE);
		int currentTime = hour * 60 + minute;
		if (currentTime >= beginTime && currentTime <= endTime) {
			return true;
		}
		return false;
	}

	// format of periodTime must is Time-Time,Time is HH:mm or H:m or HH:m or
	// H:mm, example : 07:16-18:22
	// return value unit minute
	public static int getDiffTime(String periodTime) {
		if (StringUtils.isBlank(periodTime)) {
			return -1;
		}
		String[] periodTimeArray = periodTime.split("-");
		if (periodTimeArray == null || periodTimeArray.length < 2) {
			return -1;
		}
		String[] beginTimeArray = periodTimeArray[0].split(":");
		String[] endTimeArray = periodTimeArray[1].split(":");
		if (beginTimeArray == null || endTimeArray == null || beginTimeArray.length < 2 || endTimeArray.length < 2) {
			return -1;
		}
		int beginTime = Integer.parseInt(beginTimeArray[0]) * 60 + Integer.parseInt(beginTimeArray[1]);
		int endTime = Integer.parseInt(endTimeArray[0]) * 60 + Integer.parseInt(endTimeArray[1]);
		return endTime - beginTime;
	}

	public static String getCurrentYearDigits() {
		Date nowDate = new Date(System.currentTimeMillis());
		SimpleDateFormat bartDateFormat = dateFormatYear;
		String year = bartDateFormat.format(nowDate);
		return year;
	}

	public static String getWeekDay(int dayOfWeek) {
		switch (dayOfWeek) {
		case 1:
			return SUNDAY;
		case 2:
			return MONDAY;
		case 3:
			return TUESDAY;
		case 4:
			return WEDNESDAY;
		case 5:
			return THURSDAY;
		case 6:
			return FRIDAY;
		case 7:
			return SATURDAY;
		default:
			return "Non";
		}
	}

	public static String getMonth(int monthOfYear) {
		switch (monthOfYear) {
		case 0:
			return JANUARY;
		case 1:
			return FEBRUARY;
		case 2:
			return MARCH;
		case 3:
			return APRIL;
		case 4:
			return MAY;
		case 5:
			return JUNE;
		case 6:
			return JULY;
		case 7:
			return AUGUST;
		case 8:
			return SEPTEMBER;
		case 9:
			return OCTOBER;
		case 10:
			return NOVEMBER;
		case 11:
			return DECEMBER;
		default:
			return "Non";
		}

	}

	public static boolean isIncludeCurrentDay(String exeDays) {
		Calendar currentDate = Calendar.getInstance();
		int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
		return exeDays.contains(getWeekDay(dayOfWeek));
	}

	public static int transerWeekDay(int weekDay) throws Exception {
		if (weekDay < 1 || weekDay > 7) {
			throw new Exception("week day only 1-7");
		}
		if (weekDay == 7) {
			return 1;
		} else {
			return weekDay + 1;
		}
	}

}
