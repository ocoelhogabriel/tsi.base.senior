package br.com.telematica.seniorx.websocket;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.telematica.seniorx.websocket.manager.WebSocketManager;
import br.com.telematica.seniorx.websocket.model.WebSocketModelResponse;

@Configuration
public class WebsSocketMessage {
    
    private static final Logger logger = LoggerFactory.getLogger(WebsSocketMessage.class);
    
    private final WebSocketManager webSocketManager;
    
    private final ObjectMapper objectMapper;
    
    public WebsSocketMessage(WebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;
        this.objectMapper = new ObjectMapper();
    }
    
    public void handleMessageWebSocket(String message, WebSocketModelResponse responseMessage) {
        try {
            WebSocketModelResponse mensagem = parseWebSocketMessage(message, responseMessage);
            
            if (mensagem != null) {
                webSocketManager.handleMessageWebSocket(mensagem);
            } else {
                logger.warn("No valid message or responseMessage provided.");
            }
            
        } catch (IOException e) {
            logger.error("Error parsing WebSocket message: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error handling WebSocket message: {}", e.getMessage(), e);
        }
    }
    
    private WebSocketModelResponse parseWebSocketMessage(String message, WebSocketModelResponse responseMessage) throws IOException {
        if (message != null) {
            JsonNode jsonNode = objectMapper.readTree(message);
            
            String driverId = jsonNode.get("driverId").asText(null);
            String deviceId = jsonNode.get("deviceId").asText(null);
            String pendencyType = jsonNode.get("pendencyType").asText(null);
            
            if (driverId != null && deviceId != null && pendencyType != null) {
                return new WebSocketModelResponse(driverId, deviceId, pendencyType);
            } else {
                logger.warn("Missing fields in WebSocket message: {}", jsonNode);
                return null;
            }
        } else if (responseMessage != null) {
            return new WebSocketModelResponse(responseMessage.getDriverId(), responseMessage.getDeviceId(), responseMessage.getPendencyType());
        }
        
        return null;
    }
    
}
