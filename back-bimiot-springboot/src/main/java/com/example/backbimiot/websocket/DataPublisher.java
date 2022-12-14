package com.example.backbimiot.websocket;

import com.example.backbimiot.simulator.ConverterEvent;
import com.example.backbimiot.simulator.SimulatorEvent;
import org.json.JSONObject;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class DataPublisher implements ApplicationListener<ConverterEvent> {

    private final MessageSendingOperations<String> messageSendingOperations;

    public DataPublisher(MessageSendingOperations<String> messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    @Override
    public void onApplicationEvent(ConverterEvent event) {
        System.out.println("im in the data pub" + event);
        sendPeriodicMessages(event);
    }

    //@Scheduled(fixedDelay = 10000)
    public void sendPeriodicMessages(ConverterEvent event) {

        this.messageSendingOperations.convertAndSend("/data/sensors", event.getMessage());
    }
}