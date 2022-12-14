package com.example.backbimiot.simulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class Converter implements ApplicationListener<SimulatorEvent> {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private void publishCustomEvent(final String message) {
        System.out.println("Publishing custom event. ");
        ConverterEvent customSpringEvent = new ConverterEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }

    @Override
    public void onApplicationEvent( SimulatorEvent event) {
        publishCustomEvent(event.getMessage());
        System.out.println("Received spring custom event - " + event.getMessage());
    }
}