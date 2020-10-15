package org.ipc.dahua.dto;

/**
 * @Author 洋芋_Sir
 * @Date 2020/6/30
 * @description
 **/
public class Birthday{
    private int year;
    private int month;
    private int day;

    public Birthday(String birthday){
        String[] birthdays = birthday.split("-");
        year = Integer.parseInt(birthdays[0]);
        month = Integer.parseInt(birthdays[1]);
        day = Integer.parseInt(birthdays[2]);
    }

    public Birthday(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}