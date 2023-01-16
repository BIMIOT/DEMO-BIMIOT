package com.example.backbimiot.simulator;

import com.example.backbimiot.controller.RoomHashMapEvent;
import com.example.backbimiot.pojo.Room;
import com.example.backbimiot.pojo.Sensor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Converter implements ApplicationListener<SimulatorEvent> {
    public record SensorByRoom(Sensor sensor, Room room) {}
    private final HashMap<String, SensorByRoom> hashMap = new HashMap<>();
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public Map<String, SensorByRoom> getSensorByRoom() {
        return hashMap;
    }

    private void setSensorsByRoom(List<Room> rooms) {
        rooms.forEach(r -> r.getSensors().forEach(s -> hashMap.put(s.getSensorDataSetId(), new SensorByRoom(s,r))));
    }

    private void publishCustomEvent(final String message) {
        System.out.println("Publishing custom event. ");
        ConverterEvent customSpringEvent = new ConverterEvent(this, message);
        System.out.println("convert message");
        applicationEventPublisher.publishEvent(customSpringEvent);
    }


    @EventListener
    public void onApplicationEvent(RoomHashMapEvent event) {
        setSensorsByRoom(event.getMessage());
        System.out.println("Received  HashMap custom event - " + event.getMessage());
    }

    @EventListener
    public void onApplicationEvent(SimulatorEvent event) {
        if(hashMap.isEmpty()) {
            System.out.println("still empty ! ");
            return;
        }
        var sensor = event.getMessage();
        var sen= hashMap.get(sensor.id());
        var sensorJson = new JSONObject()
                .put("sensorIfcID",sen.sensor().getSensorIFCid())
                .put("value",sensor.value()).put("timestamp", sensor.timestamp());

        publishCustomEvent(sensorJson.toString());
        System.out.println("Received spring custom event - " + event.getMessage());
    }
}