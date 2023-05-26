package com.example.weatherapp.model;

public class LocationHisFirebase {
    private String location;
    private String timestampMs;


    public LocationHisFirebase(String location, String timestampMs) {
        this.location = location;
        this.timestampMs = timestampMs;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(String timestampMs) {
        this.timestampMs = timestampMs;
    }
}
