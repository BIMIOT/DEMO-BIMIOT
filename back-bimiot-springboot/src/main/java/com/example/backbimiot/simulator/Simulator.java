package com.example.backbimiot.simulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class Simulator {



    private final ScheduledExecutorService executorService;

    public Simulator() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private void publishCustomEvent(final String message) {
        System.out.println("Publishing custom event. ");
        SimulatorEvent customSpringEvent = new SimulatorEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
    @Bean
    public void start() {
        Runnable runnableTask = () -> {
            publishCustomEvent("hello");
        };
        executorService.scheduleAtFixedRate(runnableTask, 1,2 ,TimeUnit.SECONDS);
    }

}
