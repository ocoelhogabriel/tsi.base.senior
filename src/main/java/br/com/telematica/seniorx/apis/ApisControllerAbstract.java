package br.com.telematica.seniorx.apis;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public abstract class ApisControllerAbstract implements IApisController {
    
    protected final String partner;
    
    protected final String driver;
    
    protected final String baseUrl;
    
    protected ApisControllerAbstract(String baseUrl, String partner, String driver) {
        this.partner = partner;
        this.driver = driver;
        this.baseUrl = baseUrl;
    }
    
    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("partner_key", partner);
        headers.add("driver_key", driver);
        return headers;
    }
    
    protected RestTemplate rest() {
        return new RestTemplate();
    }
    
}
