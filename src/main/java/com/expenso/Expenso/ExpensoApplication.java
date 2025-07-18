package com.expenso.Expenso;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExpensoApplication {
	@PostConstruct
	public void printRedisHost() {
		System.out.println("Redis Host: " + System.getenv("SPRING_REDIS_HOST"));
	}
	public static void main(String[] args) {
		SpringApplication.run(ExpensoApplication.class, args);
	}

}
