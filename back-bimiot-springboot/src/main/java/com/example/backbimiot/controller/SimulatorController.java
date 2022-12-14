package com.example.backbimiot.controller;

import com.example.backbimiot.simulator.Simulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class SimulatorController {
    private Simulator simulator;

    @Autowired
    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }
    public void start() {
        if(simulator == null) {
            System.out.println("its null");
            return;
        }
        simulator.start();
    }


    public void stop() {
        if(simulator == null) {
            System.out.println("its null");
            return;
        }
        simulator.stop();
    }
}
