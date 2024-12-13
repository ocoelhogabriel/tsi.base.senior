package br.com.telematica.seniorx.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.telematica.websocker.AbstractWebSocketClient;

@Component
public class WebSocketClient extends AbstractWebSocketClient {
    
    @Value("${websocket.uri}")
    private String URIWS;
    
    private final WebsSocketMessage webSocketMessage;
    
    public WebSocketClient(WebsSocketMessage webSocketMessage) {
        super();
        this.webSocketMessage = webSocketMessage;
    }
    
    @Override
    protected String getWebSocketUri() {
        return URIWS;
    }
    
    @Override
    protected void handleMessage(String message) {
        webSocketMessage.handleMessageWebSocket(message, null);
    }
    
}
