package com.example.backbimiot;

import com.example.backbimiot.controller.RoomHashMapEvent;
import com.example.backbimiot.pojo.Room;
import com.example.backbimiot.pojo.Sensor;
import com.example.backbimiot.simulator.Converter;
import com.example.backbimiot.websocket.SubscribeMappingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BackBimiotApplicationTests {

    @Autowired
    private SubscribeMappingController sub;

    @Autowired
    private Converter converter;

    @Test
    public void subscribeTest() {
        var answer = "server one-time message via the application";
        assertEquals(answer, sub.sendOneTimeMessage());
    }

    @Test
    public void getRooms() {
        var sensor = new Sensor("2", "3");
        var sensors = new ArrayList<Sensor>();
        sensors.add(sensor);
        var room = new Room();
        room.setRoomId("1");
        room.setSensors(sensors);
        var rooms = new ArrayList<Room>();
        rooms.add(room);
        converter.onApplicationEvent(new RoomHashMapEvent(this, rooms));
        var result = converter.getSensorByRoom();
        var answer = new HashMap<String, Converter.SensorByRoom>();
        var sensorByRoom = new Converter.SensorByRoom(sensor, room);
        assertEquals(result.get("2"), sensorByRoom);
    }

}
