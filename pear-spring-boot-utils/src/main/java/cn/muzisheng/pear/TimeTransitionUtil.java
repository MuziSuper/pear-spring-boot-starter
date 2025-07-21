package cn.muzisheng.pear;

import cn.muzisheng.pear.exception.TimeException;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * 时间转换工具类
 **/
public class TimeTransitionUtil {
    /**
     * 将UTC时间戳转换为LocalDateTime时间
     * @param time UTC时间戳
     * @return LocalDateTime时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalDateTime UTCToLocalDateTime(long time) {
        LocalDateTime localDateTime;
        try {
            Instant instant = Instant.ofEpochMilli(time);
            localDateTime = LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
        }catch (DateTimeException e){
            throw new TimeException("UTC conversion of LocalDateTime failed.");
        }
        return localDateTime;
    }
    /**
     * 将UTC时间戳转换为LocalTime时间
     * @param time UTC时间戳
     * @return LocalTime时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalTime UTCToLocalTime(long time) {
        LocalTime localTime;
        try {
            Instant instant = Instant.ofEpochMilli(time);
            localTime = LocalTime.ofInstant(instant, java.time.ZoneId.systemDefault());
        }catch (DateTimeException e){
            throw new TimeException("UTC conversion of LocalTime failed.");
        }
        return localTime;
    }
    /**
     * 将UTC时间戳转换为LocalDate时间
     * @param time UTC时间戳
     * @return LocalTime时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalDate UTCToLocalDate(long time) {
        LocalDate localDate;
        try {
            Instant instant = Instant.ofEpochMilli(time);
            localDate = LocalDate.ofInstant(instant, java.time.ZoneId.systemDefault());
        }catch (DateTimeException e){
            throw new TimeException("UTC conversion of LocalDate failed.");
        }
        return localDate;
    }
    /**
     * 将UTC时间戳转换为Date时间
     * @param time UTC时间戳
     * @return Date时间
     * @throws TimeException 时间转换异常
     **/
    public static Date UTCToDate(long time) {
        Date date;
        try {
            Instant instant = Instant.ofEpochMilli(time);
            date = Date.from(instant);
        }catch (NullPointerException|IllegalArgumentException e){
            throw new TimeException("UTC conversion of Date failed.");
        }
        return date;
    }
    /**
     * 将LocalDateTime时间转换为UTC时间戳
     * @param time UTC时间戳
     * @return LocalDateTime时间
     * @throws TimeException 时间转换异常
     **/
    public static long LocalDateTimeToUTC(LocalDateTime time) {
        long UTC;
        try {
            UTC = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }catch (ArithmeticException e){
            throw new TimeException("LocalDateTime conversion to UTC failed.");
        }
        return UTC;
    }
    /**
     * 将LocalDateTime时间转换为LocalDate时间
     * @param time LocalDateTime时间
     * @return LocalDate时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalDate LocalDateTimeToLocalDate(LocalDateTime time) {
        LocalDate localDate;
        try {
            localDate = time.toLocalDate();
        }catch (DateTimeException e){
            throw new TimeException("LocalDateTime conversion to LocalDate failed.");
        }
        return localDate;
    }
    /**
     * 将LocalDateTime时间转换为LocalTime时间
     * @param time LocalDateTime时间
     * @return LocalTime时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalTime LocalDateTimeToLocalTime(LocalDateTime time) {
        LocalTime localTime;
        try {
            localTime = time.toLocalTime();
        }catch (DateTimeException e){
            throw new TimeException("LocalDateTime conversion to LocalTime failed.");
        }
        return localTime;
    }
    /**
     * 将LocalDateTime时间转换为Date时间
     * @param time LocalDateTime时间
     * @return Date时间
     * @throws TimeException 时间转换异常
     **/
    public static Date LocalDateTimeToDate(LocalDateTime time) {
        Date date;
        try {
            date = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        }catch (DateTimeException e){
            throw new TimeException("LocalDateTime conversion to Date failed.");
        }
        return date;
    }

    /**
     * 将LocalDate时间转换为UTC时间戳
     * @param time LocalDate时间
     * @return UTC时间戳
     * @throws TimeException 时间转换异常
     **/
    public static long LocalDateToUTC(LocalDate time) {
        long UTC;
        try {
            UTC = time.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }catch (DateTimeException e){
            throw new TimeException("LocalDate conversion to UTC failed.");
        }
        return UTC;
    }
    /**
     * 将LocalDate时间转换为LocalDateTime时间
     * @param time LocalDate时间
     * @return LocalDateTime时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalDateTime LocalDateToLocalDateTime(LocalDate time) {
        LocalDateTime localDateTime;
        try {
            localDateTime = time.atStartOfDay();
        }catch (DateTimeException e){
            throw new TimeException("LocalDate conversion to LocalDateTime failed.");
        }
        return localDateTime;
    }
    /**
     * 将LocalDate时间转换为Date时间
     * @param time LocalDate时间
     * @return Date时间
     * @throws TimeException 时间转换异常
     **/
    public static Date LocalDateToDate(LocalDate time) {
        Date date;
        try {
            date = Date.from(time.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }catch (DateTimeException e){
            throw new TimeException("LocalDate conversion to Date failed.");
        }
        return date;
    }
    /**
     * 将Date时间转换为LocalDateTime时间
     * @param time Date时间
     * @return LocalDateTime时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalDateTime DateToLocalDateTime(Date time) {
        LocalDateTime localDateTime;
        try {
            localDateTime = time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }catch (DateTimeException e){
            throw new TimeException("Date conversion to LocalDateTime failed.");
        }
        return localDateTime;
    }
    /**
     * 将Date时间转换为LocalDate时间
     * @param time Date时间
     * @return LocalDate时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalDate DateToLocalDate(Date time) {
        LocalDate localDate;
        try {
            localDate = time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }catch (DateTimeException e){
            throw new TimeException("Date conversion to LocalDate failed.");
        }
        return localDate;
    }
    /**
     * 将Date时间转换为LocalTime时间
     * @param time Date时间
     * @return LocalTime时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalTime DateToLocalTime(Date time) {
        LocalTime localTime;
        try {
            localTime = time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        }catch (DateTimeException e){
            throw new TimeException("Date conversion to LocalTime failed.");
        }
        return localTime;
    }
    /**
     * 将Date时间转换为UTC时间戳
     * @param time Date时间
     * @return UTC时间戳
     * @throws TimeException 时间转换异常
     **/
    public static long DateToUTC(Date time) {
        long UTC;
        try {
            UTC = time.toInstant().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }catch (DateTimeException e){
            throw new TimeException("Date conversion to UTC failed.");
        }
        return UTC;
    }
    /**
     * 将Date时间字符串转换为Date时间
     * @param time Date时间字符串
     * @return Date时间
     * @throws TimeException 时间转换异常
     **/
    public static Date stringToDate(String time) {
        Date date;
        try {
            date = Date.from(LocalDateTime.parse(time).atZone(ZoneId.systemDefault()).toInstant());
        }catch (
                NullPointerException|IllegalArgumentException|DateTimeParseException e){
            throw new TimeException("String conversion to Date failed.");
        }
        return date;
    }
    /**
     * 将LocalDate时间字符串转换为LocalDate时间
     * @param time LocalDate时间字符串
     * @return LocalDate时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalDate stringToLocalDate(String time) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(time);
        }catch (DateTimeParseException  e){
            throw new TimeException("String conversion to LocalDate failed.");
        }
        return localDate;
    }
    /**
     * 将LocalTime时间字符串转换为LocalTime时间
     * @param time LocalTime时间字符串
     * @return LocalTime时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalTime stringToLocalTime(String time) {
        LocalTime localTime;
        try {
            localTime = LocalTime.parse(time);
        }catch (DateTimeParseException  e){
            throw new TimeException("String conversion to LocalTime failed.");
        }
        return localTime;
    }
    /**
     * 将LocalDateTime时间字符串转换为LocalDateTime时间
     * @param time LocalDateTime时间字符串
     * @return LocalDateTime时间
     * @throws TimeException 时间转换异常
     **/
    public static LocalDateTime stringToLocalDateTime(String time) {
        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(time);
        }catch (DateTimeParseException  e){
            throw new TimeException("String conversion to LocalDateTime failed.");
        }
        return localDateTime;
    }
    /**
     * 将UTC字符串转换为UTC时间戳
     * @param time UTC字符串
     * @return UTC时间戳
     * @throws TimeException 时间转换异常
     **/
    public static long stringToUTC(String time) {
        long UTC;
        try {
            UTC = LocalDateTime.parse(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }catch (DateTimeParseException|ArithmeticException e){
            throw new TimeException("String conversion to UTC failed.");
        }
        return UTC;
    }

}
