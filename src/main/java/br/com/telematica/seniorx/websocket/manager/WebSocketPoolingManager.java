package br.com.telematica.seniorx.websocket.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

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
import br.com.telematica.seniorx.model.PersonAreaUpdatedPendency;
import br.com.telematica.seniorx.model.UnblockDevicePendency;
import br.com.telematica.seniorx.websocket.WebSocketPoolingHandler;

@Configuration
public class WebSocketPoolingManager {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketPoolingManager.class);
	AllPendency allPendency = null;
	private final WebSocketPoolingHandler poolingHandler;

	public WebSocketPoolingManager(WebSocketPoolingHandler poolingHandler) {
		super();
		this.poolingHandler = poolingHandler;
		// Thread.currentThread().setName("WebSocket Pooling Manager");
	}

	public static void removePendencyMessageWebSocket() {
		try {

			AllPendency allPendencyBody = new AllPendency();

			List<DevicePendency> pendencyFacialList = allPendencyBody.getLoadCredentialFacialList();
			WebSocketManager.handleDevicePendency(pendencyFacialList, allPendencyBody);
			List<DevicePendency> pendencyRemoveHolidayList = allPendencyBody.getRemoveHolidayList();
			WebSocketManager.handleDevicePendency(pendencyRemoveHolidayList, allPendencyBody);
			List<DevicePendency> pendencyInputStatus = allPendencyBody.getInputStatus();
			WebSocketManager.handleDevicePendency(pendencyInputStatus, allPendencyBody);
			List<DevicePendency> pendencyUpdateFirmware = allPendencyBody.getUpdateFirmware();
			WebSocketManager.handleDevicePendency(pendencyUpdateFirmware, allPendencyBody);
			List<LoadHolidayListPendency> pendencyHolidayList = allPendencyBody.getLoadHolidayList();
			WebSocketManager.handleLoadHolidayListPendency(pendencyHolidayList, allPendencyBody);
			List<ActiveDeviceOutputPendency> pendencyActivateDeviceOutput = allPendencyBody.getActivateDeviceOutput();
			WebSocketManager.handleActiveDeviceOutputPendency(pendencyActivateDeviceOutput, allPendencyBody);
			List<CollectEventPendency> pendencyCollectEvent = allPendencyBody.getCollectEvent();
			WebSocketManager.handleCollectEventPendency(pendencyCollectEvent, allPendencyBody);
			List<DeactiveDeviceOutputPendency> pendencyDesactiveDeviceOutput = allPendencyBody
					.getDeactivateDeviceOutput();
			WebSocketManager.handleDeactiveDeviceOutputPendency(pendencyDesactiveDeviceOutput, allPendencyBody);
			List<PersonAreaUpdatedPendency> pendencyPersonAreaUpdate = allPendencyBody.getPersonLocationUpdated();
			WebSocketManager.handlePersonAreaUpdatedPendency(pendencyPersonAreaUpdate, allPendencyBody);
			List<DatamartUpdatedPendency> pendencyDatamartUpdate = allPendencyBody.getDatamartUpdated();
			WebSocketManager.handleDatamartUpdatedPendency(pendencyDatamartUpdate, allPendencyBody);
			List<BlockDevicePendency> pendencyRBlockDevice = allPendencyBody.getBlockDevice();
			WebSocketManager.handleBlockDevicePendency(pendencyRBlockDevice, allPendencyBody);
			List<UnblockDevicePendency> pendencyUnblockDevice = allPendencyBody.getUnblockDevice();
			WebSocketManager.handleUnblockDevicePendency(pendencyUnblockDevice, allPendencyBody);
			List<DeviceDisplayMessagePendency> pendencyDeviceDisplayMessage = allPendencyBody.getDeviceDisplayMessage();
			WebSocketManager.handleDeviceDisplayMessagePendency(pendencyDeviceDisplayMessage, allPendencyBody);
			List<IncludePhotoPendency> pendencyIncludePhoto = allPendencyBody.getIncludePhoto();
			WebSocketManager.handleIncludePhotoPendency(pendencyIncludePhoto, allPendencyBody);
			List<ExcludePhotoPendency> pendencyExcludePhoto = allPendencyBody.getExcludePhoto();
			WebSocketManager.handleExcludePhotoPendency(pendencyExcludePhoto, allPendencyBody);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
