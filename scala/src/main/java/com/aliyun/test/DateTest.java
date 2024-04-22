package com.aliyun.test;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author twan
 * @version 1.0
 * @description 时间测试
 * @date 2024-04-14 15:57:11
 */
public class DateTest {

    public static void main(String[] args) {
        final TimeZone timeZone = TimeZone.getTimeZone("UTC");
        TimeZone.setDefault(timeZone);
        Timestamp timestamp = Timestamp.valueOf("2024-03-27 13:05:43");
        Instant instant = timestamp.toInstant();
        long epochSecond = instant.getEpochSecond();
        System.out.println(instant);
        System.out.println("second = " + epochSecond);
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println(ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(epochSecond * 1000), ZoneId.of("America/New_York"))));

        //System.out.println("args = " +instant);

    }
}
