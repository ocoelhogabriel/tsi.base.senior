package br.com.telematica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "br.com.telematica.seniorx")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
