package br.com.telematica.seniorx.service.keepalive;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.telematica.seniorx.model.devices.DeviceController;
import br.com.telematica.seniorx.model.devices.DevicesCollection;
import br.com.telematica.util.Utils;

public class KeepAliveJob implements Job {
    
    private static final Logger logger = LoggerFactory.getLogger(KeepAliveJob.class);
    
    private final DeviceController deviceController;
    
    public KeepAliveJob(DeviceController deviceController) {
        super();
        this.deviceController = deviceController;
    }
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        DevicesCollection deviceId = deviceController.findDevicesByIdOrIp(context.getJobDetail().getJobDataMap().getString("deviceId"));
        
        try {
            sendKeepAlive(deviceId);
        } catch (Exception e) {
            logger.error("Error while sending KeepAlive to device {}: {}", deviceId, e.getMessage(), e);
        }
    }
    
    private void sendKeepAlive(DevicesCollection deviceId) {
        // Lógica de envio do KeepAlive
        logger.info("Sending KeepAlive to device: {} - {} ", Utils.padStart(deviceId.getId().toString(), 10, '0'), deviceId.getNetworkIdentification());
    }
    
}
