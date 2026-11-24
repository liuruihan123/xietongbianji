package com.xmzs.common.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
/**
 * 时间工具类
 *
 * @author ruoyi
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final String YYYY = "yyyy";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String[] PARSE_PATTERNS = {
        "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
        "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
        "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), PARSE_PATTERNS);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

//    public static void main(String[] args) {
//        int monthsAfter = 1;
//        long expiryTimestamp = calculateExpiryTimestamp(monthsAfter);
//        System.out.println("会员到期时间戳: " + expiryTimestamp);
//    }

    /**
     * 计算会员到期时间戳
     *
     * @param monthsAfter 当前时间之后的月份数
     * @return 到期时间的时间戳（秒）
     */
    public static long calculateExpiryTimestamp(int monthsAfter) {
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();

        // 计算到期时间
        LocalDateTime expiryTime = currentTime.plus(monthsAfter, ChronoUnit.MONTHS);

        // 转换为时间戳（秒）
        return expiryTime.toEpochSecond(ZoneOffset.UTC);
    }

    /**
     * 计算会员到期时间戳
     *
     * @param daysAfter 当前时间之后的天数
     * @return 到期时间的时间戳（秒）
     */
    public static long calculateExpiryTimestampByDay(int daysAfter) {
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();

        // 计算到期时间
        LocalDateTime expiryTime = currentTime.plus(daysAfter, ChronoUnit.DAYS);

        // 转换为时间戳（秒）
        return expiryTime.toEpochSecond(ZoneOffset.UTC);
    }


    /**
     * 会员续费时间戳
     *
     * @param monthsAfter 当前时间之后的月份数
     * @return 到期时间的时间戳（秒）
     */
    public static long renewExpiryTimestamp(int monthsAfter, Long vipExpiryTimestamp) {
        // 将 vipExpiryTimestamp 转换为 LocalDateTime
        LocalDateTime expiryTime = LocalDateTime.ofEpochSecond(vipExpiryTimestamp, 0, ZoneOffset.UTC);

        // 在到期时间上增加 monthsAfter 个月
        LocalDateTime newExpiryTime = expiryTime.plus(monthsAfter, ChronoUnit.MONTHS);

        // 转换为时间戳（秒）
        return newExpiryTime.toEpochSecond(ZoneOffset.UTC);
    }

    /**
     * 检测会员是否过期
     *
     * @param expiryTimestamp 会员到期时间戳
     * @return 如果当前时间大于到期时间戳，则返回 true（已过期），否则返回 false（未过期）
     */
    public static boolean isExpired(long expiryTimestamp) {
        // 获取当前时间的时间戳（秒）
        long currentTimestamp = Instant.now().getEpochSecond();

        // 比较当前时间戳和到期时间戳
        return currentTimestamp > expiryTimestamp;
    }

//    public static void main(String[] args) {
//        int monthsAfter = 6; // 例如，6个月后
//        long expiryTimestamp = calculateExpiryTimestamp(monthsAfter);
//        System.out.println("会员到期时间戳: " + expiryTimestamp);
//
//        // 检查会员是否过期
//        boolean expired = isExpired(1732450673);
//        if (expired) {
//            System.out.println("会员已过期");
//        } else {
//            System.out.println("会员未过期");
//        }
//    }
    /**
     * 计算相差天数
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor) {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor) {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }
}
