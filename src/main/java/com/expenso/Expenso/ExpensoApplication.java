package com.expenso.Expenso;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class ExpensoApplication {
	@PostConstruct
	public void printRedisHost() {
		System.out.println("Redis Host: " + System.getenv("SPRING_REDIS_HOST"));
		System.out.println("Current Server Time: " + LocalDateTime.now());
	}
	public static void main(String[] args) {
		SpringApplication.run(ExpensoApplication.class, args);
	}

}
