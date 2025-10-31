package com.expenso.Expenso;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(
  info = @Info(
    title = "Expenso API Documentation",
    version = "1.0",
    description = "Hybrid Personal and Group Finance Management API — including authentication, user profile, transactions, categories, and wallet modules.",
    contact = @Contact(
      name = "Expenso Developer Team",
      email = "rishiraghuvanshi08@gmail.com"
    )
  )
)
public class ExpensoApplication {

  /**
   * Prints Redis host and current time for debugging when the application starts.
   */
	@PostConstruct
	public void printRedisHost() {
		System.out.println("Redis Host: " + System.getenv("SPRING_REDIS_HOST"));
		System.out.println("Current Server Time: " + LocalDateTime.now());
	}

  /**
   * Main method to run the Spring Boot application.
   *
   * @param args command-line arguments
   */
	public static void main(String[] args) {
		SpringApplication.run(ExpensoApplication.class, args);
	}

}
