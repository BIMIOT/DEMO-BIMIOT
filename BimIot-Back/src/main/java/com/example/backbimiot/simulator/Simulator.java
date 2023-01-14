package com.example.backbimiot.simulator;

import com.example.backbimiot.simulator.simpojo.SensorSim;
import com.example.backbimiot.simulator.simpojo.SensorType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.*;

@Component
public class Simulator {

    private ScheduledExecutorService executorService;

    public Simulator() {

    }

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private void publishCustomEvent(final SensorSim message) {
        System.out.println("Publishing custom event. ");
        SimulatorEvent customSpringEvent = new SimulatorEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }

    public void start() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable runnableTask = () -> {
            // bdd call to get raw data about a sensor
            System.out.println("hello");
            SensorSim sensorFromBdd;
            var rand = new Random().nextInt(1,3);
            if (rand == 1) {
                sensorFromBdd = new SensorSim("sensor:sensor:143759", SensorType.TEMPERATURE,1608208221, new Random().nextInt(19,21));
            } else {
                sensorFromBdd = new SensorSim("sensor4:sensor:146504", SensorType.TEMPERATURE,1608208221, new Random().nextInt(19,21));
            }
            publishCustomEvent(sensorFromBdd);
        };
        executorService.scheduleAtFixedRate(runnableTask, 1,2 ,TimeUnit.SECONDS);
    }

    public void stop() {
        executorService.shutdown();
    }

}
