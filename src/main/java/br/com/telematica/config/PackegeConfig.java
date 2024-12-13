package br.com.telematica.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.apis.abst.ApisControllerImpl;

@Configuration
public class PackegeConfig {
    
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.setConnectTimeout(Duration.ofSeconds(5)).setReadTimeout(Duration.ofSeconds(5)).build();
    }
    
    @Bean
    IApisController apisController(@Value("${api.sdk.senior}") String baseUrl, @Value("${partner_key}") String partner, @Value("${driver_key}") String driver) {
        return new ApisControllerImpl(baseUrl, partner, driver);
    }
    
}
