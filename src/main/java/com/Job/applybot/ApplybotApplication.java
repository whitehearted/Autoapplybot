package com.Job.applybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ApplybotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplybotApplication.class, args);
	}

}
