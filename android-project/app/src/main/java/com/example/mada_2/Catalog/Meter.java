package com.example.mada_2.Catalog;

public class Meter {
    public Meter(int id, String name, String meter_reading) {
        this.id = id;
        this.name = name;
        this.meter_reading = meter_reading;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMeter_reading() {
        return meter_reading;
    }

    public String meter_reading = "0";

    public String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int id;
}
