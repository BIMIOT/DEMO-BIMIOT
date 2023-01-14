package com.example.backbimiot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableScheduling
public class BackBimiotApplication {

    public static void main(String[] args) {


        SpringApplication.run(BackBimiotApplication.class, args);
    }

}
