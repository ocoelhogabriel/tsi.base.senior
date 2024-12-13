package br.com.telematica.seniorx.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.Driver;

@Configuration
public class WebSocketPoolingHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketPoolingHandler.class);
    
    private final IApisController iApisController;
    
    public WebSocketPoolingHandler(IApisController iApisController) {
        this.iApisController = iApisController;
        Thread.currentThread().setName("WebSocket Pooling Handler");
    }
    
    public String getDriverKey() {
        try {
            Driver driver = iApisController.getDriver().getBody();
            
            if (driver != null && driver.getId() != null) {
                return driver.getId().toString();
            } else {
                logger.warn("Driver object or ID is null. Returning default value.");
                return "0";
            }
            
        } catch (Exception e) {
            logger.error("Error fetching Driver from API: {}", e.getMessage(), e);
            return "0";
        }
    }
    
}
