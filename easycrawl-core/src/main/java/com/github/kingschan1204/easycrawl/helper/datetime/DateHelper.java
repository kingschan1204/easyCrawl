package com.github.kingschan1204.easycrawl.helper.datetime;


import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * java8日期工具类封装
 *
 * @author kings.chan
 * @date 2021-9-10
 */
@Slf4j
public class DateHelper {

    private LocalDateTime localDateTime;

    public DateHelper(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    /**
     * 初始对象
     *
     * @return DateHelperNew
     */
    public static DateHelper now() {
        return new DateHelper(LocalDateTime.now());
    }

    /**
     * 初始对象
     *
     * @param timeStamp 时间戳
     * @return DateHelperNew
     */
    public static DateHelper of(Long timeStamp) {
        Assert.notNull(timeStamp, "时间戳不能为空！");
        if (String.valueOf(timeStamp).length() > 10) {
            timeStamp = timeStamp / 1000;
        }
        LocalDateTime time = LocalDateTime.ofEpochSecond(timeStamp, 0, ZoneOffset.ofHours(8));
        return new DateHelper(time);
    }

    /**
     * 初始对象
     *
     * @param text 格式：2023-04-01,2023-4-1,2023-04-01 00:00:00,2022-2-1T12:55:00,20230401
     * @return DateHelperNew
     */
    public static DateHelper of(String text) {
        String date = text.contains(".") ? text.replaceAll("\\..*", "") : text;
        if (date.contains("T")) {
            date = date.replace("T", " ");
        }
        if (date.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            String[] array = date.split("-");
            date = String.format("%s-%02d-%02d 00:00:00", array[0], Integer.valueOf(array[1]), Integer.valueOf(array[2]));
        } else if (date.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}")) {
            String[] array = date.replaceAll("\\s.*", "").split("-");
            date = String.format("%s-%02d-%02d %s", array[0], Integer.valueOf(array[1]), Integer.valueOf(array[2]), date.split("\\s")[1]);
        } else if (date.matches("\\d{8}")) {
            date = String.format("%s-%s-%s 00:00:00", date.substring(0, 4), date.substring(4, 6), date.substring(6));
        } else {
            throw new RuntimeException("不支持的格式：" + text);
        }
        LocalDateTime localDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new DateHelper(localDate);
    }


    public DateHelper plusYears(long years) {
        this.localDateTime = localDateTime.plusYears(years);
        return this;
    }

    public DateHelper plusMonths(long months) {
        this.localDateTime = localDateTime.plusMonths(months);
        return this;
    }

    /**
     * 日期天数相加
     *
     * @param days 要加的天数
     * @return DateHelperNew
     */
    public DateHelper plusDays(long days) {
        this.localDateTime = localDateTime.plusDays(days);
        return this;
    }

    public DateHelper minusYears(long years) {
        this.localDateTime = localDateTime.minusYears(years);
        return this;
    }

    public DateHelper minusMonths(long months) {
        this.localDateTime = localDateTime.minusMonths(months);
        return this;
    }

    /**
     * 日期天数相减
     *
     * @param days 要减的天数
     * @return DateHelperNew
     */
    public DateHelper minusDays(long days) {
        this.localDateTime = localDateTime.minusDays(days);
        return this;
    }

    /**
     * 得到当前时间戳
     *
     * @return 10位的时间戳
     */
    public Long timeStamp() {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;
    }

    /**
     * 返回日期的年
     *
     * @return yyyy年
     */
    public String year() {
        return format("yyyy");
    }

    /**
     * 返回日期的月
     *
     * @return MM月
     */
    public String month() {
        return format("MM");
    }

    /**
     * 返回日期的日
     *
     * @return dd日
     */
    public String day() {
        return format("dd");
    }

    /**
     * 返回日期
     *
     * @return yyyy-MM-dd
     */
    public String date() {
        return format("yyyy-MM-dd");
    }

    /**
     * 返回日期
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public String dateTime() {
        return format("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 返回时间
     *
     * @return HH:mm:ss
     */
    public String time() {
        return format("HH:mm:ss");
    }

    /**
     * 按传入的格式 格式化日期
     *
     * @param format
     * @return
     */
    public String format(String format) {
        return DateTimeFormatter.ofPattern(format).format(localDateTime);
    }

    public static void main(String[] args) {
        String[] dates = {"2022-2-1", "2022-12-1", "2022-2-12", "2022-2-1 12:55:00", "2022-2-1T12:55:00", "2022-02-01 00:00:00", "20220201"};
        for (String date : dates) {
            System.out.println(DateHelper.of(date).date());
        }
    }

}
