package com.example.backbimiot;

import com.example.backbimiot.websocket.SubscribeMappingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BackBimiotApplicationTests {

    @Autowired
    private SubscribeMappingController sub;

    @Test
    public void subscribeTest() {
        var answer = "server one-time message via the application";
        assertEquals(answer, sub.sendOneTimeMessage());
    }

}
