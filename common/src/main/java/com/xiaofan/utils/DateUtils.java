package com.xiaofan.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;

public class DateUtils {


    /**
     * yyyy-MM-dd HH:mm:ss
     **/
    public static final ThreadLocal<SimpleDateFormat> FMT_YMDHMS = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    /**
     * yyyyMMddHHmmss
     **/
    public static final ThreadLocal<SimpleDateFormat> FMT_YMDHMS_N = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));
    /**
     * yyyy-MM-dd HH:mm:ss.SSS
     **/
    public static final ThreadLocal<SimpleDateFormat> FMT_YMDHMSS = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
    /**
     * yyyyMMddHHmmssSSS
     **/
    public static final ThreadLocal<SimpleDateFormat> FMT_YMDHMSS_N = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmssSSS"));
    /**
     * yyyy-MM-dd
     **/
    public static final ThreadLocal<SimpleDateFormat> FMT_YMD = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    /**
     * yyyyMMdd
     **/
    public static final ThreadLocal<SimpleDateFormat> FMT_YMD_N = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));
    /**
     * 显示年月日时分秒，例如 2015-08-11
     */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd";
    /**
     * 仅显示时分秒，例如 09:51:53.
     */
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 显示年月日时分秒(无符号)，例如 20150811095153.
     */
    public static final String UNSIGNED_DATETIME_PATTERN = "yyyyMMddHHmmss";
    /**
     * 仅显示年月日(无符号)，例如 20150811.
     */
    public static final String UNSIGNED_DATE_PATTERN = "yyyyMMdd";
    /**
     * 春天;
     */
    public static final Integer SPRING = 1;
    /**
     * 夏天;
     */
    public static final Integer SUMMER = 2;
    /**
     * 秋天;
     */
    public static final Integer AUTUMN = 3;
    /**
     * 冬天;
     */
    public static final Integer WINTER = 4;
    /**
     * 星期日;
     */
    public static final String SUNDAY = "星期日";
    /**
     * 星期一;
     */
    public static final String MONDAY = "星期一";
    /**
     * 星期二;
     */
    public static final String TUESDAY = "星期二";
    /**
     * 星期三;
     */
    public static final String WEDNESDAY = "星期三";
    /**
     * 星期四;
     */
    public static final String THURSDAY = "星期四";
    /**
     * 星期五;
     */
    public static final String FRIDAY = "星期五";
    /**
     * 星期六;
     */
    public static final String SATURDAY = "星期六";
    /**
     * 时间格式字符长度与时间格式的映射
     **/
    private static final HashMap<Integer, SimpleDateFormat> FMT_LENGTH_MAP = new HashMap<Integer, SimpleDateFormat>() {{
        put(19, FMT_YMDHMS.get());
        put(14, FMT_YMDHMS_N.get());
        put(23, FMT_YMDHMSS.get());
        put(17, FMT_YMDHMSS_N.get());
        put(10, FMT_YMD.get());
        put(8, FMT_YMD_N.get());
        put(23, FMT_YMDHMSS.get());
    }};
    /**
     * 年
     */
    private static final String YEAR = "year";

    /**
     * 月
     */
    private static final String MONTH = "month";

    /**
     * 周
     */
    private static final String WEEK = "week";

    /**
     * 日
     */
    private static final String DAY = "day";

    /**
     * 时
     */
    private static final String HOUR = "hour";

    /**
     * 分
     */
    private static final String MINUTE = "minute";

    /**
     * 秒
     */
    private static final String SECOND = "second";

    /**
     * 获取当前日期和时间字符串.
     *
     * @return String 日期时间字符串，例如 2015-08-11 09:51:53
     */
    public static String getLocalDateTimeStr() {
        return format(LocalDateTime.now(), DATE_TIME_PATTERN);
    }

    public static Long getCurrentTimeStamp() {
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000;
    }

    /**
     * 获取当前日期字符串.
     *
     * @return String 日期字符串，例如2015-08-11
     */
    public static String getLocalDateStr() {
        return format(LocalDateTime.now(), DATE_TIME_PATTERN);
    }

    /**
     * 获取当前时间字符串.
     *
     * @return String 时间字符串，例如 09:51:53
     */
    public static String getLocalTimeStr() {
        return format(LocalTime.now(), TIME_PATTERN);
    }

    /**
     * 获取当前星期字符串.
     *
     * @return String 当前星期字符串，例如 星期二
     */
    public static String getDayOfWeekStr() {
        return format(LocalDate.now(), "E");
    }

    /**
     * 获取指定日期是星期几
     *
     * @param localDate 日期
     * @return String 星期几
     */
    public static String getDayOfWeekStr(LocalDate localDate) {
        String[] weekOfDays = {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY};
        int dayOfWeek = localDate.getDayOfWeek().getValue() - 1;
        return weekOfDays[dayOfWeek];
    }

    /**
     * 获取日期时间字符串
     *
     * @param temporal 需要转化的日期时间
     * @param pattern  时间格式
     * @return String 日期时间字符串，例如 2015-08-11 09:51:53
     */
    public static String format(TemporalAccessor temporal, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter.format(temporal);
    }


    /**
     * 转换字符串为毫秒
     *
     * @param localDateTimeStr
     * @param pattern
     * @return
     */
    public static Long convertDateStr2Long(String localDateTimeStr, String pattern) {
        LocalDateTime parse = LocalDateTime.parse(localDateTimeStr, DateTimeFormatter.ofPattern(pattern));
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String convertSecondTimeToString(Long time){
        return  DateTimeFormatter.ofPattern(DATETIME_PATTERN).format(LocalDateTime.ofInstant(Instant.ofEpochSecond(time),ZoneId.systemDefault()));
    }

    public static String convertSecondTimeToString(Long time,String datePattern){
        return  DateTimeFormatter.ofPattern(datePattern).format(LocalDateTime.ofInstant(Instant.ofEpochSecond(time),ZoneId.systemDefault()));
    }


    /**
     * Long类型时间戳转时间
     */
    public static String convertMilliTimeToString(Long time){
     return convertMilliTimeToString(time, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    public static String convertMilliTimeToString(Long time,DateTimeFormatter ftf ){
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time),ZoneId.systemDefault()));
    }
    /**
     * 日期字符串转换为日期(java.time.LocalDate)
     *
     * @param localDateStr 日期字符串
     * @param pattern      日期格式 例如DATE_PATTERN
     * @return LocalDate 日期
     */
    public static LocalDate parseLocalDate(String localDateStr, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(localDateStr, dateTimeFormatter);
    }

    /**
     * 获取指定日期时间加上指定数量日期时间单位之后的日期时间.
     *
     * @param localDateTime 日期时间
     * @param num           数量
     * @param chronoUnit    日期时间单位
     * @return LocalDateTime 新的日期时间
     */
    public static LocalDateTime plus(LocalDateTime localDateTime, int num, ChronoUnit chronoUnit) {
        return localDateTime.plus(num, chronoUnit);
    }

    /**
     * 获取指定日期时间减去指定数量日期时间单位之后的日期时间.
     *
     * @param localDateTime 日期时间
     * @param num           数量
     * @param chronoUnit    日期时间单位
     * @return LocalDateTime 新的日期时间
     */
    public static LocalDateTime minus(LocalDateTime localDateTime, int num, ChronoUnit chronoUnit) {
        return localDateTime.minus(num, chronoUnit);
    }

    public static LocalDateTime minus(Long timestamp, int num, ChronoUnit chronoUnit) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).minus(num, chronoUnit);
    }

    public static LocalDateTime minus(int num, ChronoUnit chronoUnit) {
        return LocalDateTime.now().minus(num, chronoUnit);
    }

    /**
     * 根据ChronoUnit计算两个日期时间之间相隔日期时间
     *
     * @param start      开始日期时间
     * @param end        结束日期时间
     * @param chronoUnit 日期时间单位
     * @return long 相隔日期时间
     */
    public static long getChronoUnitBetween(LocalDateTime start, LocalDateTime end, ChronoUnit chronoUnit) {
        return Math.abs(start.until(end, chronoUnit));
    }

    /**
     * 根据ChronoUnit计算两个日期之间相隔年数或月数或天数
     *
     * @param start      开始日期
     * @param end        结束日期
     * @param chronoUnit 日期时间单位,(ChronoUnit.YEARS,ChronoUnit.MONTHS,ChronoUnit.WEEKS,ChronoUnit.DAYS)
     * @return long 相隔年数或月数或天数
     */
    public static long getChronoUnitBetween(LocalDate start, LocalDate end, ChronoUnit chronoUnit) {
        return Math.abs(start.until(end, chronoUnit));
    }

    /**
     * 获取本年第一天的日期字符串
     *
     * @return String 格式：yyyy-MM-dd 00:00:00
     */
    public static String getFirstDayOfYearStr() {
        return getFirstDayOfYearStr(LocalDateTime.now());
    }

    /**
     * 获取本年最后一天的日期字符串
     *
     * @return String 格式：yyyy-MM-dd 23:59:59
     */
    public static String getLastDayOfYearStr() {
        return getLastDayOfYearStr(LocalDateTime.now());
    }

    /**
     * 获取指定日期当年第一天的日期字符串
     *
     * @param localDateTime 指定日期时间
     * @return String 格式：yyyy-MM-dd 00:00:00
     */
    public static String getFirstDayOfYearStr(LocalDateTime localDateTime) {
        return getFirstDayOfYearStr(localDateTime, DATETIME_PATTERN);
    }

    /**
     * 获取指定日期当年最后一天的日期字符串
     *
     * @param localDateTime 指定日期时间
     * @return String 格式：yyyy-MM-dd 23:59:59
     */
    public static String getLastDayOfYearStr(LocalDateTime localDateTime) {
        return getLastDayOfYearStr(localDateTime, DATETIME_PATTERN);
    }

    /**
     * 获取指定日期当年第一天的日期字符串,带日期格式化参数
     *
     * @param localDateTime 指定日期时间
     * @param pattern       日期时间格式
     * @return String 格式：yyyy-MM-dd 00:00:00
     */
    public static String getFirstDayOfYearStr(LocalDateTime localDateTime, String pattern) {
        return format(localDateTime.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0), pattern);
    }

    /**
     * 获取指定日期当年最后一天的日期字符串,带日期格式化参数
     *
     * @param localDateTime 指定日期时间
     * @param pattern       日期时间格式
     * @return String 格式：yyyy-MM-dd 23:59:59
     */
    public static String getLastDayOfYearStr(LocalDateTime localDateTime, String pattern) {
        return format(localDateTime.with(TemporalAdjusters.lastDayOfYear()).withHour(23).withMinute(59).withSecond(59), pattern);
    }

    public static  String getCurrentDateTimeStr(){
        return format(LocalDateTime.now(),DATE_TIME_PATTERN) ;
    }

    public static  String getCurrentDateStr(){
        return format(LocalDateTime.now(),DATETIME_PATTERN) ;
    }


    /**
     * 获取本月最后一天的日期字符串
     *
     * @return String 格式：yyyy-MM-dd 23:59:59
     */
    public static String getLastDayOfMonthStr() {
        return getLastDayOfMonthStr(LocalDateTime.now());
    }


    /**
     * 获取指定日期当月最后一天的日期字符串
     *
     * @param localDateTime 指定日期时间
     * @return String 格式：yyyy-MM-dd 23:59:59
     */
    public static String getLastDayOfMonthStr(LocalDateTime localDateTime) {
        return getLastDayOfMonthStr(localDateTime, DATETIME_PATTERN);
    }

    /**
     * @param num 指定第几天
     * @return String 格式：yyyy-MM-dd
     */
    public static String getDayOfMonthStr(LocalDateTime localDateTime, int num) {
        return format(localDateTime.withDayOfMonth(num).withHour(0).withMinute(0).withSecond(0), DATETIME_PATTERN);
    }

    /**
     * 获取指定日期当月最后一天的日期字符串,带日期格式化参数
     *
     * @param localDateTime 指定日期时间
     * @param pattern       日期时间格式
     * @return String 格式：yyyy-MM-dd 23:59:59
     */
    public static String getLastDayOfMonthStr(LocalDateTime localDateTime, String pattern) {
        return format(localDateTime.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59), pattern);
    }

    /**
     * 获取本周第一天的日期字符串
     *
     * @return String 格式：yyyy-MM-dd 00:00:00
     */
    public static String getFirstDayOfWeekStr() {
        return getFirstDayOfWeekStr(LocalDateTime.now());
    }

    /**
     * 获取指定日期当周第一天的日期字符串,这里第一天为周一
     *
     * @param localDateTime 指定日期时间
     * @return String 格式：yyyy-MM-dd 00:00:00
     */
    public static String getFirstDayOfWeekStr(LocalDateTime localDateTime) {
        return getFirstDayOfWeekStr(localDateTime, DATETIME_PATTERN);
    }


    /**
     * 获取指定日期当周第一天的日期字符串,这里第一天为周一,带日期格式化参数
     *
     * @param localDateTime 指定日期时间
     * @param pattern       日期时间格式
     * @return String 格式：yyyy-MM-dd 00:00:00
     */
    public static String getFirstDayOfWeekStr(LocalDateTime localDateTime, String pattern) {
        return format(localDateTime.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0), pattern);
    }


    public static String getLastDateStrOfWeekStr(DayOfWeek dayOfWeek) {
        return getDateStrOfWeekStr(minus(LocalDateTime.now(), 7, ChronoUnit.DAYS), dayOfWeek);
    }

    public static String getDateStrOfWeekStr(LocalDateTime localDateTime, DayOfWeek dayOfWeek) {
        return format(localDateTime.with(dayOfWeek).withHour(0).withMinute(0).withSecond(0), DATETIME_PATTERN);
    }

    /**
     * 获取指定日期当周最后一天的日期字符串,这里最后一天为周日,带日期格式化参数
     *
     * @param localDateTime 指定日期时间
     * @param pattern       日期时间格式
     * @return String 格式：yyyy-MM-dd 23:59:59
     */
    public static String getLastDayOfWeekStr(LocalDateTime localDateTime, String pattern) {
        return format(localDateTime.with(DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59), pattern);
    }


    /**
     * 实现自定义选择周几
     *
     * @param localDateTime
     * @param pattern
     * @param dayOfWeek
     * @return
     */
    public static String getLastDayOfWeekStr(LocalDateTime localDateTime, String pattern, DayOfWeek dayOfWeek) {
        return format(localDateTime.with(dayOfWeek).withHour(23).withMinute(59).withSecond(59), pattern);
    }


    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


    /**
     * 将字符串日期转换为Date类型
     *
     * @param date
     * @return
     */
    public static Date strToDate(String date, int length) {
        try {
            SimpleDateFormat sdf = FMT_LENGTH_MAP.get(length);
            if (null != sdf) {
                return sdf.parse(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 返回上周日的时间
     *
     * @return
     */
    public static String getLastSundayDatStr() {
        return DateUtils.getLastDayOfWeekStr(DateUtils.minus(LocalDateTime.now(), 7, ChronoUnit.DAYS), DATETIME_PATTERN);
    }

    public static String constellation(String monthStr, String dayStr) {
        Integer month = 0;
        Integer day = 0;
        String constellation = "";
        try {
            if (StringUtils.isNoneBlank(monthStr) || StringUtils.isNoneBlank(dayStr)) {
                month = Integer.valueOf(monthStr);
                day = Integer.valueOf(dayStr);
            }
        } catch (NumberFormatException e) {
            //log.error("monthStr:{},dayStr:{}, constellation error message:{}", monthStr, dayStr, e.getMessage());
        }
        if (month == 1 && day >= 20 || month == 2 && day <= 18) {
            constellation = "水瓶座";
        } else if (month == 2 && day >= 19 || month == 3 && day <= 20) {
            constellation = "双鱼座";
        } else if (month == 3 && day >= 21 || month == 4 && day <= 19) {
            constellation = "白羊座";
        } else if (month == 4 && day >= 20 || month == 5 && day <= 20) {
            constellation = "金牛座";
        } else if (month == 5 && day >= 21 || month == 6 && day <= 21) {
            constellation = "双子座";
        } else if (month == 6 && day >= 22 || month == 7 && day <= 22) {
            constellation = "巨蟹座";
        } else if (month == 7 && day >= 23 || month == 8 && day <= 22) {
            constellation = "狮子座";
        } else if (month == 8 && day >= 23 || month == 9 && day <= 22) {
            constellation = "处女座";
        } else if (month == 9 && day >= 23 || month == 10 && day <= 23) {
            constellation = "天秤座";
        } else if (month == 10 && day >= 24 || month == 11 && day <= 22) {
            constellation = "天蝎座";
        } else if (month == 11 && day >= 23 || month == 12 && day <= 21) {
            constellation = "射手座";
        } else if (month == 12 && day >= 22 || month == 1 && day <= 19) {
            constellation = "摩羯座";
        }
        return constellation;
    }


    public static void main(String[] args) {
        System.out.println(DateUtils.format(DateUtils.minus(LocalDateTime.now(), 0, ChronoUnit.DAYS), DateUtils.DATETIME_PATTERN));
        System.out.println(DateUtils.getDayOfMonthStr(DateUtils.minus(1, ChronoUnit.MONTHS), 1));
        System.out.println(DateUtils.getLastDateStrOfWeekStr(DayOfWeek.SUNDAY));
        // println(DateUtils.format(DateUtils.minus(LocalDateTime.now,1, ChronoUnit.DAYS), DateUtils.DATETIME_PATTERN))
        //  val lastMounthFirstDayStr: String = "2021-08-01" //DateUtils.getFirstDayOfMonthStr(LocalDateTime.now,DateUtils.DATE_PATTERN)
        // val lastSundayDateStr=DateUtils.getDateStrOfWeekStr(DayOfWeek.SUNDAY)
       /* System.out.println(getLocalDateTimeStr());
        System.out.println(getLocalDateStr());
        System.out.println(getLocalTimeStr());
        System.out.println(getDayOfWeekStr());
        System.out.println(getDayOfWeekStr(LocalDate.now()));

        System.out.println("========");
        System.out.println(format(LocalDate.now(), UNSIGNED_DATE_PATTERN));

        System.out.println("========");
        System.out.println(parseLocalDateTime("2020-12-13 11:14:12", DATETIME_PATTERN));
        System.out.println(parseLocalDate("2020-12-13", DATE_PATTERN));

        System.out.println("========");
        System.out.println(plus(LocalDateTime.now(), 3, ChronoUnit.HOURS));
        System.out.println(minus(LocalDateTime.now(), 4, ChronoUnit.DAYS));

        System.out.println("========");
        System.out.println(getChronoUnitBetween(LocalDateTime.now(), parseLocalDateTime("2020-12-12 12:03:12", DATETIME_PATTERN), ChronoUnit.MINUTES));
        System.out.println(getChronoUnitBetween(LocalDate.now(), parseLocalDate("2021-12-12", DATE_PATTERN), ChronoUnit.WEEKS));

        System.out.println("========");
        System.out.println(getFirstDayOfYearStr());
        System.out.println(getFirstDayOfYearStr(parseLocalDateTime("2021-12-12 12:03:12", DATETIME_PATTERN)));
        System.out.println(getFirstDayOfYearStr(parseLocalDateTime("2021-12-12 12:03:12", DATETIME_PATTERN), UNSIGNED_DATETIME_PATTERN));

        System.out.println(getLastDayOfYearStr());
        System.out.println(getLastDayOfYearStr(parseLocalDateTime("2021-12-12 12:03:12", DATETIME_PATTERN)));
        System.out.println(getLastDayOfYearStr(parseLocalDateTime("2021-12-12 12:03:12", DATETIME_PATTERN), UNSIGNED_DATETIME_PATTERN));

        System.out.println("========");
        System.out.println(getFirstDayOfMonthStr());
        System.out.println(getFirstDayOfMonthStr(parseLocalDateTime("2021-12-12 12:03:12", DATETIME_PATTERN)));
        System.out.println(getFirstDayOfMonthStr(parseLocalDateTime("2021-12-12 12:03:12", DATETIME_PATTERN), UNSIGNED_DATETIME_PATTERN));

        System.out.println(getLastDayOfMonthStr());
        System.out.println(getLastDayOfMonthStr(parseLocalDateTime("2021-12-12 12:03:12", DATETIME_PATTERN)));
        System.out.println(getLastDayOfMonthStr(parseLocalDateTime("2021-12-12 12:03:12", DATETIME_PATTERN), UNSIGNED_DATETIME_PATTERN));

        System.out.println("========");
        System.out.println(getFirstDayOfWeekStr());
        System.out.println(getFirstDayOfWeekStr(parseLocalDateTime("2021-12-12 12:03:12", DATETIME_PATTERN)));
        System.out.println(getFirstDayOfWeekStr(parseLocalDateTime("2021-12-12 12:03:12", DATETIME_PATTERN), UNSIGNED_DATETIME_PATTERN));
        System.out.println(getLastDayOfWeekStr(LocalDateTime.now(), UNSIGNED_DATETIME_PATTERN));*/
    }

}
