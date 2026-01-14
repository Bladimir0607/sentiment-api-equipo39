package com.hackaton.sentiment;

import org.springframework.boot.SpringApplication;

public class TestSentimentBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(SentimentBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
