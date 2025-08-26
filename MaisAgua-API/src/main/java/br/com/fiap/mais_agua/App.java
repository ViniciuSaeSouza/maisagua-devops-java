package br.com.fiap.mais_agua;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
		info = @Info(
				title = "+Água API - Sistema de Monitoramento Inteligente",
				version = "v1",
				description = "API REST para gerenciamento e monitoramento inteligente de reservatórios de água com foco em eventos extremos como secas e enchentes"
		)
)
@EnableScheduling
@SpringBootApplication
@EnableCaching
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
