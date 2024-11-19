package br.com.telematica.seniorx.websocket;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.telematica.seniorx.websocket.manager.WebSocketManager;
import br.com.telematica.seniorx.websocket.model.WebSocketModelResponse;

@Configuration
public class WebsSocketMessage {

	private final WebSocketManager webSocketManager;

	public WebsSocketMessage(WebSocketManager webSocketManager) {
		this.webSocketManager = webSocketManager;
	}

	public void handleMessageWebSocket(String message, WebSocketModelResponse responseMessage) {
		try {
			WebSocketModelResponse mensagem = null;

			if (message != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(message);

				String driverId = jsonNode.get("driverId").asText();
				String deviceId = jsonNode.get("deviceId").asText();
				String pendencyType = jsonNode.get("pendencyType").asText();

				mensagem = new WebSocketModelResponse(driverId, deviceId, pendencyType);
				webSocketManager.handleMessageWebSocket(mensagem);
			} else if (responseMessage != null) {
				mensagem = new WebSocketModelResponse(responseMessage.getDriverId(), responseMessage.getDeviceId(),
						responseMessage.getPendencyType());
				webSocketManager.handleMessageWebSocket(mensagem);
			} else {
			}

		} catch (IOException e) {
		}
	}
}
