package com.example.backbimiot.controller;

import com.example.backbimiot.pojo.Room;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/api")
public class MyController {
    @PostMapping("/rooms")
    public void storeSensorsByRooms(@RequestBody Map<String, List<Room>> body) {
        var rooms = body.get("rooms");
        System.out.println(rooms.get(0).getRoomId());
    }
}



