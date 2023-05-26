package com.example.weatherapp.model;

public class LocationHis {
    private String location, country, temperature;

    public LocationHis(String location, String country, String temperature) {
        this.location = location;
        this.country = country;
        this.temperature = temperature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
