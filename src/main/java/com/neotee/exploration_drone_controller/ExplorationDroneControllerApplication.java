package com.neotee.exploration_drone_controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"anticorruption","certification"})
public class ExplorationDroneControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExplorationDroneControllerApplication.class, args);
	}

}
