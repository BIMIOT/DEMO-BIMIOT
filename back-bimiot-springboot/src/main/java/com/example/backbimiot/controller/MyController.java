package com.example.backbimiot.controller;

import com.example.backbimiot.pojo.Room;
import com.example.backbimiot.simulator.Simulator;
import com.example.backbimiot.simulator.SimulatorEvent;
import com.example.backbimiot.simulator.simpojo.SensorSim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class MyController {
    private final SimulatorController simulatorController;
    @Autowired
    public MyController(SimulatorController simulatorController) {
        this.simulatorController = simulatorController;
    }

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    private void publishCustomEvent(final List<Room> rooms) {
        System.out.println("Publishing custom event. ");
        applicationEventPublisher.publishEvent(new RoomHashMapEvent(this,rooms));
    }
    @PostMapping("/rooms")
    @CrossOrigin(origins = "http://localhost:8081")
    public void storeSensorsByRooms(@RequestBody Map<String, List<Room>> body) {
        var rooms = body.get("rooms");
        publishCustomEvent(rooms);
    }

    @PostMapping("/start")
    @CrossOrigin(origins = "http://localhost:8081")
    public void start() {
        System.out.println("lancement");
       simulatorController.start();
    }

    @PostMapping("/stop")
    @CrossOrigin(origins = "http://localhost:8081")
    public void stop() {
        simulatorController.stop();
    }
}



