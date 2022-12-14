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

import java.util.concurrent.*;

@Component
public class Simulator {

    private final ScheduledExecutorService executorService;

    public Simulator() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private void publishCustomEvent(final SensorSim message) {
        System.out.println("Publishing custom event. ");
        SimulatorEvent customSpringEvent = new SimulatorEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }

    public void start() {
        Runnable runnableTask = () -> {
            // bdd call to get raw data about a sensor
            System.out.println("hello");
            var sensorFromBdd = new SensorSim("M_Table-Dining Round w Chairs:0915mm Diameter:165477", SensorType.TEMPERATURE,1608208221, 20);
            publishCustomEvent(sensorFromBdd);
        };
        executorService.scheduleAtFixedRate(runnableTask, 1,2 ,TimeUnit.SECONDS);
    }

    public void stop() {
        executorService.shutdown();
    }

}
