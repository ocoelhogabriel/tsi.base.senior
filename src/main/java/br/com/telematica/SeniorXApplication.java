package br.com.telematica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "br.com.telematica.seniorx")
public class SeniorXApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeniorXApplication.class, args);
    }
    
}
