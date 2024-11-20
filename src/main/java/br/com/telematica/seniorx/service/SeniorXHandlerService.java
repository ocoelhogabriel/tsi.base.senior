package br.com.telematica.seniorx.service;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.AllPendency;
import br.com.telematica.seniorx.model.AreaControlList;
import br.com.telematica.seniorx.model.DevicePendency;
import br.com.telematica.seniorx.model.DeviceUpdatedPendency;
import br.com.telematica.seniorx.model.DriverDateTime;
import br.com.telematica.seniorx.model.ExcludeBiometryPendency;
import br.com.telematica.seniorx.model.ExcludeCardPendency;
import br.com.telematica.seniorx.model.IncludeBiometryPendency;
import br.com.telematica.seniorx.model.IncludeCardPendency;
import br.com.telematica.seniorx.model.ManagerDevice;
import br.com.telematica.seniorx.model.ManufacturerUpdatedPendency;
import br.com.telematica.seniorx.model.OperationEnum;
import br.com.telematica.seniorx.model.SetDeviceEmergencyPendency;
import br.com.telematica.seniorx.model.UnsetDeviceEmergencyPendency;
import br.com.telematica.seniorx.model.UpdatePersonREPPendency;
import br.com.telematica.seniorx.model.devices.DeviceController;
import br.com.telematica.seniorx.model.devices.DevicesCollection;
import br.com.telematica.seniorx.websocket.model.WebSocketModelResponse;
import br.com.telematica.util.Utils;
import jakarta.annotation.PostConstruct;

public class SeniorXHandlerService extends SeniorXService {

	private static final Logger logger = LoggerFactory.getLogger(SeniorXHandlerService.class);

	public SeniorXHandlerService(IApisController iApisController, DeviceController deviceController) {
		super(iApisController, deviceController);
	}

	@PostConstruct
	public void initialize() {
		sendAllDevice();
	}

	public void sendAllDevice() {

		try {

			List<ManagerDevice> managerDevice = iApisController.getDevice().getBody();
			List<AreaControlList> areaControlResponse = iApisController.getAreaControl().getBody();

			if ((managerDevice != null && !managerDevice.isEmpty())
					|| (areaControlResponse != null && !areaControlResponse.isEmpty())) {
				deviceController.onAreaUpdate(areaControlResponse);
				deviceController.onDeviceUpdateManagerDevice(managerDevice);

			} else {
				logger.info("Send All Device - urlDevice and urlDataMartAreaControl are null.");
			}
		} catch (Exception e) {
			logger.error("Send All Device - Senior - Error when executing the mapping of registered equipment", e);
		}
	}

	public void handleDeviceDateTime(WebSocketModelResponse message) {
		logger.info("Handle Device DateTime - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			logger.info("Device Address: {}", device.getNetworkIdentification());

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<DevicePendency> dateTimePendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getDeviceDateTime).orElse(Collections.emptyList());

			handlePendency(dateTimePendencies, pendency -> processDateTimePendency(device, pendency),
					"Handle Device DateTime");

		} catch (Exception e) {
			logger.error("Handle Device DateTime - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processDateTimePendency(DevicesCollection device, DevicePendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		try {
			DriverDateTime dateTime = iApisController.getDriverDateTime().getBody();
			if (dateTime == null) {
				updatePendency(pendency.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
				return;
			}

			String adjustedDateTime = Utils.addGmtToDateTime(dateTime.getDateTime(), device.getAreaControl().getGmt());
			logger.info("Adjusted DateTime: {}", adjustedDateTime);
			successPendency(pendency.getPendencyId());

		} catch (DateTimeParseException e) {
			updatePendency(pendency.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
			logger.error("DateTime parsing error: {}", e.getMessage(), e);
		}
	}

	public void handleDeviceStatus(WebSocketModelResponse message) {
		logger.info("Handle Device Status - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<DevicePendency> statusPendencies = Optional.ofNullable(pendencies).map(AllPendency::getDeviceStatus)
					.orElse(Collections.emptyList());

			handlePendency(statusPendencies, pendency -> processDeviceStatus(device, pendency), "Handle Device Status");
		} catch (Exception e) {
			logger.error("Handle Device Status - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processDeviceStatus(DevicesCollection device, DevicePendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		logger.info("Device Address: {}", device.getNetworkIdentification());
		successPendency(pendency.getPendencyId());
	}

	public void handleDevice(WebSocketModelResponse message) {
		logger.info("Handle Device - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<DeviceUpdatedPendency> devicePendencies = Optional.ofNullable(pendencies).map(AllPendency::getDevice)
					.orElse(Collections.emptyList());

			handlePendency(devicePendencies, pendency -> processDevice(device, pendency), "Handle Device");
		} catch (Exception e) {
			logger.error("Handle Device - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processDevice(DevicesCollection device, DeviceUpdatedPendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		if (device.getRepConfiguration() != null) {
			successPendency(pendency.getPendencyId());
		} else {
			sendAllDevice();
		}
	}

	public void handleSetDeviceEmergency(WebSocketModelResponse message) {
		logger.info("Handle Device Set Emergency - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<SetDeviceEmergencyPendency> emergencyPendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getSetDeviceEmergency).orElse(Collections.emptyList());

			handlePendency(emergencyPendencies, pendency -> processSetDeviceEmergency(device, pendency),
					"Handle Device Set Emergency");
		} catch (Exception e) {
			logger.error("Handle Device Set Emergency - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processSetDeviceEmergency(DevicesCollection device, SetDeviceEmergencyPendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		logger.info("Processing device emergency for Device: {}", device.getNetworkIdentification());
		successPendency(pendency.getPendencyId());
	}

	public void handleUnsetDeviceEmergency(WebSocketModelResponse message) {
		logger.info("Handle Device Unset Emergency - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<UnsetDeviceEmergencyPendency> unsetEmergencyPendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getUnsetDeviceEmergency).orElse(Collections.emptyList());

			handlePendency(unsetEmergencyPendencies, pendency -> processUnsetDeviceEmergency(device, pendency),
					"Handle Device Unset Emergency");
		} catch (Exception e) {
			logger.error("Handle Device Unset Emergency - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processUnsetDeviceEmergency(DevicesCollection device, UnsetDeviceEmergencyPendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		logger.info("Processing device unset emergency for Device: {}", device.getNetworkIdentification());
		successPendency(pendency.getPendencyId());
	}

	public void handleAllowCardList(WebSocketModelResponse message) {
		logger.info("Handle Allow Card List - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<DevicePendency> cardListPendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getLoadAllowCardList).orElse(Collections.emptyList());

			handlePendency(cardListPendencies, pendency -> processAllowCardList(device, pendency),
					"Handle Allow Card List");
		} catch (Exception e) {
			logger.error("Handle Allow Card List - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processAllowCardList(DevicesCollection device, DevicePendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		if (device.getRepConfiguration() == null) {
			successPendency(pendency.getPendencyId());
		} else {
			updatePendency(pendency.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
		}
	}

	public void handleRemoveAllowCardList(WebSocketModelResponse message) {
		try {
			logger.info("Handle Device Remove Allow Card List - Message Remove Allow Card: " + message);

			AllPendency allPendencyBody = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId()))
					.getBody();
			DevicesCollection deviceCollection = deviceController
					.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (deviceCollection != null && allPendencyBody != null) {
				logger.info("Handle Device Remove Allow Card List - Device Address: "
						+ deviceCollection.getNetworkIdentification());
				List<DevicePendency> devicePendencyRemoveAllowCardList = allPendencyBody.getRemoveAllowCardList();

				if (!devicePendencyRemoveAllowCardList.isEmpty()) {
					for (DevicePendency pendencyRemoveAllowCardList : devicePendencyRemoveAllowCardList) {
						if (PendencyQueue.searchKey(pendencyRemoveAllowCardList.getPendencyId()) != null) {
							logger.info(
									"Handle Device Remove Allow Card List - Pending completed, awaiting device return!"
											+ PendencyQueue.searchKey(pendencyRemoveAllowCardList.getPendencyId()));
						} else {
							logger.info("pendencyRemoveAllowCardList");
							successPendency(pendencyRemoveAllowCardList.getPendencyId());
						}
					}
				}
			} else {
				logger.error("Handle Device Remove Allow Card List - Failed to retrieve necessary data.");
			}
		} catch (Exception e) {
			logger.error("Handle Device Remove Allow Card List - Error", e);
		}
	}

	public void handleIncludeCard(WebSocketModelResponse message) {
		logger.info("Handle Include Card - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<IncludeCardPendency> includeCardPendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getIncludeCard).orElse(Collections.emptyList());

			handlePendency(includeCardPendencies, pendency -> processIncludeCard(device, pendency),
					"Handle Include Card");
		} catch (Exception e) {
			logger.error("Handle Include Card - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processIncludeCard(DevicesCollection device, IncludeCardPendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		successPendency(pendency.getPendencyId());
	}

	public void handleExcludeCard(WebSocketModelResponse message) {
		logger.info("Handle Exclude Card - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<ExcludeCardPendency> excludeCardPendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getExcludeCard).orElse(Collections.emptyList());

			handlePendency(excludeCardPendencies, pendency -> processExcludeCard(device, pendency),
					"Handle Exclude Card");
		} catch (Exception e) {
			logger.error("Handle Exclude Card - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processExcludeCard(DevicesCollection device, ExcludeCardPendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		successPendency(pendency.getPendencyId());
	}

	public void handleAllowBiometryList(WebSocketModelResponse message) {
		logger.info("Handle Allow Biometry List - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<DevicePendency> biometryPendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getLoadBiometryList).orElse(Collections.emptyList());

			handlePendency(biometryPendencies, pendency -> processAllowBiometryList(device, pendency),
					"Handle Allow Biometry List");
		} catch (Exception e) {
			logger.error("Handle Allow Biometry List - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processAllowBiometryList(DevicesCollection device, DevicePendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		successPendency(pendency.getPendencyId());
	}

	public void handleIncludeBiometry(WebSocketModelResponse message) {
		try {

			logger.info("Handle Include Biometry", message.toString());

			List<IncludeBiometryPendency> devicePendencyAllowBiometryList = new ArrayList<>();

			AllPendency allPendencyBody = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId()))
					.getBody();
			DevicesCollection deviceCollection = deviceController
					.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (deviceCollection != null && allPendencyBody != null) {
				logger.info("Handle Include Biometry - Device Address: " + deviceCollection.getNetworkIdentification());

				List<IncludeBiometryPendency> includeBiometrys = allPendencyBody.getIncludeBiometry();

				if (!includeBiometrys.isEmpty()) {
					for (IncludeBiometryPendency pendency : includeBiometrys) {
						logger.info("Handle Include Biometry - Pendency found: " + pendency);
						devicePendencyAllowBiometryList.add(pendency);
					}
				} else {
					logger.info("Handle Include Biometry - The issue could not be found.");
				}
			} else {
				logger.info("Handle Include Biometry - Failed to retrieve necessary data.");
			}

			if (!devicePendencyAllowBiometryList.isEmpty()) {
				for (IncludeBiometryPendency pendencyIncludeBiometry : devicePendencyAllowBiometryList) {

					logger.info("Handle Include Biometry - check pendency: "
							+ PendencyQueue.searchKey(pendencyIncludeBiometry.getPendencyId()));
					if (PendencyQueue.searchKey(pendencyIncludeBiometry.getPendencyId()) != null) {
						logger.info("Handle Include Biometry - Pending completed, awaiting device return!");
					} else {
						logger.info("IncludeBiometryPendency");
						successPendency(pendencyIncludeBiometry.getPendencyId());
					}
				}
			} else {
				logger.error("Handle Include Biometry - Pending not found!");
			}
		} catch (

		Exception ex) {
			logger.error("Handle Include Biometry - Exception occurred", ex);
		}
	}

	public void handleRemoveBiometryList(WebSocketModelResponse message) {
		logger.info("Handle Remove Biometry List - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<DevicePendency> removeBiometryPendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getRemoveBiometryList).orElse(Collections.emptyList());

			handlePendency(removeBiometryPendencies, pendency -> processRemoveBiometryList(device, pendency),
					"Handle Remove Biometry List");
		} catch (Exception e) {
			logger.error("Handle Remove Biometry List - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processRemoveBiometryList(DevicesCollection device, DevicePendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		successPendency(pendency.getPendencyId());
	}

	public void handleExcludeBiometry(WebSocketModelResponse message) {
		logger.info("Handle Exclude Biometry - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<ExcludeBiometryPendency> excludeBiometryPendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getExcludeBiometry).orElse(Collections.emptyList());

			handlePendency(excludeBiometryPendencies, pendency -> processExcludeBiometry(device, pendency),
					"Handle Exclude Biometry");
		} catch (Exception e) {
			logger.error("Handle Exclude Biometry - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processExcludeBiometry(DevicesCollection device, ExcludeBiometryPendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		successPendency(pendency.getPendencyId());
	}

	public void handleUpdateRepPerson(WebSocketModelResponse message) {
		logger.info("Handle Update Rep Person - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<UpdatePersonREPPendency> updateRepPendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getUpdatePersonREP).orElse(Collections.emptyList());

			handlePendency(updateRepPendencies, pendency -> processUpdateRepPerson(pendency),
					"Handle Update Rep Person");
		} catch (Exception e) {
			logger.error("Handle Update Rep Person - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processUpdateRepPerson(UpdatePersonREPPendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		switch (pendency.getOperationId()) {
		case INCLUDE_CARD:
		case EXCLUDE_CARD:
		case EXCLUDE_PERSON:
			successPendency(pendency.getPendencyId());
			break;
		default:
			updatePendency(pendency.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
			logger.error("Unknown operation for pendency: {}", pendency);
		}
	}

	public void handleManufatureRep(WebSocketModelResponse message) {
		logger.info("Handle Manufacturer Rep - Processing message: {}", message);

		try {
			DevicesCollection device = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (device == null) {
				logger.error("Device not found for ID: {}", message.getDeviceId());
				return;
			}

			AllPendency pendencies = iApisController.getPendencyIdDevice(Long.valueOf(message.getDeviceId())).getBody();
			List<ManufacturerUpdatedPendency> manufacturerPendencies = Optional.ofNullable(pendencies)
					.map(AllPendency::getManufacturerUpdated).orElse(Collections.emptyList());

			handlePendency(manufacturerPendencies, pendency -> processManufacturerRep(device, pendency),
					"Handle Manufacturer Rep");
		} catch (Exception e) {
			logger.error("Handle Manufacturer Rep - Error processing: {}", e.getMessage(), e);
		}
	}

	private void processManufacturerRep(DevicesCollection device, ManufacturerUpdatedPendency pendency) {
		if (PendencyQueue.searchKey(pendency.getPendencyId()) != null) {
			logger.info("Pendency already processed: {}", pendency.getPendencyId());
			return;
		}

		// Assuming manufacturer update logic here
		successPendency(pendency.getPendencyId());
	}

	// -------------------------------------------------------------------------
	// TODO Develop
	// -------------------------------------------------------------------------

	// Function with built structure - Test
	public void handleBlockDevice(WebSocketModelResponse message) {
		logger.info("Handle Block Device - Message received at BlockDevice: " + message);

	};

	// Function with built structure - Test
	public void handleUnblockDevice(WebSocketModelResponse message) {
		logger.info("Handle Unblock Device - Message received at UnblockDevice: " + message);

	};

	public void handleUpdateFirmware(WebSocketModelResponse message) {
		logger.info("Handle Update Firmware - Message received at UpdateFirmware: " + message);

	};

	public void handleDeviceInputStatus(WebSocketModelResponse message) {
		logger.info("Handle Device Input Status - Message received at DeviceInputStatus: " + message);

	};

	//
	public void handleCredentialFacialList(WebSocketModelResponse message) {
		logger.info("Handle Credential Facial List - Message received at CredentialFacialList: " + message);

	};

	//
	public void handleLoadHolidayList(WebSocketModelResponse message) {
		logger.info("Handle Load Holiday List - Message received at LoadHolidayList: " + message);

	};

	public void handleRemoveHolidayList(WebSocketModelResponse message) {
		logger.info("Handle Remove Holiday List - Message received at RemoveHolidayList: " + message);

	};

	public void handleCollectEvents(WebSocketModelResponse message) {
		logger.info("Handle Collect Events - Message received at CollectEvents: " + message);

	};

}
