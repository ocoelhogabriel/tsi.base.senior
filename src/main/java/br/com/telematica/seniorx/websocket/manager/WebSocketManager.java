package br.com.telematica.seniorx.websocket.manager;

import java.util.List;

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
import br.com.telematica.seniorx.websocket.model.TypeWebSocketEnum;
import br.com.telematica.seniorx.websocket.model.WebSocketModelResponse;

@Service
public class WebSocketManager {
	private static final Logger logger = LoggerFactory.getLogger(WebSocketManager.class);

	public WebSocketManager() {
		super();
		Thread.currentThread().setName("WebSocket Manager");
	}

	public void handleMessageWebSocket(WebSocketModelResponse message) {

		if ((message.getPendencyType()).equals(TypeWebSocketEnum.DEVICE_STATUS.toString()))
			// seniorHandler.handleDeviceStatus(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.DEVICE_DATE_TIME.toString()))
			// seniorHandler.handleDeviceDateTime(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.DEVICE.toString()))
			// seniorHandler.handleDevice(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.SET_DEVICE_EMERGENCY.toString()))
			// seniorHandler.handleSetDeviceEmergency(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.UNSET_DEVICE_EMERGENCY.toString()))
			// seniorHandler.handleUnsetDeviceEmergency(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.REP_MANUFACTURER_UPDATED.toString()))
			// seniorHandler.handleManufatureRep(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.ALLOW_CARD_LIST.toString()))
			// seniorHandler.handleAllowCardList(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.REMOVE_ALLOW_CARD_LIST.toString()))
			// seniorHandler.handleRemoveAllowCardList(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.BIOMETRY_LIST.toString()))
			// seniorHandler.handleAllowBiometryList(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.REMOVE_BIOMETRY_LIST.toString()))
			// seniorHandler.handleAllowBiometryList(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.INCLUDE_BIOMETRY.toString()))
			// seniorHandler.handleIncludeBiometry(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.EXCLUDE_BIOMETRY.toString()))
			// seniorHandler.handleExcludeBiometry(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.INCLUDE_CARD.toString()))
			// seniorHandler.handleIncludeCard(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.EXCLUDE_CARD.toString()))
			// seniorHandler.handleExcludeCard(message);
			return;
		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.UPDATE_PERSON_REP.toString()))
			// seniorHandler.handleUpdateRepPerson(message);
//		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.CREDENTIAL_FACIAL_LIST.toString()))
//			// seniorHandler.handleCredentialFacialList(message);
//		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.UPDATE_FIRMWARE.toString()))
//			// seniorHandler.handleUpdateFirmware(message);
//		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.LOAD_HOLIDAY_LIST.toString()))
//			// seniorHandler.handleLoadHolidayList(message);
//		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.REMOVE_HOLIDAY_LIST.toString()))
//			// seniorHandler.handleRemoveHolidayList(message);
//		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.DEVICE_INPUT_STATUS.toString()))
//			// seniorHandler.handleDeviceInputStatus(message);
//		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.BLOCK_DEVICE.toString()))
//			// seniorHandler.handleBlockDevice(message);
//		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.UNBLOCK_DEVICE.toString()))
//			// seniorHandler.handleUnblockDevice(message);
//		else if ((message.getPendencyType()).equals(TypeWebSocketEnum.COLLECT_EVENTS.toString()))
//			// seniorHandler.handleCollectEvents(message);
			return;
		else {
			removePendencyMessageWebSocket(message);
		}

	}

	public static void removePendencyMessageWebSocket(WebSocketModelResponse message) {
		AllPendency allPendencyBody = new AllPendency();
		try {

			switch (message.getPendencyType()) {
			case "CREDENTIAL_FACIAL_LIST":
				List<DevicePendency> pendencyFacialList = allPendencyBody.getLoadCredentialFacialList();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleDevicePendency(pendencyFacialList, allPendencyBody);
				break;
			case "REMOVE_HOLIDAY_LIST":
				List<DevicePendency> pendencyRemoveHolidayList = allPendencyBody.getRemoveHolidayList();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleDevicePendency(pendencyRemoveHolidayList, allPendencyBody);
				break;
			case "DEVICE_INPUT_STATUS":
				List<DevicePendency> pendencyInputStatus = allPendencyBody.getInputStatus();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleDevicePendency(pendencyInputStatus, allPendencyBody);
				break;
			case "UPDATE_FIRMWARE":
				List<DevicePendency> pendencyUpdateFirmware = allPendencyBody.getUpdateFirmware();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleDevicePendency(pendencyUpdateFirmware, allPendencyBody);
				break;
			case "LOAD_HOLIDAY_LIST":
				List<LoadHolidayListPendency> pendencyHolidayList = allPendencyBody.getLoadHolidayList();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleLoadHolidayListPendency(pendencyHolidayList, allPendencyBody);
				break;
			case "ACTIVATE_DEVICE_OUTPUT":
				List<ActiveDeviceOutputPendency> pendencyActivateDeviceOutput = allPendencyBody
						.getActivateDeviceOutput();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleActiveDeviceOutputPendency(pendencyActivateDeviceOutput, allPendencyBody);
				break;
			case "COLLECT_EVENTS":
				List<CollectEventPendency> pendencyCollectEvent = allPendencyBody.getCollectEvent();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleCollectEventPendency(pendencyCollectEvent, allPendencyBody);
				break;
			case "DEACTIVATE_DEVICE_OUTPUT":
				List<DeactiveDeviceOutputPendency> pendencyDesactiveDeviceOutput = allPendencyBody
						.getDeactivateDeviceOutput();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleDeactiveDeviceOutputPendency(pendencyDesactiveDeviceOutput, allPendencyBody);
				break;
			case "PERSON_AREA_UPDATED":
				List<PersonAreaUpdatedPendency> pendencyPersonAreaUpdate = allPendencyBody.getPersonLocationUpdated();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handlePersonAreaUpdatedPendency(pendencyPersonAreaUpdate, allPendencyBody);
				break;
			case "DATAMART_UPDATED":
				List<DatamartUpdatedPendency> pendencyDatamartUpdate = allPendencyBody.getDatamartUpdated();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleDatamartUpdatedPendency(pendencyDatamartUpdate, allPendencyBody);
				break;
			case "BLOCK_DEVICE":
				List<BlockDevicePendency> pendencyRBlockDevice = allPendencyBody.getBlockDevice();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleBlockDevicePendency(pendencyRBlockDevice, allPendencyBody);
				break;
			case "UNBLOCK_DEVICE":
				List<UnblockDevicePendency> pendencyUnblockDevice = allPendencyBody.getUnblockDevice();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleUnblockDevicePendency(pendencyUnblockDevice, allPendencyBody);
				break;
			case "DEVICE_DISPLAY_MESSAGE":
				List<DeviceDisplayMessagePendency> pendencyDeviceDisplayMessage = allPendencyBody
						.getDeviceDisplayMessage();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleDeviceDisplayMessagePendency(pendencyDeviceDisplayMessage, allPendencyBody);
				break;
			case "INCLUDE_PHOTO":
				List<IncludePhotoPendency> pendencyIncludePhoto = allPendencyBody.getIncludePhoto();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleIncludePhotoPendency(pendencyIncludePhoto, allPendencyBody);
				break;
			case "EXCLUDE_PHOTO":
				List<ExcludePhotoPendency> pendencyExcludePhoto = allPendencyBody.getExcludePhoto();
				logger.info("Remove Pendency Message WebSocket" + "Webhook Message type not implement "
						+ message.getPendencyType());
				handleExcludePhotoPendency(pendencyExcludePhoto, allPendencyBody);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void handleDevicePendency(List<DevicePendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleDevicePendency", new Gson().toJson(message));

			List<DevicePendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (DevicePendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleDevicePendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleDevicePendency" + "Exception occurred: ", ex);
		}
	}

	public static void handleBlockDevicePendency(List<BlockDevicePendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleBlockDevicePendency", new Gson().toJson(message));

			List<BlockDevicePendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (BlockDevicePendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleBlockDevicePendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleBlockDevicePendency" + "Exception occurred: ", ex);
		}
	}

	public static void handleUnblockDevicePendency(List<UnblockDevicePendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleUnblockDevicePendency", new Gson().toJson(message));

			List<UnblockDevicePendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (UnblockDevicePendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleUnblockDevicePendency" + "The issue could not be found.");
			}
		} catch (Exception ex) {
			logger.debug("handleUnblockDevicePendency" + "Exception occurred: ", ex);
		}
	}

	public static void handleDatamartUpdatedPendency(List<DatamartUpdatedPendency> message,
			AllPendency allPendencyBody) {
		try {
			logger.debug("handleDatamartUpdatedPendency", new Gson().toJson(message));

			List<DatamartUpdatedPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (DatamartUpdatedPendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleDatamartUpdatedPendency" + "The issue could not be found.");
			}
		} catch (Exception ex) {
			logger.debug("handleDatamartUpdatedPendency" + "Exception occurred: ", ex);
		}
	}

	public static void handleLoadHolidayListPendency(List<LoadHolidayListPendency> message,
			AllPendency allPendencyBody) {
		try {
			logger.debug("handleLoadHolidayListPendency", new Gson().toJson(message));

			List<LoadHolidayListPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (LoadHolidayListPendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleLoadHolidayListPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleLoadHolidayListPendency" + "Exception occurred: ", ex);
		}
	}

	public static void handleActiveDeviceOutputPendency(List<ActiveDeviceOutputPendency> message,
			AllPendency allPendencyBody) {
		try {
			logger.debug("handleActiveDeviceOutputPendency", new Gson().toJson(message));

			List<ActiveDeviceOutputPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (ActiveDeviceOutputPendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleActiveDeviceOutputPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleActiveDeviceOutputPendency" + "Exception occurred: ", ex);
		}
	}

	public static void handleExcludePhotoPendency(List<ExcludePhotoPendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleExcludePhotoPendency", new Gson().toJson(message));

			List<ExcludePhotoPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (ExcludePhotoPendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleExcludePhotoPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleExcludePhotoPendency" + "Exception occurred: ", ex);
		}
	}

	public static void handleIncludePhotoPendency(List<IncludePhotoPendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleIncludePhotoPendency", new Gson().toJson(message));

			List<IncludePhotoPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (IncludePhotoPendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleIncludePhotoPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleIncludePhotoPendency" + "Exception occurred: ", ex);
		}
	}

	public static void handleCollectEventPendency(List<CollectEventPendency> message, AllPendency allPendencyBody) {
		try {
			logger.debug("handleCollectEventPendency", new Gson().toJson(message));

			List<CollectEventPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (CollectEventPendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleCollectEventPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleCollectEventPendency" + "Exception occurred: ", ex);
		}
	}

	public static void handleDeviceDisplayMessagePendency(List<DeviceDisplayMessagePendency> message,
			AllPendency allPendencyBody) {
		try {
			logger.debug("handleDeviceDisplayMessagePendency", new Gson().toJson(message));

			List<DeviceDisplayMessagePendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (DeviceDisplayMessagePendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleDeviceDisplayMessagePendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleDeviceDisplayMessagePendencyy" + "Exception occurred: ", ex);
		}
	}

	public static void handlePersonAreaUpdatedPendency(List<PersonAreaUpdatedPendency> message,
			AllPendency allPendencyBody) {
		try {
			logger.debug("handlePersonAreaUpdatedPendency", new Gson().toJson(message));

			List<PersonAreaUpdatedPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (PersonAreaUpdatedPendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handlePersonAreaUpdatedPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handlePersonAreaUpdatedPendency" + "Exception occurred: ", ex);
		}
	}

	public static void handleDeactiveDeviceOutputPendency(List<DeactiveDeviceOutputPendency> message,
			AllPendency allPendencyBody) {
		try {
			logger.debug("handleDeactiveDeviceOutputPendency", new Gson().toJson(message));

			List<DeactiveDeviceOutputPendency> devicePendency = message;
			if (!devicePendency.isEmpty()) {
				for (DeactiveDeviceOutputPendency devicePendencyItem : devicePendency) {
					// seniorHandler.sendPendencyUpdate(devicePendencyItem.getPendencyId(), 345,
					// OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.debug("handleDeactiveDeviceOutputPendency" + "The issue could not be found.");
			}

		} catch (Exception ex) {
			logger.debug("handleDeactiveDeviceOutputPendency" + "Exception occurred: ", ex);
		}
	}
}
