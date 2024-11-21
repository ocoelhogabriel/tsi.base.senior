package br.com.telematica.seniorx.service.keepalive;

import java.util.Collection;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.telematica.seniorx.model.devices.DevicesCollection;

@Service
public class DevicesKeepAliveServices {

	private static final Logger logger = LoggerFactory.getLogger(DevicesKeepAliveServices.class);

	private final Scheduler scheduler;

	public DevicesKeepAliveServices(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * Inicializa o KeepAlive para uma lista de dispositivos.
	 */
	public void initializeKeepAlive(Collection<DevicesCollection> deviceIds) {
		for (DevicesCollection device : deviceIds) {
			try {
				scheduleKeepAliveJob(device);
			} catch (SchedulerException e) {
				logger.error("Failed to schedule KeepAlive job for device {} - {}: {}", device.getId(),
						device.getNetworkIdentification(), e.getMessage(), e);
			}
		}
	}

	/**
	 * Agenda o Job para um dispositivo específico.
	 */
	private void scheduleKeepAliveJob(DevicesCollection device) throws SchedulerException {
		JobDetail jobDetail = buildJobDetail(device);
		Trigger trigger = buildJobTrigger(jobDetail);

		scheduler.scheduleJob(jobDetail, trigger);
		logger.info("Scheduled KeepAlive job for Device: {} Network: {}", device.getId(),
				device.getNetworkIdentification());
	}

	/**
	 * Cria o JobDetail com as informações do dispositivo.
	 */
	private JobDetail buildJobDetail(DevicesCollection device) {
		return JobBuilder.newJob(KeepAliveJob.class).withIdentity("KeepAliveJob_" + device.getId())
				.usingJobData("deviceId", String.valueOf(device.getId()))
				.usingJobData("deviceNetwork", device.getNetworkIdentification()).storeDurably().build();
	}

	/**
	 * Configura o Trigger para executar o Job periodicamente.
	 */
	private Trigger buildJobTrigger(JobDetail jobDetail) {
		return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobDetail.getKey().getName() + "_Trigger")
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(15) // Intervalo de 15
																								// segundos
						.repeatForever())
				.build();
	}
}
