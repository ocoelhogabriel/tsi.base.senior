package br.com.telematica.seniorx.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.google.gson.Gson;

import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.AccessBiometry;
import br.com.telematica.seniorx.model.AllPendency;
import br.com.telematica.seniorx.model.AreaControlList;
import br.com.telematica.seniorx.model.Biometry;
import br.com.telematica.seniorx.model.CardAndTechnology;
import br.com.telematica.seniorx.model.DevicePendency;
import br.com.telematica.seniorx.model.DeviceStatus;
import br.com.telematica.seniorx.model.DeviceUpdatedPendency;
import br.com.telematica.seniorx.model.DriverDateTime;
import br.com.telematica.seniorx.model.Event;
import br.com.telematica.seniorx.model.Event.EventTypeEnum;
import br.com.telematica.seniorx.model.ExcludeBiometryPendency;
import br.com.telematica.seniorx.model.ExcludeCardPendency;
import br.com.telematica.seniorx.model.ExtensibleConfiguration;
import br.com.telematica.seniorx.model.ExtensibleProperty;
import br.com.telematica.seniorx.model.IncludeBiometryPendency;
import br.com.telematica.seniorx.model.IncludeCardPendency;
import br.com.telematica.seniorx.model.ManagerDevice;
import br.com.telematica.seniorx.model.ManufacturerUpdatedPendency;
import br.com.telematica.seniorx.model.OnOffStatusEnum;
import br.com.telematica.seniorx.model.OperationEnum;
import br.com.telematica.seniorx.model.OperationIdEnum;
import br.com.telematica.seniorx.model.OperationUpdateDeviceEnum;
import br.com.telematica.seniorx.model.PendencyUpdated;
import br.com.telematica.seniorx.model.PersonRep;
import br.com.telematica.seniorx.model.RepConfiguration;
import br.com.telematica.seniorx.model.SetDeviceEmergencyPendency;
import br.com.telematica.seniorx.model.StatusField;
import br.com.telematica.seniorx.model.UnsetDeviceEmergencyPendency;
import br.com.telematica.seniorx.model.UpdatePersonREPPendency;
import br.com.telematica.seniorx.model.devices.DeviceController;
import br.com.telematica.seniorx.model.devices.DevicesCollection;
import br.com.telematica.seniorx.websocket.model.WebSocketModelResponse;
import br.com.telematica.util.Utils;
import jakarta.annotation.PostConstruct;

public class SeniorXService {

	private static final Logger logger = LoggerFactory.getLogger(SeniorXService.class);
	boolean checkKeepAlive = false;
	ApiAllPendencyModel apiManagerDeviceAllPendencyModel = null;
	private final DeviceController deviceController;
	private final IApisController iApisController;

	public SeniorXService(DeviceController deviceController) {
		this.deviceController = deviceController;
		Thread.currentThread().setName("Senior Handler");
	}

	@PostConstruct
	public void initialize() {
		sendAllDevice();
		// sendStatusEventAllDevice();
	}

	public void sendAllDevice() {

		try {

			List<ManagerDevice> managerDevice = iApisController.getDevice().getBody();
			List<AreaControlList> areaControlResponse = iApisController.getAreaControl().getBody();

			if ((managerDevice != null && !managerDevice.isEmpty()) || (areaControlResponse != null && !areaControlResponse.isEmpty())) {
				deviceController.onAreaUpdate(areaControlResponse);
				deviceController.onDeviceUpdateManagerDevice(managerDevice);

			} else {
				logger.info("Send All Device" + "urlDevice and urlDataMartAreaControl are null.");
			}
		} catch (Exception e) {
			logger.error("Send All Device" + "Senior - Error when executing the mapping of registered equipment", e);
		}
	}

	public void sendStatusEventAllDevice() {
		Gson gson = new Gson();
		List<Event> eventsToNotify = new ArrayList<>();
		List<DevicesCollection> devicesUp = new ArrayList<>();
		List<DevicesCollection> devicesDown = new ArrayList<>();
		Collection<DevicesCollection> devices = deviceController.getAllDevices();

		if (devices == null || devices.isEmpty()) {
			logger.error("Send Status Event All Device" + "No devices found or empty response");
			return;
		}

		for (DevicesCollection device : devices) {
			ApiDeviceRequestModel requestModel = new ApiDeviceRequestModel();
			requestModel.setDeviceIpAddress(device.getNetworkIdentification());
			HttpEntity<ApiDeviceRequestModel> requestEntity = new HttpEntity<>(requestModel);

			try {
				ApiDeviceGenericModel apiResponse = restTemplate.postForObject(UtilsUrlService.urlConexGetStatus, requestEntity, ApiDeviceGenericModel.class);

				if (Boolean.FALSE.equals(apiResponse.getError())) {
					WebhookDeviceStatusModel deviceStatus = gson.fromJson(gson.toJson(apiResponse.getData()), WebhookDeviceStatusModel.class);
					handleDeviceStatus(device, deviceStatus, devicesUp, devicesDown);
				} else {
					handleDeviceError(device, apiResponse.getMessage());
					devicesDown.add(device);
				}

			} catch (Exception e) {
				logger.error("Send Status Event All Device", String.format("Device - Error, device does not respond - ID: %s * Network: %s", Utils.padStart(device.getId().toString(), 10, '0'), device.getNetworkIdentification()), e);
				devicesDown.add(device);
			}
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String formattedDate = dateFormat.format(new Date());

		addEvents(devicesUp, eventsToNotify, OnOffStatusEnum.ONLINE, EventTypeEnum.DEVICE_ONLINE, formattedDate);
		addEvents(devicesDown, eventsToNotify, OnOffStatusEnum.OFFLINE, EventTypeEnum.DEVICE_OFFLINE, formattedDate);

		notifyPlatform(eventsToNotify);
	}

	private void handleDeviceStatus(DevicesCollection device, WebhookDeviceStatusModel deviceStatus, List<DevicesCollection> devicesUp, List<DevicesCollection> devicesDown) {
		ResponseStatusEnum status = deviceStatus.getDeviceStatus();
		logger.info("Send Status Event All Device", String.format("cached device ID: %s * Network: %s * Status: %s", Utils.padStart(device.getId().toString(), 10, '0'), device.getNetworkIdentification(), status));

		if (ResponseStatusEnum.UP.equals(status)) {
			devicesUp.add(device);
		} else {
			devicesDown.add(device);
		}
	}

	private void handleDeviceError(DevicesCollection device, String errorMessage) {
		logger.error("Send Status Event All Device", String.format("Device - Error Response. %s", errorMessage != null ? errorMessage : "Unknown error"));
	}

	private void addEvents(List<DevicesCollection> devices, List<Event> eventList, OnOffStatusEnum status, EventTypeEnum eventType, String eventDate) {
		for (DevicesCollection device : devices) {
			Event event = codeUtils.handleEventModel(device.getId().intValue(), device.getAreaControl(), status, eventType, eventDate);
			eventList.add(event);
		}
	}

	private void notifyPlatform(List<Event> events) {
		try {
			HttpEntity<List<Event>> entityResponse = new HttpEntity<>(events, UtilsHttpHeaderSenior.sendHttpSenior());
			ResponseEntity<String> response = restTemplate.exchange(UtilsUrlService.urlNotifyDeviceEvent, HttpMethod.POST, entityResponse, String.class);

			logger.info("Send Status Event All Device", String.format("Data sent to the platform. Status Equipment sent. %s", response.getStatusCode()));
		} catch (Exception e) {
			logger.error("Send Status Event All Device" + "Data sent to the platform. Status Equipment Error", e);
		}
	}

//	public void sendAllDevice() {
////		logger.info("Send All Device" + "Searching for devices...");
//
//		HttpEntity<String> entity = new HttpEntity<>(httpHeaderSenior);
//
//		try {
//			final String urlDevice = UtilsUrlService.urlDevice;
//			final String urlDataMartAreaControl = UtilsUrlService.urlDataMartAreaControl;
//			if (urlDevice != null || urlDataMartAreaControl != null) {
//				List<ManagerDevice> managerDevice = restTemplate.exchange(urlDevice, HttpMethod.GET, entity, new ParameterizedTypeReference<List<ManagerDevice>>() {
//				}).getBody();
//				List<AreaControlList> areaControlResponse = restTemplate.exchange(urlDataMartAreaControl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<AreaControlList>>() {
//				}).getBody();
//
//				if (managerDevice != null && !managerDevice.isEmpty()) {
//					mapManager.clearDevice();
//					for (ManagerDevice deviceManager : managerDevice) {
//						String deviceIdFromResponse = String.valueOf(deviceManager.getId());
//						ApiDeviceModel device = new ApiDeviceModel();
//						device.setId(deviceManager.getId().toString());
//						device.setDeviceIpAddress(deviceManager.getNetworkIdentification());
//						device.setDeviceArea(deviceManager.getAreaId().toString());
//						device.setExtensibleConfiguration(deviceManager.getExtensibleConfiguration());
//						mapManager.sendCachedDevices(deviceIdFromResponse, device);
//						logger.info("Send All Device" + "Device to cached data for ID:  " + Utils.padStart(deviceManager.getId().toString(), 10, '0') + " * Network: " + deviceManager.getNetworkIdentification());
//					}
////					logger.info("Send All Device" + "List devices that received" + new Gson().toJson(mapManager.sendGetDevice()));
//					if (!checkKeepAlive) {
//						deviceKeepAlive.startProcess();
//						checkKeepAlive = true;
//					}
//				}
//			} else {
//				logger.info("Send All Device" + "urlDevice is null.");
//			}
//		} catch (Exception e) {
//			logger.error("Send All Device" + "Senior - Error when executing the mapping of registered equipment", e);
//		}
//
//	}
//	public void sendStatusEventAllDevice() {
////		logger.info("Send Status Event All Device" + "Device check started...");
//		Gson gson;
//		Event event = null;
//
//		try {
//			List<Event> listNotify = new ArrayList<>();
//			List<ApiDeviceModel> listUp = new ArrayList<>();
//			List<ApiDeviceModel> listDown = new ArrayList<>();
//			Collection<ApiDeviceModel> managerDevice = mapManager.sendGetDevice();
//
//			if (managerDevice != null && !managerDevice.isEmpty()) {
//
//				for (ApiDeviceModel device : managerDevice) {
//					ApiDeviceRequestModel apiIpDeviceModel = new ApiDeviceRequestModel();
//					apiIpDeviceModel.setDeviceIpAddress(device.getDeviceIpAddress());
//					HttpEntity<ApiDeviceRequestModel> request = new HttpEntity<>(apiIpDeviceModel);
//
//					try {
//						ApiDeviceGenericModel apiDeviceGenericModel = restTemplate.postForObject(UtilsUrlService.urlConexGetStatus, request, ApiDeviceGenericModel.class);
//
//						if (Boolean.FALSE.equals(apiDeviceGenericModel.getError())) {
//							gson = new Gson();
//							String getData = gson.toJson(apiDeviceGenericModel.getData());
//
//							WebhookDeviceStatusModel deviceStatus = gson.fromJson(getData, WebhookDeviceStatusModel.class);
//							ResponseStatusEnum deviceStatusEnum = deviceStatus.getDeviceStatus();
//
//							if (deviceStatusEnum instanceof ResponseStatusEnum) {
//
//								logger.info("Send Status Event All Device" + "cached device ID: " + Utils.padStart(device.getId(), 10, '0') + " * Network: " + device.getDeviceIpAddress() + " * Status: " + deviceStatusEnum);
//
//								if (ResponseStatusEnum.UP.equals(deviceStatusEnum))
//									listUp.add(device);
//								else if (ResponseStatusEnum.DOWN.equals(deviceStatusEnum)) {
////									listUp.add(device);
//									listDown.add(device);
//								}
//
//							} else {
//								logger.info("Send Status Event All Device" + "Device - Unexpected response data type: " + deviceStatusEnum.getClass().getSimpleName());
////								listUp.add(device);
//								listDown.add(device);
//							}
//						} else {
//							String errorMessage = apiDeviceGenericModel.getError() != null ? apiDeviceGenericModel.getMessage() : "Unknown error";
//							logger.error("Send Status Event All Device" + "Device - Error Response. " + errorMessage);
////							listUp.add(device);
//							listDown.add(device);
//						}
//					} catch (Exception e) {
//						logger.error("Send Status Event All Device" + "Device - Error, device does not respond - ID: " + Utils.padStart(device.getId(), 10, '0') + " * Network: " + device.getDeviceIpAddress() + "\n", e);
////						listUp.add(device);
//						listDown.add(device);
//					}
//
//				}
//
//				Date d = new Date();
//				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//				String dSDF = df.format(d);
//				for (ApiDeviceModel device : listUp) {
//
//					event = codeUtils.handleEventModel(Integer.valueOf(device.getId()), Long.valueOf(device.getDeviceArea()), OnOffStatusEnum.ONLINE, EventTypeEnum.DEVICE_ONLINE, dSDF);
//					listNotify.add(event);
//				}
//
//				for (ApiDeviceModel device : listDown) {
//					event = codeUtils.handleEventModel(Integer.valueOf(device.getId()), Long.valueOf(device.getDeviceArea()), OnOffStatusEnum.OFFLINE, EventTypeEnum.DEVICE_OFFLINE, dSDF);
//					listNotify.add(event);
//				}
//
//				// LoggerUtils.logSeniorInfo("List of verified equipment: " + listNotify);
//				try {
//					HttpEntity<List<Event>> entityResponse = new HttpEntity<>(listNotify, UtilsHttpHeaderSenior.sendHttpSenior());
//					ResponseEntity<String> responseEntity = restTemplate.exchange(UtilsUrlService.urlNotifyDeviceEvent, HttpMethod.POST, entityResponse, String.class);
//
//					HttpStatus statusCodeResponse = responseEntity.getStatusCode();
//
//					logger.info("Send Status Event All Device" + "Data sent to the platform. Status Equipment sent. " + statusCodeResponse);
//				} catch (Exception e) {
//					logger.error("Send Status Event All Device" + "Data sent to the platform. Status Equipment Error", e);
//				}
//
//			} else {
//				logger.error("Send Status Event All Device" + "Data sent to the platform. No devices found or empty response");
//			}
//		} catch (Exception e) {
//			logger.error("Status Event All Device" + "Data sent to the platform. Error Equipment List", e);
//		}
//	}

	public void handleDeviceDateTime(WebSocketModelResponse message) {
		try {

			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

			logger.info("Handle Device Date Time", message.toString());

			HttpEntity<String> entityHeader = new HttpEntity<>(httpHeaderSenior);

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DriverDateTime dateTimeBody = null;

			if (deviceCollection != null) {
				logger.info("Handle Device Date Time" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<DevicePendency> devicePendencyDateTime = (allPendencyBody != null && allPendencyBody.getDeviceDateTime() != null) ? allPendencyBody.getDeviceDateTime() : new ArrayList<>();

				for (DevicePendency pendencyHandler : devicePendencyDateTime) {
					if (MapManager.searchKey(pendencyHandler.getPendencyId()) != null) {
						logger.info("Handle Device Date Time" + "Pending completed, awaiting device return!");

					} else {

						try {
							dateTimeBody = restTemplate.exchange(UtilsUrlService.urlSeniorDriveDateTime, HttpMethod.GET, entityHeader, DriverDateTime.class).getBody();

							if (dateTimeBody != null) {
								logger.info("Handle Device Date Time" + "Date and time without GMT found: " + dateTimeBody.getDateTime());

								AreaControlList areaControl = deviceController.findAreaControlList(deviceCollection.getAreaControl().getId());

								if (areaControl != null) {
									String dateTimeAddGmt = Utils.addGmtToDateTime(dateTimeBody.getDateTime(), areaControl.getGmt());
									ConexDateTimeModel dateTimeModel = new ConexDateTimeModel();
									dateTimeModel.setDateTime(dateTimeAddGmt);
									logger.info("Handle Device Date Time" + "Date and time with GMT: " + dateTimeAddGmt);
									dateTimeModel.setDeviceIpAddress(deviceCollection.getNetworkIdentification());

									HttpEntity<ConexDateTimeModel> requestEntity = new HttpEntity<>(dateTimeModel);
									ApiDeviceGenericModel apiDeviceGenericModel = restTemplate.postForObject(UtilsUrlService.urlConexDriveDateTime, requestEntity, ApiDeviceGenericModel.class);

									if (Boolean.FALSE.equals(apiDeviceGenericModel.getError())) {
										MapManager.sendCachedAllPendency(deviceCollection.getId(), apiDeviceGenericModel.getPendencyId(), pendencyHandler.getPendencyId());
										logger.info("Handle Device Date Time" + "Device Pendency id return: " + apiDeviceGenericModel.getPendencyId() + " and " + pendencyHandler.getPendencyId());
									} else {
										logger.error("Handle Device Date Time" + "Error receiving Date Time return message!");
										sendPendencyUpdate(pendencyHandler.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
									}
								} else {
									logger.error("Handle Device Date Time" + "AreaControl not found for manager device.");
									sendPendencyUpdate(pendencyHandler.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
								}
							} else {
								logger.error("Handle Device Date Time" + "Received empty or null Area Control List!");
								sendPendencyUpdate(pendencyHandler.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
							}
						} catch (DateTimeParseException e) {
							logger.error("Handle Device Date Time" + "Error Date Time", e);
							sendPendencyUpdate(pendencyHandler.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
						}
					}
				}
			} else {
				logger.error("Handle Device Date Time" + "ManagerDevice body is null.");
			}
		} catch (Exception e) {
			logger.error("Handle Device Date Time" + "Error Date Time", e);
		}
	}

	public void handleDeviceStatus(WebSocketModelResponse message) {
		try {
			logger.info("Handle Device Status" + "Message received at DeviceStatus: " + message);

			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

			logger.info("Handle Device Status", message.toString());

			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();

			if (deviceCollection != null) {
				logger.info("Handle Device Status" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<DevicePendency> deviceStatusPendencyList = Optional.ofNullable(allPendencyBody).map(AllPendency::getDeviceStatus).orElse(Collections.emptyList());

				if (!deviceStatusPendencyList.isEmpty()) {
					DevicePendency deviceStatusPendency = deviceStatusPendencyList.stream().filter(p -> p.getManagerDeviceId().equals(deviceCollection.getId())).findFirst().orElse(null);

					if (deviceStatusPendency == null) {
						logger.info("Handle Device Status" + "Device not found.");
					} else if (MapManager.searchKey(deviceStatusPendency.getPendencyId()) != null) {
						logger.info("Handle Device Status" + "Pending completed, awaiting device return!");
					} else {
						ConexComandDeviceStatusModel comandDeviceStatus = new ConexComandDeviceStatusModel();
						comandDeviceStatus.setDeviceIpAddress(deviceCollection.getNetworkIdentification());

						HttpEntity<ConexComandDeviceStatusModel> entityComandDeviceStatus = new HttpEntity<>(comandDeviceStatus);

						try {
							ApiDeviceGenericModel apiDeviceGenericModel = restTemplate.exchange(UtilsUrlService.urlConexGetStatus, HttpMethod.POST, entityComandDeviceStatus, ApiDeviceGenericModel.class).getBody();

							if (Boolean.FALSE.equals(apiDeviceGenericModel.getError())) {

								Gson gson = new Gson();
								String getData = gson.toJson(apiDeviceGenericModel.getData());
								logger.info("Handle Device Status" + "Received device message data : " + getData);

								WebhookRequestGetDeviceStatusModel deviceStatusRequest = gson.fromJson(getData, WebhookRequestGetDeviceStatusModel.class);
								ConexDeviceStatusEnum deviceStatusType = deviceStatusRequest.getDeviceStatus();

								StatusField statusField = new StatusField();
								statusField.setKey(deviceCollection.getId().toString());
								statusField.setValue(deviceStatusType.toString());

								DeviceStatus deviceStatus = new DeviceStatus();
								deviceStatus.setManagerDeviceId(deviceCollection.getId());
								deviceStatus.setPendencyId(deviceStatusPendency.getPendencyId());
								deviceStatus.setStatus(Collections.singletonList(statusField));

								HttpEntity<DeviceStatus> entityResponse = new HttpEntity<>(deviceStatus, UtilsHttpHeaderSenior.sendHttpSenior());
								ResponseEntity<String> responseEntity = restTemplate.exchange(UtilsUrlService.urlDeviceStatus, HttpMethod.POST, entityResponse, String.class);

								HttpStatus statusCodeResponse = responseEntity.getStatusCode();
								logger.info("Handle Device Status" + "Sent Senior Device Status: " + statusCodeResponse);
							} else {
								logger.info("Handle Device Status" + "Error receiving Device Status feedback message");
								sendPendencyUpdate(deviceStatusPendency.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
							}
						} catch (RestClientException e) {
							logger.error("Handle Device Status" + "Error sending Senior Device Status", e);
							sendPendencyUpdate(deviceStatusPendency.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
						}
					}
				} else {
					logger.error("Handle Device Status" + "Pending list is empty! Data received: " + deviceStatusPendencyList.toString());
				}
			}
		} catch (Exception e) {
			logger.error("Handle Device Status" + "Error Device Status", e);
		}
	}

	public void handleDevice(WebSocketModelResponse message) {
		try {
			logger.info("Handle Device" + "Message received at Device: " + message);

			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

			logger.info("Handle Device", message.toString());

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			List<DeviceUpdatedPendency> devicePendencyList = new ArrayList<>();
			for (DeviceUpdatedPendency pendency : allPendencyBody.getDevice()) {
				logger.info("Handle Device" + "Pendency found: " + pendency);
				devicePendencyList.add(pendency);
			}

			if (!devicePendencyList.isEmpty()) {
				DeviceUpdatedPendency deviceConfigPendency = devicePendencyList.stream().filter(p -> p.getManagerDeviceId().equals(deviceCollection.getId())).findFirst().orElse(null);
				if (deviceCollection != null) {
					logger.info("Handle Device" + "Device Address: " + deviceCollection.getNetworkIdentification());
					if (deviceConfigPendency == null) {
						logger.info("Handle Device" + "Device config pendency not found.");
					} else if (MapManager.searchKey(deviceConfigPendency.getPendencyId()) != null) {
						logger.info("Handle Device" + "Pending completed, awaiting device return!");
					} else {

						if (deviceConfigPendency.getOperation() == OperationUpdateDeviceEnum.DEVICE_CONFIG) {
							try {

								if (deviceCollection.getRepConfiguration() != null) {

									RepConfiguration repConfig = deviceCollection.getRepConfiguration();
									ExtensibleConfiguration extensible = deviceCollection.getExtensibleConfiguration();
									List<ExtensibleProperty> listPropertyExtensible = extensible.getExtensiblePropertyList();
									ExtensibleProperty filteProperty = listPropertyExtensible.stream().filter(property -> "CPF".equals(property.getKey())).findFirst().orElse(null);

									ConexEmployeerModel employeerModel = new ConexEmployeerModel();
									employeerModel.setName(repConfig.getCompanyName());
									employeerModel.setAddress(repConfig.getAddress());
									employeerModel.setDeviceIpAddress(deviceCollection.getNetworkIdentification());
									employeerModel.setCei(repConfig.getCei() == null ? "" : repConfig.getCei());
									employeerModel.setResponsibleCpf(filteProperty.getValue() == null ? "" : filteProperty.getValue());
									if (repConfig.getCnpj() == null) {
										employeerModel.setDocType(DocTypeEnum.CPF);
										employeerModel.setDoc(repConfig.getCpf() == null ? "00000000" : repConfig.getCpf());
									} else if (repConfig.getCnpj() != null) {
										employeerModel.setDocType(DocTypeEnum.CNPJ);
										employeerModel.setDoc(repConfig.getCnpj() == null ? "00000000" : repConfig.getCnpj());
									}

									HttpEntity<ConexEmployeerModel> entityEmployeesList = new HttpEntity<>(employeerModel);
									try {

										ApiDeviceGenericModel apiDeviceGeneric = restTemplate.postForObject(UtilsUrlService.urlConexSetEmployeer, entityEmployeesList, ApiDeviceGenericModel.class);

										if (Boolean.FALSE.equals(apiDeviceGeneric.getError())) {
											MapManager.sendCachedAllPendency(deviceCollection.getId(), apiDeviceGeneric.getPendencyId(), deviceConfigPendency.getPendencyId());
											logger.info("Handle Device" + "Device Pendency id return: " + apiDeviceGeneric.getPendencyId() + " and " + deviceConfigPendency.getPendencyId());
										} else {
											logger.info("Handle Device" + "Error pending response from the equipment.");
											sendPendencyUpdate(deviceConfigPendency.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
											logger.error("Handle Device" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());

										}
									} catch (Exception e) {
										logger.error("Handle Device" + "Error in request for equipment.", e);
										sendPendencyUpdate(deviceConfigPendency.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
									}

								} else {
									sendAllDevice();
									ApiPendencyModel apiPendency = new ApiPendencyModel(deviceCollection.getId(), deviceConfigPendency.getPendencyId(), null);
									WebhookPendencyModel webhookPendency = new WebhookPendencyModel();
									webhookPendency.setStatusPendency(ResponseStatusEnum.UP);
									webhookPendency.setPendency(apiPendency);
									WebhookService.addItemToQueue(webhookPendency);
								}

//								sendPendencyUpdate(deviceConfigPendency.getPendencyId(), 343, OperationEnum.REMOVE_PENDENCY);
							} catch (Exception e) {
								logger.error("Handle Device" + "Error occurred during processing: " + e.getMessage());
								sendPendencyUpdate(deviceConfigPendency.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
							}
						} else {

							sendAllDevice();

							ApiPendencyModel apiPendency = new ApiPendencyModel(deviceCollection.getId(), deviceConfigPendency.getPendencyId(), null);
							WebhookPendencyModel webhookPendency = new WebhookPendencyModel();
							webhookPendency.setStatusPendency(ResponseStatusEnum.UP);
							webhookPendency.setPendency(apiPendency);
							WebhookService.addItemToQueue(webhookPendency);
						}

					}
				} else {
					sendPendencyUpdate(deviceConfigPendency.getPendencyId(), 343, OperationEnum.REMOVE_PENDENCY);
				}
			} else {
				logger.error("Handle Device" + "Pending list is empty! Data received: " + devicePendencyList.toString());
			}

		} catch (Exception e) {
			logger.error("Handle Device" + "Error Device", e);
		}
	}

	public void handleSetDeviceEmergency(WebSocketModelResponse message) {
		try {
			logger.info("Handle Device Set Emergency" + "Message received at DeviceEmergency: " + message);

			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

			logger.info("Handle Device Set Emergency", message.toString());

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (deviceCollection != null && allPendencyBody != null) {
				logger.info("Handle Device Set Emergency" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<SetDeviceEmergencyPendency> devicePendencySetEmergency = Optional.ofNullable(allPendencyBody.getSetDeviceEmergency()).orElse(Collections.emptyList());

				SetDeviceEmergencyPendency deviceEmergency = devicePendencySetEmergency.stream().filter(p -> p.getManagerDeviceId().equals(deviceCollection.getId())).findFirst().orElse(null);

				if (deviceEmergency == null) {
					logger.info("Handle Device Set Emergency" + "Device pendency emergency not found!");
				} else if (MapManager.searchKey(deviceEmergency.getPendencyId()) != null) {
					logger.info("Handle Device Set Emergency" + "Pending completed, awaiting device return!");
				} else {
					ApiDeviceRequestModel deviceIP = new ApiDeviceRequestModel();
					deviceIP.setDeviceIpAddress(deviceCollection.getNetworkIdentification());

					HttpEntity<ApiDeviceRequestModel> conexSetEmergency = new HttpEntity<>(deviceIP);

					try {
						logger.info("Handle Device Set Emergency" + "Senior response: " + conexSetEmergency.getBody());
						ApiDeviceGenericModel apiDeviceGeneric = restTemplate.postForObject(UtilsUrlService.urlConexSetEmergency, conexSetEmergency, ApiDeviceGenericModel.class);
						if (Boolean.FALSE.equals(apiDeviceGeneric.getError())) {
							MapManager.sendCachedAllPendency(deviceCollection.getId(), apiDeviceGeneric.getPendencyId(), deviceEmergency.getPendencyId());
							logger.info("Handle Device Set Emergency" + "Results: " + deviceEmergency);
						} else {
							logger.error("Handle Device Set Emergency" + "Error receiving Set Emergency return message!");
							sendPendencyUpdate(deviceEmergency.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
						}

					} catch (Exception e) {
						logger.error("Handle Device Set Emergency" + "Error in request for equipment", e);
						sendPendencyUpdate(deviceEmergency.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
					}
				}
			} else {
				logger.error("Handle Device Set Emergency" + "Pending list is empty! Data received: ");
			}
		} catch (Exception ex) {
			logger.error("Handle Device Set Emergency" + "Exception occurred", ex);
		}
	}

	public void handleUnsetDeviceEmergency(WebSocketModelResponse message) {
		logger.info("Handle Device Unset Emergency" + "Message received at UnsetDeviceEmergency: " + message);

		String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

		logger.info("Handle Device Unset Emergency", message.toString());
		try {

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (deviceCollection != null && allPendencyBody != null) {
				logger.info("Handle Device Unset Emergency" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<UnsetDeviceEmergencyPendency> unsetEmergencyDevicePendency = new ArrayList<>();
				for (UnsetDeviceEmergencyPendency pendency : allPendencyBody.getUnsetDeviceEmergency()) {
					logger.info("Handle Device Unset Emergency" + "Pendency found: " + pendency);
					unsetEmergencyDevicePendency.add(pendency);
				}

				if (!unsetEmergencyDevicePendency.isEmpty()) {
					UnsetDeviceEmergencyPendency deviceUnsetEmergency = unsetEmergencyDevicePendency.stream().filter(pendency -> pendency.getManagerDeviceId().equals(deviceCollection.getId())).findFirst().orElse(null);

					if (deviceUnsetEmergency == null) {
						logger.info("Handle Device Unset Emergency" + "Device unset emergency not found!");
					} else if (MapManager.searchKey(deviceUnsetEmergency.getPendencyId()) != null) {
						logger.info("Handle Device Unset Emergency" + "Pending completed, awaiting device return!" + MapManager.searchKey(deviceUnsetEmergency.getPendencyId()));
					} else {
						ApiDeviceRequestModel deviceIP = new ApiDeviceRequestModel();
						deviceIP.setDeviceIpAddress(deviceCollection.getNetworkIdentification());

						HttpEntity<ApiDeviceRequestModel> conexUnsetEmergency = new HttpEntity<>(deviceIP);
						try {
							logger.info("Handle Device Unset Emergency" + "Senior response: " + conexUnsetEmergency.getBody());
							ApiDeviceGenericModel apiDeviceGeneric = restTemplate.postForObject(UtilsUrlService.urlConexUnsetEmergency, conexUnsetEmergency, ApiDeviceGenericModel.class);

							if (Boolean.FALSE.equals(!apiDeviceGeneric.getError())) {
								MapManager.sendCachedAllPendency(deviceCollection.getId(), apiDeviceGeneric.getPendencyId(), deviceUnsetEmergency.getPendencyId());
								logger.info("Handle Device Unset Emergency" + "Results: " + apiDeviceGeneric);
							} else {
								logger.info("Handle Device Unset Emergency" + "Error receiving Unset Emergency return message!");
								sendPendencyUpdate(deviceUnsetEmergency.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
							}
						} catch (Exception e) {
							logger.error("Handle Device Unset Emergency" + "Error in request for equipment", e);
							sendPendencyUpdate(deviceUnsetEmergency.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
						}
					}
				} else {
					logger.error("Handle Device Unset Emergency" + "Pending list is empty! Data received: " + unsetEmergencyDevicePendency.toString());
				}
			} else {
				logger.error("Handle Device Unset Emergency" + "Failed to retrieve necessary data.");
			}
		} catch (Exception ex) {
			logger.error("Handle Device Unset Emergency" + "Exception occurred", ex);
		}

	}

	public void handleAllowCardList(WebSocketModelResponse message) {
		try {
			logger.info("Handle Device Allow Card List" + "Message AllowCardList: " + message);
			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());
			String urlSeniorAccessCard = UtilsUrlService.urlSeniorAccessCard(message.getDeviceId());
			String urlSeniorRepCredential = UtilsUrlService.urlSeniorRepCredential(message.getDeviceId());

			List<DevicePendency> devicePendencyAllowCardList = new ArrayList<>();
			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (deviceCollection != null && allPendencyBody != null) {
				logger.info("Handle Device Allow Card List" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<DevicePendency> loadAllowCard = allPendencyBody.getLoadAllowCardList();

				if (!loadAllowCard.isEmpty()) {
					for (DevicePendency pendency : loadAllowCard) {
						if (pendency.getManagerDeviceId().equals(deviceCollection.getId())) {
							logger.info("Handle Device Allow Card List" + "Pendency found: " + pendency);
							devicePendencyAllowCardList.add(pendency);
						}
					}
				} else {
					logger.info("Handle Device Allow Card List" + "The issue could not be found.");
//					if (deviceCollection.getRepConfiguration() == null) {
//						codeUtils.sendRequestCardList(deviceCollection, urlSeniorAccessCard, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexLoadListCard, 1L, ApiMessageTypeEnum.ALLOW_CARDS_LIST);
//					} else if (deviceCollection.getRepConfiguration() != null) {
//						codeUtils.sendRequestPersonREP(deviceCollection, urlSeniorRepCredential, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexLoadListPerson, 1L, deviceCollection.getExtensibleConfiguration());
//					} else {
//						logger.error("Handle Device Allow Card List" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());
//					}
				}
			} else {
				logger.info("Handle Device Allow Card List" + "Failed to retrieve necessary data.");
			}

			if (!devicePendencyAllowCardList.isEmpty()) {
				for (DevicePendency pendencyAllowCardList : devicePendencyAllowCardList) {
					if (MapManager.searchKey(pendencyAllowCardList.getPendencyId()) != null) {
						logger.info("Handle Device Allow Card List" + "Pending completed, awaiting device return!" + MapManager.searchKey(pendencyAllowCardList.getPendencyId()));
					} else {
						try {
							if (deviceCollection != null) {
								if (deviceCollection.getRepConfiguration() == null) {
									codeUtils.sendRequestCardList(deviceCollection, urlSeniorAccessCard, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexLoadListCard, pendencyAllowCardList.getPendencyId(), ApiMessageTypeEnum.ALLOW_CARDS_LIST);
								} else if (deviceCollection.getRepConfiguration() != null) {
									codeUtils.sendRequestPersonREP(deviceCollection, urlSeniorRepCredential, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexLoadListPerson, pendencyAllowCardList.getPendencyId(), deviceCollection.getExtensibleConfiguration());
								} else {
									sendPendencyUpdate(pendencyAllowCardList.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
									logger.error("Handle Device Allow Card List" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());
								}
							} else {
								sendPendencyUpdate(pendencyAllowCardList.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
								logger.error("Handle Device Allow Card List" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());
							}
						} catch (Exception e) {
							logger.error("Handle Device Allow Card List" + "Error occurred during processing: " + e.getMessage());
							sendPendencyUpdate(pendencyAllowCardList.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
						}
					}
				}
			} else {
				logger.info("Handle Device Allow Card List" + "Pending not found!");
			}
		} catch (Exception ex) {
			logger.error("Handle Device Allow Card List" + "Exception occurred", ex);
		}
	}

	public void handleRemoveAllowCardList(WebSocketModelResponse message) {
		try {
			logger.info("Handle Device Remove Allow Card List" + "Message Remove Allow Card: " + message);

			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (deviceCollection != null && allPendencyBody != null) {
				logger.info("Handle Device Remove Allow Card List" + "Device Address: " + deviceCollection.getNetworkIdentification());
				List<DevicePendency> devicePendencyRemoveAllowCardList = allPendencyBody.getRemoveAllowCardList();

				if (!devicePendencyRemoveAllowCardList.isEmpty()) {
					for (DevicePendency pendencyRemoveAllowCardList : devicePendencyRemoveAllowCardList) {
						if (MapManager.searchKey(pendencyRemoveAllowCardList.getPendencyId()) != null) {
							logger.info("Handle Device Remove Allow Card List" + "Pending completed, awaiting device return!" + MapManager.searchKey(pendencyRemoveAllowCardList.getPendencyId()));
						} else {
							try {
								HttpEntity<String> entityCardList = new HttpEntity<>(null, new HttpHeaders());
								ApiDeviceGenericModel apiDeviceGenericModel = null;
								if (deviceCollection.getRepConfiguration() == null) {
									apiDeviceGenericModel = restTemplate.postForObject(UtilsUrlService.uriConexExcludeListCard, entityCardList, ApiDeviceGenericModel.class);
								} else {
									apiDeviceGenericModel = restTemplate.postForObject(UtilsUrlService.uriConexExcludeListEmployee, entityCardList, ApiDeviceGenericModel.class);
								}
								if (Boolean.FALSE.equals(apiDeviceGenericModel.getError())) {
									MapManager.sendCachedAllPendency(deviceCollection.getId(), apiDeviceGenericModel.getPendencyId(), pendencyRemoveAllowCardList.getPendencyId());
									logger.info("Handle Device Remove Allow Card List" + "Device Pendency id return: " + apiDeviceGenericModel.getPendencyId() + " and " + pendencyRemoveAllowCardList.getPendencyId());
								} else {
									logger.error("Handle Device Remove Allow Card List" + "Error pending response from the equipment.");
									sendPendencyUpdate(pendencyRemoveAllowCardList.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
								}

							} catch (Exception e) {
								logger.error("Handle Device Remove Allow Card List" + "Error occurred during processing: " + e.getMessage());
								sendPendencyUpdate(pendencyRemoveAllowCardList.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
							}
						}
					}
				}
			} else {
				logger.error("Handle Device Remove Allow Card List" + "Failed to retrieve necessary data.");
			}
		} catch (Exception e) {
			logger.error("Handle Device Remove Allow Card List" + "Error", e);
		}
	}

	public void handleIncludeCard(WebSocketModelResponse message) {
		try {
			logger.info("Handle Device Include Card" + "Message Include Card: " + message);

			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());
			String urlSeniorAccessCard = UtilsUrlService.urlSeniorAccessCard(message.getDeviceId());
			String urlSeniorRepCredential = UtilsUrlService.urlSeniorRepCredential(message.getDeviceId());

			List<IncludeCardPendency> includeCardPendency = new ArrayList<>();

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (allPendencyBody != null) {
				logger.info("Handle Device Include Card" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<IncludeCardPendency> includeCard = allPendencyBody.getIncludeCard();
				if (!includeCard.isEmpty()) {
					includeCardPendency.addAll(includeCard);
				} else {
					logger.info("Handle Device Include Card" + "The issue could not be found.");

				}
			} else {
				logger.info("Handle Device Include Card" + "Failed to retrieve include card pendency.");
			}

			if (!includeCardPendency.isEmpty()) {
				for (IncludeCardPendency pendencyIncludeCard : includeCardPendency) {

					logger.info("Handle Device Include Card" + "check pendency: " + MapManager.searchKey(pendencyIncludeCard.getPendencyId()));
					if (MapManager.searchKey(pendencyIncludeCard.getPendencyId()) != null) {
						logger.info("Handle Device Include Card" + "Pending completed, awaiting device return!");
					} else {
						try {
							if (deviceCollection != null) {
								if (deviceCollection.getRepConfiguration() == null) {
									codeUtils.sendRequestCardList(deviceCollection, urlSeniorAccessCard, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexLoadListCard, pendencyIncludeCard.getPendencyId(), ApiMessageTypeEnum.INCLUDE_CARD);
								} else if (deviceCollection.getRepConfiguration() != null) {
									codeUtils.sendRequestPersonREP(deviceCollection, urlSeniorRepCredential, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexLoadListPerson, pendencyIncludeCard.getPendencyId(), deviceCollection.getExtensibleConfiguration());

								} else {
									sendPendencyUpdate(pendencyIncludeCard.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
									logger.error("Handle Device Include Card" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());
								}
							} else {
								sendPendencyUpdate(pendencyIncludeCard.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
								logger.error("Handle Device Include Card" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());
							}
						} catch (Exception e) {
							logger.error("Handle Device Include Card" + "Error occurred during processing: " + e.getMessage());
							sendPendencyUpdate(pendencyIncludeCard.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
						}
					}
				}
			} else {
				logger.error("Handle Device Include Card" + "Pending not found!");
			}
		} catch (Exception ex) {
			logger.info("Handle Device Include Card" + "Exception occurred: " + ex.getMessage());
		}
	}

	public void handleExcludeCard(WebSocketModelResponse message) {
		try {
			logger.info("Handle Exclude Card" + "Message Include Card: " + message);

			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());
			String urlSeniorAccessCard = UtilsUrlService.urlSeniorAccessCard(message.getDeviceId());
			String urlSeniorRepCredential = UtilsUrlService.urlSeniorRepCredential(message.getDeviceId());
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			List<ExcludeCardPendency> excludeCardPendency = new ArrayList<>();

			logger.info("Handle Exclude Card", message.toString());

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();

			if (allPendencyBody != null) {
				logger.info("Handle Exclude Card" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<ExcludeCardPendency> excludePendency = allPendencyBody.getExcludeCard();
				if (!excludePendency.isEmpty()) {
					excludeCardPendency.addAll(excludePendency);
				} else {
					logger.info("Handle Exclude Card" + "The issue could not be found.");
				}
			} else {
				logger.info("Handle Exclude Card" + "Failed to retrieve include card pendency.");
			}

			if (!excludeCardPendency.isEmpty()) {
				for (ExcludeCardPendency pendencyExcludeCard : excludeCardPendency) {
					if (MapManager.searchKey(pendencyExcludeCard.getPendencyId()) != null) {
						logger.info("Handle Device Exclude Card" + "Pending completed, awaiting device return!" + MapManager.searchKey(pendencyExcludeCard.getPendencyId()));
					} else {
						try {
							if (deviceCollection != null) {
								if (deviceCollection.getRepConfiguration() == null) {
									codeUtils.sendRequestCardList(deviceCollection, urlSeniorAccessCard, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexDeleteListCard, pendencyExcludeCard.getPendencyId(), ApiMessageTypeEnum.ALLOW_CARDS_LIST);
								} else if (deviceCollection.getRepConfiguration() != null) {
									codeUtils.sendRequestPersonREP(deviceCollection, urlSeniorRepCredential, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexDeleteListCard, pendencyExcludeCard.getPendencyId(), deviceCollection.getExtensibleConfiguration());
								} else {
									sendPendencyUpdate(pendencyExcludeCard.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
									logger.error("Handle Exclude Card" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());
								}
							} else {
								sendPendencyUpdate(pendencyExcludeCard.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
								logger.error("Handle Exclude Card" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());
							}

						} catch (Exception e) {
							logger.error("Handle Exclude Card" + "Error occurred during processing: " + e.getMessage());
							sendPendencyUpdate(pendencyExcludeCard.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
						}
					}
				}
			} else {
				logger.error("Handle Exclude Card" + "Pending not found!");
			}
		} catch (Exception ex) {
			logger.error("Handle Exclude Card" + "Exception occurred: " + ex);
		}
	}

	public void handleAllowBiometryList(WebSocketModelResponse message) {
		try {
			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

			String urlSeniorAccessBiometry = UtilsUrlService.urlSeniorAccessBiometry(message.getDeviceId());

			logger.info("Handle Allow Biometry List", message.toString());

			List<DevicePendency> devicePendencyAllowBiometryList = new ArrayList<>();

			final HttpHeaders httpHeaderSenior2 = httpHeaderSenior;
			if (httpHeaderSenior2 != null) {
				HttpEntity<String> entityHeader = new HttpEntity<>(httpHeaderSenior2);
				apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
				AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
				DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
				if (deviceCollection != null && allPendencyBody != null) {
					logger.info("Handle Allow Biometry List" + "Device Address: " + deviceCollection.getNetworkIdentification());

					List<DevicePendency> listLoadAllowBiometrys = allPendencyBody.getLoadBiometryList();

					if (!listLoadAllowBiometrys.isEmpty()) {
						for (DevicePendency pendency : listLoadAllowBiometrys) {
							logger.info("Handle Allow Biometry List" + "Pendency found: " + pendency);
							devicePendencyAllowBiometryList.add(pendency);
						}
					} else {
						logger.info("Handle Allow Biometry List" + "The issue could not be found.");
					}
				} else {
					logger.info("Handle Allow Biometry List" + "Failed to retrieve necessary data.");
				}

				if (!devicePendencyAllowBiometryList.isEmpty()) {
					for (DevicePendency pendencyAllowBiometry : devicePendencyAllowBiometryList) {
						logger.info("Handle Allow Biometry List" + "check pendency: " + MapManager.searchKey(pendencyAllowBiometry.getPendencyId()));
						if (MapManager.searchKey(pendencyAllowBiometry.getPendencyId()) != null) {
							logger.info("Handle Allow Biometry List" + "Pending completed, awaiting device return!");
						} else {
							try {

								List<AccessBiometry> biometryList = restTemplate.exchange(urlSeniorAccessBiometry, HttpMethod.GET, entityHeader, new ParameterizedTypeReference<List<AccessBiometry>>() {
								}).getBody();

								if (biometryList != null && !biometryList.isEmpty()) {

									List<ConexBiometryModel> listBiometryModel = new ArrayList<>();
									for (AccessBiometry accessBiometry : biometryList) {
										for (CardAndTechnology card : accessBiometry.getCardList()) {

											ConexBiometryModel biometryModel = new ConexBiometryModel();
											biometryModel.setRegistry(card.getCardNumber().toString());
											List<String> templateList = accessBiometry.getTemplateList();
											List<String> decodedTemplates = new ArrayList<>();

											for (String decode64 : templateList) {
												String decodeString = Utils.decodeToBase64(decode64);
												decodedTemplates.add(decodeString);
											}
											if (!decodedTemplates.isEmpty()) {

												biometryModel.setMainBiometry(decodedTemplates.get(0));
												if (decodedTemplates.size() > 1) {
													biometryModel.setAlternativeBiometry(decodedTemplates.get(1));
												} else {
													biometryModel.setAlternativeBiometry(decodedTemplates.get(0));
												}
											}
											listBiometryModel.add(biometryModel);
										}

										ExtensibleConfiguration extensible = deviceCollection.getExtensibleConfiguration();
										List<ExtensibleProperty> listPropertyExtensible = extensible.getExtensiblePropertyList();
										ExtensibleProperty filteProperty = listPropertyExtensible.stream().filter(property -> "CPF".equals(property.getKey())).findFirst().orElse(null);

										ConexBiometryListModel conexBiometryListModel = new ConexBiometryListModel();
										conexBiometryListModel.setDeviceIpAddress(deviceCollection.getNetworkIdentification());
										conexBiometryListModel.setResponsibleCpf(filteProperty == null ? "" : filteProperty.getValue());
										conexBiometryListModel.setBiometriesList(listBiometryModel);

										HttpEntity<ConexBiometryListModel> entityBiometryList = new HttpEntity<>(conexBiometryListModel);
										ApiDeviceGenericModel apiDeviceGenericModel = restTemplate.postForObject(UtilsUrlService.uriConexLoadListBiometry, entityBiometryList, ApiDeviceGenericModel.class);

										if (Boolean.FALSE.equals(apiDeviceGenericModel.getError())) {
											MapManager.sendCachedAllPendency(deviceCollection.getId(), apiDeviceGenericModel.getPendencyId(), pendencyAllowBiometry.getPendencyId());
											logger.info("Handle Allow Biometry List" + "Device Pendency id return: " + apiDeviceGenericModel.getPendencyId() + " and " + pendencyAllowBiometry.getPendencyId());
										} else {
											logger.error("Handle Allow Biometry List" + "Error pending response from the equipment.");
											sendPendencyUpdate(pendencyAllowBiometry.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
										}
									}
								} else {
									logger.error("Handle Allow Biometry List" + "Empty Biometrics or People list. ");
									sendPendencyUpdate(pendencyAllowBiometry.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
								}
							} catch (Exception e) {
								logger.error("Handle Allow Biometry List" + "Error when handling Biometry List load model", e);
								sendPendencyUpdate(pendencyAllowBiometry.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
							}
						}
					}
				} else {
					logger.error("Handle Allow Biometry List" + "Pending not found!");
				}
			} else {
			}
		} catch (Exception ex) {
			logger.error("Handle Allow Biometry List" + "Exception occurred", ex);
		}
	}

	public void handleIncludeBiometry(WebSocketModelResponse message) {
		try {
			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

			logger.info("Handle Include Biometry", message.toString());

			List<IncludeBiometryPendency> devicePendencyAllowBiometryList = new ArrayList<>();
			HttpEntity<String> entityResponse = new HttpEntity<>(httpHeaderSenior);

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (deviceCollection != null && allPendencyBody != null) {
				logger.info("Handle Include Biometry" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<IncludeBiometryPendency> includeBiometrys = allPendencyBody.getIncludeBiometry();

				if (!includeBiometrys.isEmpty()) {
					for (IncludeBiometryPendency pendency : includeBiometrys) {
						logger.info("Handle Include Biometry" + "Pendency found: " + pendency);
						devicePendencyAllowBiometryList.add(pendency);
					}
				} else {
					logger.info("Handle Include Biometry" + "The issue could not be found.");
				}
			} else {
				logger.info("Handle Include Biometry" + "Failed to retrieve necessary data.");
			}

			if (!devicePendencyAllowBiometryList.isEmpty()) {
				for (IncludeBiometryPendency pendencyIncludeBiometry : devicePendencyAllowBiometryList) {

					logger.info("Handle Include Biometry" + "check pendency: " + MapManager.searchKey(pendencyIncludeBiometry.getPendencyId()));
					if (MapManager.searchKey(pendencyIncludeBiometry.getPendencyId()) != null) {
						logger.info("Handle Include Biometry" + "Pending completed, awaiting device return!");
					} else {
						try {
							String urlSeniorRepCredential = UtilsUrlService.urlSeniorRepCredential(pendencyIncludeBiometry.getManagerDeviceId().toString());

							List<PersonRep> listPersonRep = restTemplate.exchange(urlSeniorRepCredential, HttpMethod.GET, entityResponse, new ParameterizedTypeReference<List<PersonRep>>() {
							}).getBody();

							if (listPersonRep != null) {
								ExtensibleConfiguration extensible = deviceCollection.getExtensibleConfiguration();
								List<ExtensibleProperty> listPropertyExtensible = extensible.getExtensiblePropertyList();
								ExtensibleProperty filteProperty = listPropertyExtensible.stream().filter(property -> "CPF".equals(property.getKey())).findFirst().orElse(null);

								ConexBiometryIncludeModel biometryModel = new ConexBiometryIncludeModel();
								Biometry encodedString = pendencyIncludeBiometry.getBiometry();
								List<ConexBiometryModel> conexBiometryList = new ArrayList<>();

								ConexBiometryModel conexBiometry = new ConexBiometryModel();

								for (CardAndTechnology card : pendencyIncludeBiometry.getCardList()) {

									List<String> templateList = encodedString.getTemplateList();
									List<String> decodedTemplates = new ArrayList<>();

									for (String decode64 : templateList) {
										String decodedBytes = Utils.decodeToBase64(decode64);
										decodedTemplates.add(decodedBytes);
									}
									if (!decodedTemplates.isEmpty()) {

										conexBiometry.setMainBiometry(decodedTemplates.get(0));
										if (decodedTemplates.size() > 1) {
											conexBiometry.setAlternativeBiometry(decodedTemplates.get(1));
										} else {
											conexBiometry.setAlternativeBiometry(null);
										}
									}
									conexBiometry.setRegistry(card.getCardNumber().toString());
									conexBiometryList.add(conexBiometry);

								}
								biometryModel.setBiometriesList(conexBiometryList);
								biometryModel.setDeviceIpAddress(deviceCollection.getNetworkIdentification());
								biometryModel.setResponsibleCpf(filteProperty == null ? "" : filteProperty.getValue());

								System.out.println(new Gson().toJson(biometryModel));

								logger.info("Handle Include Biometry" + "Include Biometry: " + biometryModel);
								HttpEntity<ConexBiometryIncludeModel> entityBiometryList = new HttpEntity<>(biometryModel);
								ApiDeviceGenericModel apiDeviceGenericModel = restTemplate.exchange(UtilsUrlService.uriConexLoadListBiometry, HttpMethod.POST, entityBiometryList, ApiDeviceGenericModel.class).getBody();

								if (Boolean.FALSE.equals(apiDeviceGenericModel.getError())) {
									MapManager.sendCachedAllPendency(deviceCollection.getId(), apiDeviceGenericModel.getPendencyId(), pendencyIncludeBiometry.getPendencyId());
									logger.info("Handle Include Biometry" + "Device Pendency id return: " + apiDeviceGenericModel.getPendencyId() + " and " + pendencyIncludeBiometry.getPendencyId());
								} else {
									logger.info("Handle Include Biometry" + "Error pending response from the equipment.");
									sendPendencyUpdate(pendencyIncludeBiometry.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
								}
							}
						} catch (Exception e) {
							logger.error("Handle Include Biometry" + "Error when handling Biometry List load model", e);
							sendPendencyUpdate(pendencyIncludeBiometry.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
						}
					}
				}
			} else {
				logger.error("Handle Include Biometry" + "Pending not found!");
			}
		} catch (

		Exception ex) {
			logger.error("Handle Include Biometry" + "Exception occurred", ex);
		}
	}

	public void handleRemoveBiometryList(WebSocketModelResponse message) {
		try {
			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

			logger.info("Handle Remove Biometry List", message.toString());

			List<DevicePendency> devicePendencyRemoveBiometryList = new ArrayList<>();

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));
			if (deviceCollection != null && allPendencyBody != null) {
				logger.info("Handle Remove Biometry List" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<DevicePendency> listRemoveAllowBiometrys = allPendencyBody.getRemoveBiometryList();

				if (!listRemoveAllowBiometrys.isEmpty()) {
					for (DevicePendency pendency : listRemoveAllowBiometrys) {
						logger.info("Handle Remove Biometry List" + "Pendency found: " + pendency);
						devicePendencyRemoveBiometryList.add(pendency);
					}
				} else {
					logger.info("Handle Remove Biometry List" + "The issue could not be found.");
				}
			} else {
				logger.info("Handle Remove Biometry List" + "Failed to retrieve necessary data.");
			}

			if (!devicePendencyRemoveBiometryList.isEmpty()) {
				for (DevicePendency pendencyRemoveBiometry : devicePendencyRemoveBiometryList) {
					if (MapManager.searchKey(pendencyRemoveBiometry.getPendencyId()) != null) {
						logger.info("Handle Remove Biometry List" + "Pending completed, awaiting device return!" + MapManager.searchKey(pendencyRemoveBiometry.getPendencyId()));
					} else {
						try {
							ExtensibleConfiguration extensible = deviceCollection.getExtensibleConfiguration();
							List<ExtensibleProperty> listPropertyExtensible = extensible.getExtensiblePropertyList();
							ExtensibleProperty filteProperty = listPropertyExtensible.stream().filter(property -> "CPF".equals(property.getKey())).findFirst().orElse(null);

							ApiCPFDeviceRequestModel cpfDevice = new ApiCPFDeviceRequestModel();
							cpfDevice.setDeviceIpAddress(deviceCollection.getNetworkIdentification());
							cpfDevice.setResponsibleCpf(filteProperty.getValue());

							HttpEntity<ApiCPFDeviceRequestModel> entityBiometryList = new HttpEntity<>(cpfDevice);
							ApiDeviceGenericModel apiDeviceGenericModel = restTemplate.postForObject(UtilsUrlService.uriConexExcludeListBiometry, entityBiometryList, ApiDeviceGenericModel.class);

							if (Boolean.FALSE.equals(apiDeviceGenericModel.getError())) {
								MapManager.sendCachedAllPendency(deviceCollection.getId(), apiDeviceGenericModel.getPendencyId(), pendencyRemoveBiometry.getPendencyId());
								logger.info("Handle Remove Biometry List" + "Device Pendency id return: " + apiDeviceGenericModel.getPendencyId() + " and " + pendencyRemoveBiometry.getPendencyId());
							} else {
								logger.error("Handle Remove Biometry List" + "Error in request for equipment");
								sendPendencyUpdate(pendencyRemoveBiometry.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
							}

						} catch (Exception e) {
							logger.error("Handle Remove Biometry List" + "Error when handling Biometry List load model", e);
							sendPendencyUpdate(pendencyRemoveBiometry.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
						}
					}
				}
			} else {
				logger.error("Handle Remove Biometry List" + "Pending not found!");
			}
		} catch (Exception ex) {
			logger.error("Handle Remove Biometry List" + "Exception occurred", ex);
		}
	}

	public void handleExcludeBiometry(WebSocketModelResponse message) {
		try {

			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());
			String urlSeniorAccessCard = UtilsUrlService.urlSeniorAccessCard(message.getDeviceId());
			String urlSeniorRepCredential = UtilsUrlService.urlSeniorRepCredential(message.getDeviceId());

			List<ExcludeBiometryPendency> excludeBiometryPendency = new ArrayList<>();

			logger.info("Handle Exclude Biometry", message.toString());

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (allPendencyBody != null) {
				logger.info("Handle Exclude Biometry" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<ExcludeBiometryPendency> excludeCard = allPendencyBody.getExcludeBiometry();
				if (!excludeCard.isEmpty()) {
					excludeBiometryPendency.addAll(excludeCard);
				} else {
					logger.info("Handle Exclude Biometry" + "The issue could not be found.");
				}
			} else {
				logger.info("Handle Exclude Biometry" + "Failed to retrieve include card pendency.");
			}

			if (!excludeBiometryPendency.isEmpty()) {
				for (ExcludeBiometryPendency pendencyExcludeBiometry : excludeBiometryPendency) {
					if (MapManager.searchKey(pendencyExcludeBiometry.getPendencyId()) != null) {
						logger.info("Handle Exclude Biometry" + "Pending completed, awaiting device return!" + MapManager.searchKey(pendencyExcludeBiometry.getPendencyId()));
					} else {
						try {
							if (deviceCollection != null) {
								if (deviceCollection.getRepConfiguration() == null) {
									codeUtils.sendRequestCardList(deviceCollection, urlSeniorAccessCard, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexDeleteListCard, pendencyExcludeBiometry.getPendencyId(), ApiMessageTypeEnum.EXCLUDE_BIOMETRY);
								} else if (deviceCollection.getRepConfiguration() != null) {
									codeUtils.sendRequestPersonREP(deviceCollection, urlSeniorRepCredential, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexDeleteListCard, pendencyExcludeBiometry.getPendencyId(), null);
								} else {
									sendPendencyUpdate(pendencyExcludeBiometry.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
									logger.error("Handle Exclude Biometry" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());
								}
							} else {
								sendPendencyUpdate(pendencyExcludeBiometry.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
								logger.error("Handle Exclude Biometry" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());
							}

						} catch (Exception e) {
							logger.error("Handle Exclude Biometry" + "Error occurred during processing: " + e.getMessage());
							sendPendencyUpdate(pendencyExcludeBiometry.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
						}
					}
				}
			} else {
				logger.error("Handle Exclude Biometry" + "Pending not found!");
			}
		} catch (Exception ex) {
			logger.error("Handle Exclude Biometry" + "Exception occurred: " + ex);
		}
	}

	public void handleUpdateRepPerson(WebSocketModelResponse message) {
		try {
			logger.info("Handle Update Rep Person" + "Message Update Person Card: " + message);

			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

			List<UpdatePersonREPPendency> updatePersonPendency = new ArrayList<>();

			logger.info("Handle Update Rep Person", message.toString());

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (allPendencyBody != null) {
				logger.info("Handle Update Rep Person" + "Device Address: " + deviceCollection.getNetworkIdentification());

				List<UpdatePersonREPPendency> updatePerson = allPendencyBody.getUpdatePersonREP();
				if (!updatePerson.isEmpty()) {
					updatePersonPendency.addAll(updatePerson);
				} else {
					logger.info("Handle Update Rep Person" + "The issue could not be found.");
				}
			} else {
				logger.info("Handle Update Rep Person" + "Failed to retrieve include card pendency.");
			}

			if (!updatePersonPendency.isEmpty()) {
				for (UpdatePersonREPPendency pendencyUpdatePerson : updatePersonPendency) {
					if (MapManager.searchKey(pendencyUpdatePerson.getPendencyId()) == null) {
						if (pendencyUpdatePerson.getOperationId() == OperationIdEnum.INCLUDE_CARD) {
							codeUtils.sendPersonCardREP(deviceCollection, pendencyUpdatePerson, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexAddEmployeesList, pendencyUpdatePerson.getPendencyId(), deviceCollection.getExtensibleConfiguration());
						} else if (pendencyUpdatePerson.getOperationId() == OperationIdEnum.EXCLUDE_CARD) {
							codeUtils.sendExcludeByCardPersonREP(deviceCollection, pendencyUpdatePerson, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexRemoveEmployees, pendencyUpdatePerson.getPendencyId(), deviceCollection.getExtensibleConfiguration());
						} else if (pendencyUpdatePerson.getOperationId() == OperationIdEnum.EXCLUDE_PERSON) {
							codeUtils.sendExcludeByCardPersonREP(deviceCollection, pendencyUpdatePerson, deviceCollection.getNetworkIdentification(), UtilsUrlService.uriConexRemoveEmployees, pendencyUpdatePerson.getPendencyId(), deviceCollection.getExtensibleConfiguration());
						} else {
							sendPendencyUpdate(pendencyUpdatePerson.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
							logger.error("Handle Update Rep Person" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());
						}
					} else {
						logger.error("Handle Update Rep Person" + "Pending completed, awaiting device return!" + MapManager.searchKey(pendencyUpdatePerson.getPendencyId()));
					}
				}

			} else {
				logger.error("Handle Update Rep Person" + "Pending not found!");
			}
		} catch (Exception ex) {
			logger.error("Handle Update Rep Person" + "Exception occurred: " + ex);
		}
	}

	public void handleManufatureRep(WebSocketModelResponse message) {
		logger.info("Handle Manufature Rep" + "Message received at UpdateFirmware: " + message);

		try {
			String urlSeniorPendency = UtilsUrlService.urlSeniorPendencyId(message.getDeviceId());

			List<ManufacturerUpdatedPendency> updateManufacturerPendency = new ArrayList<>();

			logger.info("Handle Manufature Rep", message.toString());

			apiManagerDeviceAllPendencyModel = CodeUtilsSenior.sendPendency(urlSeniorPendency);
			AllPendency allPendencyBody = apiManagerDeviceAllPendencyModel.getAllPendency();
			DevicesCollection deviceCollection = deviceController.findDevicesByIdOrIp(Long.valueOf(message.getDeviceId()));

			if (deviceCollection != null) {

				if (allPendencyBody != null) {
					logger.info("Handle Manufature Rep" + "Device Address: " + deviceCollection.getNetworkIdentification());

					List<ManufacturerUpdatedPendency> updateManufacturer = allPendencyBody.getManufacturerUpdated();
					if (!updateManufacturer.isEmpty()) {
						updateManufacturerPendency.addAll(updateManufacturer);
					} else {
						logger.info("Handle Manufature Rep" + "The issue could not be found.");
					}
				} else {
					logger.info("Handle Manufature Rep" + "Failed to retrieve include card pendency.");
				}

				if (!updateManufacturerPendency.isEmpty()) {
					for (ManufacturerUpdatedPendency pendencyManufaturePerson : updateManufacturerPendency) {
						if (MapManager.searchKey(pendencyManufaturePerson.getPendencyId()) != null) {
							logger.info("Handle Manufature Rep" + "Pending completed, awaiting device return!" + MapManager.searchKey(pendencyManufaturePerson.getPendencyId()));
						} else {
							try {

								RepConfiguration repConfig = deviceCollection.getRepConfiguration();
								ExtensibleConfiguration extensible = deviceCollection.getExtensibleConfiguration();
								List<ExtensibleProperty> listPropertyExtensible = extensible.getExtensiblePropertyList();
								ExtensibleProperty filteProperty = listPropertyExtensible.stream().filter(property -> "CPF".equals(property.getKey())).findFirst().orElse(null);

								ConexEmployeerModel employeerModel = new ConexEmployeerModel();
								employeerModel.setName(repConfig.getCompanyName());
								employeerModel.setAddress(repConfig.getAddress());
								employeerModel.setDeviceIpAddress(deviceCollection.getNetworkIdentification());
								employeerModel.setCei(repConfig.getCei() == null ? "" : repConfig.getCei());
								employeerModel.setResponsibleCpf(filteProperty.getValue() == null ? "" : filteProperty.getValue());
								if (repConfig.getCnpj() == null) {
									employeerModel.setDocType(DocTypeEnum.CPF);
									employeerModel.setDoc(repConfig.getCpf() == null ? "00000000" : repConfig.getCpf());
								} else if (repConfig.getCnpj() != null) {
									employeerModel.setDocType(DocTypeEnum.CNPJ);
									employeerModel.setDoc(repConfig.getCnpj() == null ? "00000000" : repConfig.getCnpj());
								} else
									logger.error("Handle Manufature Rep" + "Error receiving company data for conex.");

								HttpEntity<ConexEmployeerModel> entityEmployeesList = new HttpEntity<>(employeerModel);
								try {

									ApiDeviceGenericModel apiDeviceGeneric = restTemplate.postForObject(UtilsUrlService.urlConexSetEmployeer, entityEmployeesList, ApiDeviceGenericModel.class);
									if (Boolean.FALSE.equals(apiDeviceGeneric.getError())) {
										MapManager.sendCachedAllPendency(deviceCollection.getId(), apiDeviceGeneric.getPendencyId(), pendencyManufaturePerson.getPendencyId());
										logger.info("Handle Manufature Rep" + "Device Pendency id return: " + apiDeviceGeneric.getPendencyId() + " and " + pendencyManufaturePerson.getPendencyId());
									} else {
										logger.info("Handle Manufature Rep" + "Error pending response from the equipment.");
										sendPendencyUpdate(pendencyManufaturePerson.getPendencyId(), 343, OperationEnum.KEEP_PENDENCY);
										logger.error("Handle Manufature Rep" + "Error identifying the type of load, Device:" + message.getDeviceId() + ", Pendency: " + message.getPendencyType());

									}
								} catch (Exception e) {
									logger.error("Handle Manufature Rep" + "Error in request for equipment.", e);
									sendPendencyUpdate(pendencyManufaturePerson.getPendencyId(), 344, OperationEnum.KEEP_PENDENCY);
								}

							} catch (Exception e) {
								logger.error("Handle Manufature Rep" + "Error occurred during processing: " + e.getMessage());
								sendPendencyUpdate(pendencyManufaturePerson.getPendencyId(), 345, OperationEnum.KEEP_PENDENCY);
							}
						}
					}
				} else {
					logger.error("Handle Manufature Rep" + "Pending not found!");
				}
			} else {
				logger.error("Handle Manufature Rep" + "Device not found!");
			}
		} catch (Exception ex) {
			logger.error("Handle Manufature Rep" + "Exception occurred: " + ex);
		}

	};

	public static void sendPendencyUpdate(Long pendencyId, Integer errorCode, OperationEnum operation) {

		try {
			List<PendencyUpdated> listResponsePendency = new ArrayList<>();
			PendencyUpdated setPendencyUpdate = new PendencyUpdated();
			setPendencyUpdate.setPendencyId(pendencyId);
			setPendencyUpdate.setErrorCode(errorCode);
			setPendencyUpdate.setOperation(operation);

			listResponsePendency.add(setPendencyUpdate);

			HttpEntity<List<PendencyUpdated>> entity = new HttpEntity<>(listResponsePendency, UtilsHttpHeaderSenior.sendHttpSenior());

			ResponseEntity<String> execute = restTemplate.exchange(UtilsUrlService.urlResponsePendencyUpdate, HttpMethod.POST, entity, String.class);

			HttpStatus statusCodeResponse = execute.getStatusCode();

			logger.info("Send Pendency Update" + "Pendency update success: " + statusCodeResponse);

		} catch (Exception e) {
			logger.error("Send Pendency Update" + "Error updating pendency: " + e.getMessage());
		}
	}

	// -------------------------------------------------------------------------
	// TODO Develop
	// -------------------------------------------------------------------------

	// Function with built structure - Test
	public void handleBlockDevice(WebSocketModelResponse message) {
		logger.info("Handle Block Device" + "Message received at BlockDevice: " + message);

		// UtilsUrlService.urlDeviceId( // message.getDeviceId());
		// UtilsUrlService.urlSeniorPendency( // message.getDeviceId());
		// String UtilsUrlService.urlConexBlockDevice = String.format("%s/.....",
		// UtilsUrlService.urlConex.trim());
		//
		// LoggerUtils.logSeniorInfo(message.toString());
		//
		// apiManagerDeviceAllPendencyModel =
		// CodeUtilsSenior.sendPendency(
		// urlSeniorPendency);
		// AllPendency allPendencyBody =
		// apiManagerDeviceAllPendencyModel.getAllPendency();
		//
		// LoggerUtils.logSeniorInfo("Device Address: " +
		// deviceCollection.getNetworkIdentification());
		// List<BlockDevicePendency> blockDevicePendency = new ArrayList<>();
		// for (BlockDevicePendency pendency : allPendencyBody.getBlockDevice()) {
		// LoggerUtils.logSeniorInfo("Pendency found: " + pendency);
		// blockDevicePendency.add(pendency);
		// }
		//
		// if (!(blockDevicePendency.isEmpty())) {
		//
		// BlockDevicePendency deviceBlock =
		// blockDevicePendency.stream().filter(pendency ->
		// pendency.getManagerDeviceId().equals(deviceCollection.getId())).findFirst().orElse(null);
		//
		// ConexDeviceBlockModel emergencyDevice = new ConexDeviceBlockModel();
		// emergencyDevice.setDeviceIpAddress(deviceCollection.getNetworkIdentification());
		// emergencyDevice.setBlockType(BlockDeviceEnum.DEVICE_BLOCK);
		//
		// HttpEntity<ConexDeviceBlockModel> entityComandDeviceStatus = new
		// HttpEntity<>(emergencyDevice, UtilsHttpHeaderSenior.sendHttpSenior());
		//
		// try {
		// DeviceGenericModel deviceGenericModel =
		// restTemplate.postForObject(UtilsUrlService.urlConexBlockDevice,
		// entityComandDeviceStatus,
		// DeviceGenericModel.class);
		//
		// if (deviceGenericModel != null) {
		// MapManager.sendCachedAllPendency(deviceGenericModel.getPendecyId(),
		// deviceBlock.getPendencyId());
		// LoggerUtils.logSeniorInfo("Results: " + blockDevicePendency);
		// } else {
		// LoggerUtils.logSeniorInfo("Error receiving Date Time return message!");
		// postPendencyUpdate(deviceBlock.getPendencyId(), 345,
		// OperationEnum.KEEP_PENDENCY);
		// }
		//
		// } catch (Exception e) {
		// LoggerUtils.logSeniorInfo("Error in request for equipment: " +
		// e.getMessage());
		// postPendencyUpdate(deviceBlock.getPendencyId(), 345,
		// OperationEnum.KEEP_PENDENCY);
		// }
		// } else {
		// LoggerUtils.logSeniorInfo("Pending list is empty! Data received: " +
		// blockDevicePendency.toString());
		// }

	};

	// Function with built structure - Test
	public void handleUnblockDevice(WebSocketModelResponse message) {
		logger.info("Handle Unblock Device" + "Message received at UnblockDevice: " + message);
		//
		// UtilsUrlService.urlDeviceId( // message.getDeviceId());
		// UtilsUrlService.urlSeniorPendency( // message.getDeviceId());
		//
		// LoggerUtils.logSeniorInfo(message.toString());
		//
		// apiManagerDeviceAllPendencyModel =
		// CodeUtilsSenior.sendPendency(
		// urlSeniorPendency);
		// AllPendency allPendencyBody =
		// apiManagerDeviceAllPendencyModel.getAllPendency();
		//
		// LoggerUtils.logSeniorInfo("Device Address: " +
		// deviceCollection.getNetworkIdentification());
		// List<UnblockDevicePendency> unblockDevicePendency = new ArrayList<>();
		// for (UnblockDevicePendency pendency : allPendencyBody.getUnblockDevice()) {
		// LoggerUtils.logSeniorInfo("Pendency found: " + pendency);
		// unblockDevicePendency.add(pendency);
		// }
		//
		// if (!(unblockDevicePendency.isEmpty())) {
		//
		// UnblockDevicePendency deviceUnblock =
		// unblockDevicePendency.stream().filter(pendency ->
		// pendency.getManagerDeviceId().equals(deviceCollection.getId())).findFirst().orElse(null);
		//
		// ConexDeviceBlockModel emergencyDevice = new ConexDeviceBlockModel();
		// emergencyDevice.setDeviceIpAddress(deviceCollection.getNetworkIdentification());
		// emergencyDevice.setBlockType(BlockDeviceEnum.DEVICE_BLOCK);
		//
		// HttpEntity<ConexDeviceBlockModel> entityComandDeviceStatus = new
		// HttpEntity<>(emergencyDevice, UtilsHttpHeaderSenior.sendHttpSenior());
		//
		// try {
		// DeviceGenericModel deviceGenericModel =
		// restTemplate.postForObject(urlSeniorPendency, entityComandDeviceStatus,
		// DeviceGenericModel.class);
		//
		// if (deviceGenericModel != null) {
		//
		// MapManager.sendCachedAllPendency(deviceGenericModel.getPendecyId(),
		// deviceUnblock.getPendencyId());
		// LoggerUtils.logSeniorInfo("Results: " + unblockDevicePendency);
		// } else {
		// LoggerUtils.logSeniorInfo("Error receiving Date Time return message!");
		// postPendencyUpdate(deviceUnblock.getPendencyId(), 345,
		// OperationEnum.KEEP_PENDENCY);
		// }
		//
		// } catch (Exception e) {
		// LoggerUtils.logSeniorInfo("Error in request for equipment: " +
		// e.getMessage());
		// postPendencyUpdate(deviceUnblock.getPendencyId(), 345,
		// OperationEnum.KEEP_PENDENCY);
		// }
		// } else {
		// LoggerUtils.logSeniorInfo("Pending list is empty! Data received: " +
		// unblockDevicePendency.toString());
		// }

	};

	//
	public void handleUpdateFirmware(WebSocketModelResponse message) {
		logger.info("Handle Update Firmware" + "Message received at UpdateFirmware: " + message);

		// UtilsUrlService.urlDeviceId( // message.getDeviceId());
		// String urlSeniorPendency = String.format("%s/pendency/device/%s" +
		// message.getDeviceId();
		// ManagerDevice device;
		//
		// LoggerUtils.logSeniorInfo(message.toString());
		//
		// HttpEntity<String> entityHeander = new
		// HttpEntity<>(httpHeaderSenior));
		//
		// ResponseEntity<ManagerDevice> deviceCollection =
		// restTemplate.exchange(uriDeviceId, HttpMethod.GET,
		// entityHeander, ManagerDevice.class);
		//
		// device = deviceCollection.getBody();
		//
		// ResponseEntity<AllPendency> allPendencyBody =
		// restTemplate.exchange(urlSeniorPendency, HttpMethod.GET,
		// entityHeander, AllPendency.class);
		// AllPendency allPendency = allPendencyBody.getBody();
		//
		// LoggerUtils.logSeniorInfo("Device Address: " +
		// device.getNetworkIdentification());
		// List<DevicePendency> blockDevicePendency = new ArrayList<>();
		// for (DevicePendency pendency : allPendency.getUpdateFirmware()) {
		// LoggerUtils.logSeniorInfo("Pendency found: " + pendency);
		// blockDevicePendency.add(pendency);
		// }

	};

	//
	public void handleDeviceInputStatus(WebSocketModelResponse message) {
		logger.info("Handle Device Input Status" + "Message received at DeviceInputStatus: " + message);
		//
		// UtilsUrlService.urlDeviceId( // message.getDeviceId());
		// String urlSeniorPendency = String.format("%s/pendency/device/%s" +
		// message.getDeviceId();
		// ManagerDevice device;
		//
		// LoggerUtils.logSeniorInfo(message.toString());
		//
		// HttpEntity<String> entityHeander = new
		// HttpEntity<>(httpHeaderSenior));
		//
		// ResponseEntity<ManagerDevice> deviceCollection =
		// restTemplate.exchange(uriDeviceId, HttpMethod.GET,
		// entityHeander, ManagerDevice.class);
		//
		// device = deviceCollection.getBody();
		//
		// ResponseEntity<AllPendency> allPendencyBody =
		// restTemplate.exchange(urlSeniorPendency, HttpMethod.GET,
		// entityHeander, AllPendency.class);
		// AllPendency allPendency = allPendencyBody.getBody();
		//
		// LoggerUtils.logSeniorInfo("Device Address: " +
		// device.getNetworkIdentification());
		// List<DevicePendency> blockDevicePendency = new ArrayList<>();
		// for (DevicePendency pendency : allPendency.getInputStatus()) {
		// LoggerUtils.logSeniorInfo("Pendency found: " + pendency);
		// blockDevicePendency.add(pendency);
		// }

	};

	//
	public void handleCredentialFacialList(WebSocketModelResponse message) {
		logger.info("Handle Credential Facial List" + "Message received at CredentialFacialList: " + message);

	};

	//
	public void handleLoadHolidayList(WebSocketModelResponse message) {
		logger.info("Handle Load Holiday List" + "Message received at LoadHolidayList: " + message);
		//
		// UtilsUrlService.urlDeviceId( // message.getDeviceId());
		// UtilsUrlService.urlSeniorPendency( // message.getDeviceId());
		//
		// LoggerUtils.logSeniorInfo(message.toString());
		//
		// List<LoadHolidayListPendency> listHolidayPendency = new ArrayList<>();
		// HttpEntity<String> entityHeader = new
		// HttpEntity<>(httpHeaderSenior));
		// ManagerDevice deviceCollection = null;
		//
		// try {
		// deviceCollection = restTemplate.exchange(uriDeviceId, HttpMethod.GET,
		// entityHeader, ManagerDevice.class).getBody();
		//
		// AllPendency AllPendency allPendencyBody =
		// restTemplate.exchange(urlSeniorPendency,
		// HttpMethod.GET, entityHeader, AllPendency.class).getBody();
		//
		// LoggerUtils.logSeniorInfo("Device Address: " +
		// deviceCollection.getNetworkIdentification());
		// List<LoadHolidayListPendency> listLoadAllowCard =
		// allPendencyBody.getLoadHolidayList();
		//
		// if (!listLoadAllowCard.isEmpty()) {
		//
		// for (LoadHolidayListPendency pendency : listLoadAllowCard) {
		// LoggerUtils.logSeniorInfo("Pendency found: " + pendency);
		// listHolidayPendency.add(pendency);
		// }
		// } else {
		// LoggerUtils.logSeniorInfo("The issue could not be found. ");
		// }
		//
		// if (!(listHolidayPendency.isEmpty())) {
		//
		// for (LoadHolidayListPendency pendencyHandler : listHolidayPendency) {
		//
		// try {
		//
		// List<HolidayList> holidayListRequest = restTemplate.exchange(null,
		// HttpMethod.GET, entityHeader, new
		// ParameterizedTypeReference<List<HolidayList>>() {
		// }).getBody();
		// List<String> listHolidayString = new ArrayList<>();
		//
		// for (HolidayList listHoliday : holidayListRequest) {
		// listHolidayString.addAll(listHoliday.getHoliday());
		// }
		//
		// // mapa do Modelo para Conex
		//
		// HttpEntity<String> entityListBiometry = new HttpEntity<>(null);
		// try {
		// WebhookGenericModel apiDeviceGeneric = restTemplate.postForObject(null,
		// entityListBiometry, WebhookGenericModel.class);
		//
		// MapManager.sendCachedAllPendency(apiDeviceGeneric.getPendencyId(),
		// pendencyHandler.getPendencyId());
		// LoggerUtils.logSeniorInfo("Device Pendency id return: " +
		// apiDeviceGeneric.getPendencyId() + " and " +
		// pendencyHandler.getPendencyId());
		// } catch (Exception e) {
		// LoggerUtils.logSeniorInfo("Error in request for equipment: " +
		// e.getMessage());
		// postPendencyUpdate(pendencyHandler.getPendencyId(), 345,
		// OperationEnum.KEEP_PENDENCY);
		// }
		//
		// } catch (Exception e) {
		// LoggerUtils.logSeniorInfo("Error when handling Card List load model: " +
		// e.getMessage());
		// postPendencyUpdate(pendencyHandler.getPendencyId(), 345,
		// OperationEnum.KEEP_PENDENCY);
		// }
		//
		// }
		// }
		// } catch (Exception e) {
		// LoggerUtils.logSeniorInfo("Error in request for senior Device/Pendency: " +
		// e.getMessage());
		//
		// }

	};

	//
	public void handleRemoveHolidayList(WebSocketModelResponse message) {
		logger.info("Handle Remove Holiday List" + "Message received at RemoveHolidayList: " + message);
		// UtilsUrlService.urlDeviceId( // message.getDeviceId());
		// String urlSeniorPendency = String.format("%s/pendency/device/%s" +
		// message.getDeviceId();
		// ManagerDevice device;
		//
		// LoggerUtils.logSeniorInfo(message.toString());
		//
		// HttpEntity<String> entityHeander = new
		// HttpEntity<>(httpHeaderSenior));
		//
		// ResponseEntity<ManagerDevice> deviceCollection =
		// restTemplate.exchange(uriDeviceId, HttpMethod.GET,
		// entityHeander, ManagerDevice.class);
		//
		// device = deviceCollection.getBody();
		//
		// ResponseEntity<AllPendency> allPendencyBody =
		// restTemplate.exchange(urlSeniorPendency, HttpMethod.GET,
		// entityHeander, AllPendency.class);
		// AllPendency allPendency = allPendencyBody.getBody();
		//
		// LoggerUtils.logSeniorInfo("Device Address: " +
		// device.getNetworkIdentification());
		// List<DevicePendency> blockDevicePendency = new ArrayList<>();
		// for (DevicePendency pendency : allPendency.getRemoveHolidayList()) {
		// LoggerUtils.logSeniorInfo("Pendency found: " + pendency);
		// blockDevicePendency.add(pendency);
		// }

	};

	//
	public void handleCollectEvents(WebSocketModelResponse message) {
		logger.info("Handle Collect Events" + "Message received at CollectEvents: " + message);

		// UtilsUrlService.urlDeviceId( // message.getDeviceId());
		// String urlSeniorPendency = String.format("%s/pendency/device/%s" +
		// message.getDeviceId();
		// ManagerDevice device;
		//
		// LoggerUtils.logSeniorInfo(message.toString());
		//
		// HttpEntity<String> entityHeander = new
		// HttpEntity<>(httpHeaderSenior));
		//
		// ResponseEntity<ManagerDevice> deviceCollection =
		// restTemplate.exchange(uriDeviceId, HttpMethod.GET,
		// entityHeander, ManagerDevice.class);
		//
		// device = deviceCollection.getBody();
		//
		// ResponseEntity<AllPendency> allPendencyBody =
		// restTemplate.exchange(urlSeniorPendency, HttpMethod.GET,
		// entityHeander, AllPendency.class);
		// AllPendency allPendency = allPendencyBody.getBody();
		//
		// LoggerUtils.logSeniorInfo("Device Address: " +
		// device.getNetworkIdentification());
		// List<CollectEventPendency> blockDevicePendency = new ArrayList<>();
		// for (CollectEventPendency pendency : allPendency.getCollectEvent()) {
		// LoggerUtils.logSeniorInfo("Pendency found: " + pendency);
		// blockDevicePendency.add(pendency);
		// }

	};

}
