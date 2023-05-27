package com.example.weatherapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String convertLocalTimeEpoch(String epoch) {
        long l = Long.valueOf(epoch);
        Date date = new Date(l * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd/MM", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }

    public static String convertStringDoubleToStringInt(String s) {
        Double d = Double.valueOf(s);
        return String.valueOf(d.intValue());
    }

    public static String formatDate(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM");

        try {
            Date d = inputFormat.parse(date);
            String outputDate = outputFormat.format(d);
            return outputDate.toString();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentTimestampMs(){
        return String.valueOf(new Date().getTime());
    }

    public static void main(String[] args) {
        System.out.println(formatDate("2023-05-24"));
    }
}
