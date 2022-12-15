package com.example.backbimiot.pojo;

public class Sensor {


    private String sensorDataSetId;
    private String sensorIFCid;

    public Sensor(String sensorDataSetId, String sensorIFCid) {
        this.sensorDataSetId = sensorDataSetId;
        this.sensorIFCid = sensorIFCid;
    }

    public Sensor() {}

    public String getSensorDataSetId() {
        return sensorDataSetId;
    }

    public String getSensorIFCid() {
        return sensorIFCid;
    }
}