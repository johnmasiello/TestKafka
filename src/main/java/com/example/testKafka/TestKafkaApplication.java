package com.example.testKafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class TestKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestKafkaApplication.class, args);
	}

}
