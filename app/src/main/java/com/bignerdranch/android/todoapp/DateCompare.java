package com.bignerdranch.android.todoapp;


import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class DateCompare {


    public static void compare(Date date, TextView textView) {
        Calendar instanceCalendar = Calendar.getInstance();
        int instanceYear = instanceCalendar.get(Calendar.YEAR);
        int instanceMonth = instanceCalendar.get(Calendar.MONTH);
        int instanceDay = instanceCalendar.get(Calendar.DAY_OF_MONTH);

//        Log.i("DateCompare", instanceDay + " " + instanceMonth + " " + instanceYear);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

//        Log.i("DateCompare", day + " " + month + " " + year);

        if (year < instanceYear) {
            textView.setTextColor(0xAAFF0000);
        } else if (year == instanceYear) {
            if (month < instanceMonth) {
                textView.setTextColor(0xAAFF0000);
            } else if (month == instanceMonth) {
                if (day < instanceDay) {
                    textView.setTextColor(0xAAFF0000);
                } else if (day == instanceDay) {
                    textView.setTextColor(0xAA0000FF);
                } else {
                    textView.setTextColor(0xAA333333);
                }
            } else {
                textView.setTextColor(0xAA333333);
            }
        } else {
            textView.setTextColor(0xAA333333);
        }





    }
}
