package br.com.telematica.seniorx.service.keepalive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.telematica.seniorx.model.devices.DeviceController;

@Component
public class DevicesInitializer implements CommandLineRunner {

	private final DevicesKeepAliveServices devicesKeepAliveServices;
	private final DeviceController deviceController;

	public DevicesInitializer(DevicesKeepAliveServices devicesKeepAliveServices, DeviceController deviceController) {
		this.devicesKeepAliveServices = devicesKeepAliveServices;
		this.deviceController = deviceController;
	}

	@Override
	public void run(String... args) {
		
		devicesKeepAliveServices.initializeKeepAlive(deviceController.getAllDevices());
	}
}
