package com.example.backbimiot.pojo;

import java.util.List;

public class Room {
    private String roomId;
    private List<Sensor> sensors;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }
}

