package com.github.kingschan1204.easycrawl.helper.datetime;


import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author kings.chan
 * @date 2021-9-10
 */
@Slf4j
public class DateHelper {


    /**
     * 得到当前时间戳
     *
     * @return
     */
    public static Long getUnixTimeStamp() {
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        return Long.valueOf(time);
    }

    /**
     * 当前时间加传入天数后的时间戳
     *
     * @return
     */
    public static Long getPlusDayUnixTimeStamp(int day) {
        LocalDateTime newtime = LocalDateTime.now().plusDays(day);
        long ts = newtime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;
        return Long.valueOf(String.valueOf(ts));
    }

    /**
     * 得到当前系统时间
     *
     * @return
     */
    public static String now() {
        return now(null);
    }

    /**
     * 得到当前系统时间
     *
     * @param format 日期格式
     * @return
     */
    public static String now(String format) {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(null == format ? "yyyy-MM-dd HH:mm:ss" : format);
        return dateFormat.format(time);
    }

    /**
     * 格式化时间戳
     *
     * @param timestamp
     * @param formatStr
     * @return
     */
    public static String formatTimeStamp(Long timestamp, String formatStr) {
        /*if (!String.valueOf(timestamp).matches("\\d{10}|\\d{13}")) {
            log.warn("时间戳格式不对：{}转换失败！", timestamp);
            return String.valueOf(timestamp);
        }*/
        if (String.valueOf(timestamp).length() > 10) {
            timestamp = timestamp / 1000;
        }
        LocalDateTime time = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(8));
        String ft = Optional.ofNullable(formatStr).filter(v -> !v.isEmpty()).orElse("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter format = DateTimeFormatter.ofPattern(ft);
        return format.format(time);
    }

    /**
     * 字符串日期转时间戳
     *
     * @param dateStr
     * @return
     */
    public static Long toTimeStamp(String dateStr) {
        String date = dateStr.contains(".") ? dateStr.replaceAll("\\..*", "") : dateStr;
        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long timestamp = localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
            return Long.valueOf(String.valueOf(timestamp / 1000));
        } else if (date.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            LocalDateTime localDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            long ts = localDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;
            return Long.valueOf(String.valueOf(ts));
        }
        throw new RuntimeException(String.format("不支持的日期格式:%s", dateStr));
    }

    public static void main(String[] args) {
        System.out.println(DateHelper.formatTimeStamp(968083200000L, "yyyy-MM-dd HH:mm:ss"));
    }

}
