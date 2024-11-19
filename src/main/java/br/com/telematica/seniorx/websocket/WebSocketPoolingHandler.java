package br.com.telematica.seniorx.websocket;

import org.springframework.context.annotation.Configuration;

import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.Driver;

@Configuration
public class WebSocketPoolingHandler {

	private final IApisController iApisController;

	public WebSocketPoolingHandler(IApisController iApisController) {
		super();
		this.iApisController = iApisController;
		Thread.currentThread().setName("WebSocket Pooling Handler");
	}

	public String getDriverKey() {
		Driver driver = iApisController.getDriver().getBody();
		String idDriver = null;

		if (driver != null) {
			idDriver = driver.getId().toString();
		} else {
			idDriver = "0";
		}

		return idDriver;
	}

}
