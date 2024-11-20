package br.com.telematica.seniorx.websocket.manager;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import br.com.telematica.seniorx.model.ActiveDeviceOutputPendency;
import br.com.telematica.seniorx.model.AllPendency;
import br.com.telematica.seniorx.model.BlockDevicePendency;
import br.com.telematica.seniorx.model.CollectEventPendency;
import br.com.telematica.seniorx.model.DatamartUpdatedPendency;
import br.com.telematica.seniorx.model.DeactiveDeviceOutputPendency;
import br.com.telematica.seniorx.model.DeviceDisplayMessagePendency;
import br.com.telematica.seniorx.model.DevicePendency;
import br.com.telematica.seniorx.model.ExcludePhotoPendency;
import br.com.telematica.seniorx.model.IncludePhotoPendency;
import br.com.telematica.seniorx.model.LoadHolidayListPendency;
import br.com.telematica.seniorx.model.OperationEnum;
import br.com.telematica.seniorx.model.PersonAreaUpdatedPendency;
import br.com.telematica.seniorx.model.UnblockDevicePendency;
import br.com.telematica.seniorx.service.SeniorXService;
import br.com.telematica.seniorx.websocket.model.MessageTypeWebSocketEnum;
import br.com.telematica.seniorx.websocket.model.WebSocketModelResponse;

@Service
public class WebSocketManager extends SeniorXService {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketManager.class);

	public WebSocketManager() {
		super();
		Thread.currentThread().setName("WebSocket Manager");
	}

	public void handleMessageWebSocket(WebSocketModelResponse message) {
		if (message == null || message.getPendencyType() == null) {
			logger.warn("Received null or invalid message.");
			return;
		}

		String pendencyType = message.getPendencyType();
		try {
			switch (MessageTypeWebSocketEnum.TypeWebSocketEnum.valueOf(pendencyType)) {
			case DEVICE_STATUS -> handleSpecificPendency(message, this::handleDeviceStatus);
			case DEVICE_DATE_TIME -> handleSpecificPendency(message, this::handleDeviceDateTime);
			case DEVICE -> handleSpecificPendency(message, this::handleDevice);
			case SET_DEVICE_EMERGENCY -> handleSpecificPendency(message, this::handleSetDeviceEmergency);
			case UNSET_DEVICE_EMERGENCY -> handleSpecificPendency(message, this::handleUnsetDeviceEmergency);
			case BLOCK_DEVICE -> handleSpecificPendency(message, this::handleBlockDevice);
			case UNBLOCK_DEVICE -> handleSpecificPendency(message, this::handleUnblockDevice);
			default -> logger.info("Unhandled pendency type: {}", pendencyType);
			}
		} catch (IllegalArgumentException e) {
			logger.warn("Invalid pendency type: {}", pendencyType);
		} catch (Exception e) {
			logger.error("Error handling message: {}", e.getMessage(), e);
		}
	}

	private void handleSpecificPendency(WebSocketModelResponse message, Consumer<WebSocketModelResponse> handler) {
		try {
			handler.accept(message);
		} catch (Exception e) {
			logger.error("Error processing pendency for type {}: {}", message.getPendencyType(), e.getMessage(), e);
		}
	}

	private void handleDeviceStatus(WebSocketModelResponse message) {
		logger.info("Handling Device Status: {}", message);
		// Add specific logic for Device Status here
	}

	private void handleDeviceDateTime(WebSocketModelResponse message) {
		logger.info("Handling Device DateTime: {}", message);
		// Add specific logic for Device DateTime here
	}

	private void handleDevice(WebSocketModelResponse message) {
		logger.info("Handling Device: {}", message);
		// Add specific logic for Device here
	}

	private void handleSetDeviceEmergency(WebSocketModelResponse message) {
		logger.info("Handling Set Device Emergency: {}", message);
		// Add specific logic for Set Device Emergency here
	}

	private void handleUnsetDeviceEmergency(WebSocketModelResponse message) {
		logger.info("Handling Unset Device Emergency: {}", message);
		// Add specific logic for Unset Device Emergency here
	}

	private void handleBlockDevice(WebSocketModelResponse message) {
		logger.info("Handling Block Device: {}", message);
		// Add specific logic for Block Device here
	}

	private void handleUnblockDevice(WebSocketModelResponse message) {
		logger.info("Handling Unblock Device: {}", message);
		// Add specific logic for Unblock Device here
	}

	public void removePendencyMessageWebSocket(WebSocketModelResponse message) {
		if (message == null || message.getPendencyType() == null) {
			logger.warn("Invalid WebSocket message: {}", message);
			return;
		}

		AllPendency allPendencyBody = new AllPendency();

		try {
			String pendencyType = message.getPendencyType();
			switch (pendencyType) {
			case "CREDENTIAL_FACIAL_LIST" ->
				processPendency(allPendencyBody.getLoadCredentialFacialList(), "CREDENTIAL_FACIAL_LIST");
			case "REMOVE_HOLIDAY_LIST" ->
				processPendency(allPendencyBody.getRemoveHolidayList(), "REMOVE_HOLIDAY_LIST");
			case "DEVICE_INPUT_STATUS" -> processPendency(allPendencyBody.getInputStatus(), "DEVICE_INPUT_STATUS");
			case "UPDATE_FIRMWARE" -> processPendency(allPendencyBody.getUpdateFirmware(), "UPDATE_FIRMWARE");
			case "LOAD_HOLIDAY_LIST" -> processPendency(allPendencyBody.getLoadHolidayList(), "LOAD_HOLIDAY_LIST");
			case "ACTIVATE_DEVICE_OUTPUT" ->
				processPendency(allPendencyBody.getActivateDeviceOutput(), "ACTIVATE_DEVICE_OUTPUT");
			case "COLLECT_EVENTS" -> processPendency(allPendencyBody.getCollectEvent(), "COLLECT_EVENTS");
			case "DEACTIVATE_DEVICE_OUTPUT" ->
				processPendency(allPendencyBody.getDeactivateDeviceOutput(), "DEACTIVATE_DEVICE_OUTPUT");
			case "PERSON_AREA_UPDATED" ->
				processPendency(allPendencyBody.getPersonLocationUpdated(), "PERSON_AREA_UPDATED");
			case "DATAMART_UPDATED" -> processPendency(allPendencyBody.getDatamartUpdated(), "DATAMART_UPDATED");
			case "BLOCK_DEVICE" -> processPendency(allPendencyBody.getBlockDevice(), "BLOCK_DEVICE");
			case "UNBLOCK_DEVICE" -> processPendency(allPendencyBody.getUnblockDevice(), "UNBLOCK_DEVICE");
			case "DEVICE_DISPLAY_MESSAGE" ->
				processPendency(allPendencyBody.getDeviceDisplayMessage(), "DEVICE_DISPLAY_MESSAGE");
			case "INCLUDE_PHOTO" -> processPendency(allPendencyBody.getIncludePhoto(), "INCLUDE_PHOTO");
			case "EXCLUDE_PHOTO" -> processPendency(allPendencyBody.getExcludePhoto(), "EXCLUDE_PHOTO");
			default -> logger.info("Unhandled pendency type: {}", pendencyType);
			}
		} catch (Exception e) {
			logger.error("Error processing pendency type: {}", message.getPendencyType(), e);
		}
	}

	private static <T> void processPendency(List<T> pendencies, String pendencyType) {
		if (pendencies == null || pendencies.isEmpty()) {
			logger.info("No pendencies found for type: {}", pendencyType);
			return;
		}

		logger.info("Processing {} pendencies for type: {}", pendencies.size(), pendencyType);

		for (T pendency : pendencies) {
			// Add logic for processing each pendency item if needed
			logger.debug("Processing pendency: {}", new Gson().toJson(pendency));
		}
	}

	public void handleDevicePendency(List<DevicePendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleDevicePendency", new Gson().toJson(message));

			List<DevicePendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (DevicePendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleDevicePendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleDevicePendency" + "Exception occurred: ", ex);
		}
	}

	public void handleBlockDevicePendency(List<BlockDevicePendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleBlockDevicePendency", new Gson().toJson(message));

			List<BlockDevicePendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (BlockDevicePendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleBlockDevicePendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleBlockDevicePendency" + "Exception occurred: ", ex);
		}
	}

	public void handleUnblockDevicePendency(List<UnblockDevicePendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleUnblockDevicePendency", new Gson().toJson(message));

			List<UnblockDevicePendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (UnblockDevicePendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleUnblockDevicePendency" + "The issue could not be found.");
			}
		} catch (Exception ex) {
			logger.debug("handleUnblockDevicePendency" + "Exception occurred: ", ex);
		}
	}

	public void handleDatamartUpdatedPendency(List<DatamartUpdatedPendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleDatamartUpdatedPendency", new Gson().toJson(message));

			List<DatamartUpdatedPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (DatamartUpdatedPendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleDatamartUpdatedPendency" + "The issue could not be found.");
			}
		} catch (Exception ex) {
			logger.debug("handleDatamartUpdatedPendency" + "Exception occurred: ", ex);
		}
	}

	public void handleLoadHolidayListPendency(List<LoadHolidayListPendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleLoadHolidayListPendency", new Gson().toJson(message));

			List<LoadHolidayListPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (LoadHolidayListPendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleLoadHolidayListPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleLoadHolidayListPendency" + "Exception occurred: ", ex);
		}
	}

	public void handleActiveDeviceOutputPendency(List<ActiveDeviceOutputPendency> message,
			AllPendency allPendencyBody) {
		try {
			logger.debug("handleActiveDeviceOutputPendency", new Gson().toJson(message));

			List<ActiveDeviceOutputPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (ActiveDeviceOutputPendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleActiveDeviceOutputPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleActiveDeviceOutputPendency" + "Exception occurred: ", ex);
		}
	}

	public void handleExcludePhotoPendency(List<ExcludePhotoPendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleExcludePhotoPendency", new Gson().toJson(message));

			List<ExcludePhotoPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (ExcludePhotoPendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleExcludePhotoPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleExcludePhotoPendency" + "Exception occurred: ", ex);
		}
	}

	public void handleIncludePhotoPendency(List<IncludePhotoPendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleIncludePhotoPendency", new Gson().toJson(message));

			List<IncludePhotoPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (IncludePhotoPendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleIncludePhotoPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleIncludePhotoPendency" + "Exception occurred: ", ex);
		}
	}

	public void handleCollectEventPendency(List<CollectEventPendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleCollectEventPendency", new Gson().toJson(message));

			List<CollectEventPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (CollectEventPendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleCollectEventPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleCollectEventPendency" + "Exception occurred: ", ex);
		}
	}

	public void handleDeviceDisplayMessagePendency(List<DeviceDisplayMessagePendency> message,
			AllPendency allPendencyBody) {
		try {
			logger.debug("handleDeviceDisplayMessagePendency", new Gson().toJson(message));

			List<DeviceDisplayMessagePendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (DeviceDisplayMessagePendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleDeviceDisplayMessagePendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleDeviceDisplayMessagePendencyy" + "Exception occurred: ", ex);
		}
	}

	public void handlePersonAreaUpdatedPendency(List<PersonAreaUpdatedPendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handlePersonAreaUpdatedPendency", new Gson().toJson(message));

			List<PersonAreaUpdatedPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (PersonAreaUpdatedPendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handlePersonAreaUpdatedPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handlePersonAreaUpdatedPendency" + "Exception occurred: ", ex);
		}
	}

	public void handleDeactiveDeviceOutputPendency(List<DeactiveDeviceOutputPendency> message,
			AllPendency allPendencyBody) {
		try {
			logger.debug("handleDeactiveDeviceOutputPendency", new Gson().toJson(message));

			List<DeactiveDeviceOutputPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (DeactiveDeviceOutputPendency devicePendencyItem : devicePendency) {
					updatePendency(devicePendencyItem.getPendencyId(), 345, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleDeactiveDeviceOutputPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleDeactiveDeviceOutputPendency" + "Exception occurred: ", ex);
		}
	}
}

//public void handleMessageWebSocket(WebSocketModelResponse message) {
//
//	if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.DEVICE_STATUS.toString()))
//		// seniorHandler.handleDeviceStatus(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.DEVICE_DATE_TIME.toString()))
//		// seniorHandler.handleDeviceDateTime(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.DEVICE.toString()))
//		// seniorHandler.handleDevice(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.SET_DEVICE_EMERGENCY.toString()))
//		// seniorHandler.handleSetDeviceEmergency(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.UNSET_DEVICE_EMERGENCY.toString()))
//		// seniorHandler.handleUnsetDeviceEmergency(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.REP_MANUFACTURER_UPDATED.toString()))
//		// seniorHandler.handleManufatureRep(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.ALLOW_CARD_LIST.toString()))
//		// seniorHandler.handleAllowCardList(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.REMOVE_ALLOW_CARD_LIST.toString()))
//		// seniorHandler.handleRemoveAllowCardList(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.BIOMETRY_LIST.toString()))
//		// seniorHandler.handleAllowBiometryList(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.REMOVE_BIOMETRY_LIST.toString()))
//		// seniorHandler.handleAllowBiometryList(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.INCLUDE_BIOMETRY.toString()))
//		// seniorHandler.handleIncludeBiometry(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.EXCLUDE_BIOMETRY.toString()))
//		// seniorHandler.handleExcludeBiometry(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.INCLUDE_CARD.toString()))
//		// seniorHandler.handleIncludeCard(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.EXCLUDE_CARD.toString()))
//		// seniorHandler.handleExcludeCard(message);
//		return;
//	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.UPDATE_PERSON_REP.toString()))
//		// seniorHandler.handleUpdateRepPerson(message);
////	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.CREDENTIAL_FACIAL_LIST.toString()))
////		// seniorHandler.handleCredentialFacialList(message);
////	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.UPDATE_FIRMWARE.toString()))
////		// seniorHandler.handleUpdateFirmware(message);
////	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.LOAD_HOLIDAY_LIST.toString()))
////		// seniorHandler.handleLoadHolidayList(message);
////	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.REMOVE_HOLIDAY_LIST.toString()))
////		// seniorHandler.handleRemoveHolidayList(message);
////	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.DEVICE_INPUT_STATUS.toString()))
////		// seniorHandler.handleDeviceInputStatus(message);
////	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.BLOCK_DEVICE.toString()))
////		// seniorHandler.handleBlockDevice(message);
////	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.UNBLOCK_DEVICE.toString()))
////		// seniorHandler.handleUnblockDevice(message);
////	else if ((message.getPendencyType()).equals(MessageTypeWebSocketEnum.TypeWebSocketEnum.COLLECT_EVENTS.toString()))
////		// seniorHandler.handleCollectEvents(message);
//		return;
//	else {
//		removePendencyMessageWebSocket(message);
//	}
//
//}
