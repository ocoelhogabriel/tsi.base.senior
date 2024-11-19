package br.com.telematica.websocker;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

@ClientEndpoint
public abstract class AbstractWebSocketClient {

	@Value("${reconnectIntervalWebSocket}")
	private Integer reconect;

	private Session session;
	private volatile boolean connected = false;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private final int RECONNECT_DELAY_SECONDS = reconect == null ? 5 : reconect;
	private static final Logger logger = LoggerFactory.getLogger(AbstractWebSocketClient.class);

	protected AbstractWebSocketClient() {
	}

	protected abstract String getWebSocketUri();

	protected abstract void handleMessage(String message);

	public void connect() {
		scheduler.execute(() -> {
			try {
				WebSocketContainer container = ContainerProvider.getWebSocketContainer();
				session = container.connectToServer(this, new URI(getWebSocketUri()));
			} catch (Exception e) {
				scheduleReconnect();
			}
		});
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		setConnected(true);
		logger.info("Connected to WebSocket: " + getWebSocketUri());
	}

	@OnMessage
	public void onMessage(String message) {
		handleMessage(message);
	}

	@OnClose
	public void onClose() {
		setConnected(false);
		logger.info("Disconnected from WebSocket: " + getWebSocketUri());
		scheduleReconnect();
	}

	private synchronized void setConnected(boolean connected) {
		this.connected = connected;
	}

	synchronized boolean isConnected() {
		return connected;
	}

	private void scheduleReconnect() {
		if (!isConnected()) {
			scheduler.schedule(() -> {
				System.out.println("Reconnecting to WebSocket: " + getWebSocketUri());
				connect();
			}, RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
		}
	}

	public void sendMessage(String message) {
		if (session != null && session.isOpen()) {
			try {
				session.getAsyncRemote().sendText(message);
			} catch (Exception e) {
				logger.error("Failed to send message: " + e.getMessage());
			}
		} else {
			logger.error("Cannot send message. WebSocket is not connected.");
		}
	}

	public void close() {
		try {
			if (session != null) {
				session.close();
			}
			scheduler.shutdown();
		} catch (Exception e) {
			logger.error("Error while closing WebSocket: " + e.getMessage());
		}
	}
}
