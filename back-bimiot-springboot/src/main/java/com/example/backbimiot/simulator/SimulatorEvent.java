package com.example.backbimiot.simulator;

import org.springframework.context.ApplicationEvent;

public class SimulatorEvent extends ApplicationEvent {
        private String message;

        public SimulatorEvent(Object source, String message) {
            super(source);
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }