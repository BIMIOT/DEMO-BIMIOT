package com.example.backbimiot.controller;

import com.example.backbimiot.pojo.Room;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class RoomHashMapEvent extends ApplicationEvent {
    private List<Room> rooms;

    public RoomHashMapEvent(Object source, List<Room> rooms) {
        super(source);
        this.rooms = rooms;
    }
    public List<Room> getMessage() {
        return rooms;
    }
}