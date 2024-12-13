package br.com.telematica.seniorx.websocket.manager;

import java.util.List;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import br.com.telematica.seniorx.model.AllPendency;

@Configuration
public class WebSocketPoolingManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketPoolingManager.class);
    
    private final WebSocketManager webSocketManager;
    
    public WebSocketPoolingManager(WebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;
    }
    
    public void removePendencyMessageWebSocket() {
        try {
            AllPendency allPendencyBody = new AllPendency();
            
            processPendencies(allPendencyBody.getLoadCredentialFacialList(), webSocketManager::handleDevicePendency, "CREDENTIAL_FACIAL_LIST");
            processPendencies(allPendencyBody.getRemoveHolidayList(), webSocketManager::handleDevicePendency, "REMOVE_HOLIDAY_LIST");
            processPendencies(allPendencyBody.getInputStatus(), webSocketManager::handleDevicePendency, "DEVICE_INPUT_STATUS");
            processPendencies(allPendencyBody.getUpdateFirmware(), webSocketManager::handleDevicePendency, "UPDATE_FIRMWARE");
            processPendencies(allPendencyBody.getLoadHolidayList(), webSocketManager::handleLoadHolidayListPendency, "LOAD_HOLIDAY_LIST");
            processPendencies(allPendencyBody.getActivateDeviceOutput(), webSocketManager::handleActiveDeviceOutputPendency, "ACTIVATE_DEVICE_OUTPUT");
            processPendencies(allPendencyBody.getCollectEvent(), webSocketManager::handleCollectEventPendency, "COLLECT_EVENTS");
            processPendencies(allPendencyBody.getDeactivateDeviceOutput(), webSocketManager::handleDeactiveDeviceOutputPendency, "DEACTIVATE_DEVICE_OUTPUT");
            processPendencies(allPendencyBody.getPersonLocationUpdated(), webSocketManager::handlePersonAreaUpdatedPendency, "PERSON_AREA_UPDATED");
            processPendencies(allPendencyBody.getDatamartUpdated(), webSocketManager::handleDatamartUpdatedPendency, "DATAMART_UPDATED");
            processPendencies(allPendencyBody.getBlockDevice(), webSocketManager::handleBlockDevicePendency, "BLOCK_DEVICE");
            processPendencies(allPendencyBody.getUnblockDevice(), webSocketManager::handleUnblockDevicePendency, "UNBLOCK_DEVICE");
            processPendencies(allPendencyBody.getDeviceDisplayMessage(), webSocketManager::handleDeviceDisplayMessagePendency, "DEVICE_DISPLAY_MESSAGE");
            processPendencies(allPendencyBody.getIncludePhoto(), webSocketManager::handleIncludePhotoPendency, "INCLUDE_PHOTO");
            processPendencies(allPendencyBody.getExcludePhoto(), webSocketManager::handleExcludePhotoPendency, "EXCLUDE_PHOTO");
            
        } catch (Exception e) {
            logger.error("Error removing pendency messages: {}", e.getMessage(), e);
        }
    }
    
    private <T> void processPendencies(List<T> pendencies, BiConsumer<List<T>, AllPendency> handler, String pendencyType) {
        if (pendencies == null || pendencies.isEmpty()) {
            logger.info("No pendencies found for type: {}", pendencyType);
            return;
        }
        
        try {
            logger.info("Processing {} pendencies for type: {}", pendencies.size(), pendencyType);
            handler.accept(pendencies, new AllPendency());
        } catch (Exception e) {
            logger.error("Error processing pendencies for type {}: {}", pendencyType, e.getMessage(), e);
        }
    }
    
}
