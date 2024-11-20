package br.com.telematica.seniorx.service;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.OperationEnum;
import br.com.telematica.seniorx.model.PendencyExecuted;
import br.com.telematica.seniorx.model.PendencyUpdated;
import br.com.telematica.seniorx.model.devices.DeviceController;

@Component
public abstract class SeniorXService {

	protected static final Logger logger = LoggerFactory.getLogger(SeniorXService.class);

	@Autowired
	protected DeviceController deviceController;
	@Autowired
	protected IApisController iApisController;

	protected SeniorXService() {
	}

	protected <T> void handlePendency(List<T> pendencies, Consumer<T> action, String logMessage) {
		if (pendencies == null || pendencies.isEmpty()) {
			logger.warn("{} - Pendency list is empty!", logMessage);
			return;
		}

		for (T pendency : pendencies) {
			try {
				action.accept(pendency);
			} catch (Exception e) {
				logger.error("{} - Error processing pendency: {}", logMessage, e.getMessage(), e);
			}
		}
	}

	protected void updatePendency(Long pendencyId, Integer errorCode, OperationEnum operation) {
		try {
			PendencyUpdated update = new PendencyUpdated();
			update.setPendencyId(pendencyId);
			update.setErrorCode(errorCode);
			update.setOperation(operation);
			ResponseEntity<Object> response = iApisController.updatePendency(Collections.singletonList(update));
			logger.info("Pendency update success: {}", response.getStatusCode());
		} catch (Exception e) {
			logger.error("Error updating pendency: {}", e.getMessage(), e);
		}
	}

	protected void successPendency(Long pendencyId) {
		try {
			PendencyExecuted success = new PendencyExecuted();
			success.setPendencyId(pendencyId);
			ResponseEntity<Object> response = iApisController.successPendency(success);
			logger.info("Pendency success: {}", response.getStatusCode());
		} catch (Exception e) {
			logger.error("Error marking pendency as successful: {}", e.getMessage(), e);
		}
	}
}
