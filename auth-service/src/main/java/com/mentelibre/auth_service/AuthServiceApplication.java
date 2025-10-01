package com.mentelibre.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
		// http://localhost:8081/swagger-ui/index.html 
        // Consola de Swagger para probar los endpoints
	}

}
