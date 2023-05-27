package com.example.weatherapp.model;

import java.util.ArrayList;

public class Forecast {
    private String date;
    private String status;
    private String imageUrl;
    private String maxTemp;
    private String minTemp;
    private String wind;

    private ArrayList<Integer> temps;

    public Forecast(String date, String status, String imageUrl, String maxTemp, String minTemp, String wind) {
        this.date = date;
        this.status = status;
        this.imageUrl = imageUrl;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.wind = wind;
        this.temps = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public ArrayList<Integer> getTemps() {
        return temps;
    }

    public void setTemps(ArrayList<Integer> temps) {
        this.temps = temps;
    }
}
