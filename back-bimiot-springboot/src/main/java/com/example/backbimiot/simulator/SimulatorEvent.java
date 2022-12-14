package com.example.backbimiot.simulator;

import com.example.backbimiot.simulator.simpojo.SensorSim;
import org.springframework.context.ApplicationEvent;

public class SimulatorEvent extends ApplicationEvent {
        private SensorSim message;

        public SimulatorEvent(Object source, SensorSim message) {
            super(source);
            this.message = message;
        }
        public SensorSim getMessage() {
            return message;
        }
    }