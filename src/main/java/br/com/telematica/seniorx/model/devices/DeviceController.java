package br.com.telematica.seniorx.model.devices;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.telematica.seniorx.model.AreaControlList;
import br.com.telematica.seniorx.model.ManagerDevice;

@Component
public class DeviceController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);
    
    private final Collection<DevicesCollection> devices = new CopyOnWriteArrayList<>();
    
    private final Collection<AreaControlList> areaId = new CopyOnWriteArrayList<>();
    
    /**
     * @param updatedDevices Coleção de dispositivos a serem atualizados.
     */
    public void onDeviceUpdate(Collection<DevicesCollection> updatedDevices) {
        logger.debug("Atualizando os dispositivos recebidos no DeviceController");
        updatedDevices.forEach(this::updateOrAddDevice);
        logger.debug("|-> Total de dispositivos armazenados: {}", devices.size());
    }
    
    public void onDeviceUpdateManagerDevice(Collection<ManagerDevice> updatedDevices) {
        logger.debug("Atualizando os dispositivos recebidos no DeviceController");
        updatedDevices.forEach(this::updateOrAddDevice);
        logger.debug("|-> Total de dispositivos armazenados: {}", devices.size());
    }
    
    /**
     * @param updatedAreas Coleção de áreas a serem atualizadas.
     */
    public void onAreaUpdate(Collection<AreaControlList> updatedAreas) {
        logger.debug("Atualizando as áreas de controle no DeviceController");
        updatedAreas.forEach(this::updateOrAddArea);
        logger.debug("|-> Total de áreas armazenadas: {}", areaId.size());
    }
    
    /**
     * @param newArea A área de controle a ser atualizada ou adicionada.
     */
    private void updateOrAddArea(AreaControlList newArea) {
        areaId.removeIf(area -> Objects.equals(area.getId(), newArea.getId()));
        areaId.add(newArea);
    }
    
    /**
     * @param newDevice O dispositivo a ser atualizado ou adicionado.
     */
    private void updateOrAddDevice(DevicesCollection newDevice) {
        devices.removeIf(device -> Objects.equals(device.getId(), newDevice.getId()) || device.getNetworkIdentification().equals(newDevice.getNetworkIdentification()));
        devices.add(newDevice);
    }
    
    private void updateOrAddDevice(ManagerDevice newDevice) {
        logger.info("Atualizando dispositivo com ID: {} e IP: {}", newDevice.getId(), newDevice.getNetworkIdentification());
        
        devices.removeIf(device -> Objects.equals(device.getId(), newDevice.getId()));
        
        DevicesCollection deviceToAdd = convertToDevicesCollection(newDevice);
        devices.add(deviceToAdd);
        
        logger.info("Dispositivo adicionado ou atualizado: {} - ", deviceToAdd.getId(), deviceToAdd.getNetworkIdentification());
    }
    
    /**
     * @param managerDevice O dispositivo a ser convertido.
     * @return Um novo objeto DevicesCollection com os dados do ManagerDevice.
     */
    private DevicesCollection convertToDevicesCollection(ManagerDevice managerDevice) {
        DevicesCollection device = new DevicesCollection();
        device.setId(managerDevice.getId());
        device.setNetworkIdentification(managerDevice.getNetworkIdentification());
        device.setNetworkPort(managerDevice.getNetworkPort());
        device.setDisplayMessage(managerDevice.getDisplayMessage());
        device.setNumberOfCardRecords(managerDevice.getNumberOfCardRecords());
        device.setTurnstileBypassTime(managerDevice.getTurnstileBypassTime());
        device.setModule(managerDevice.getModule());
        device.setReader(managerDevice.getReader());
        device.setAccessMessage(managerDevice.getAccessMessage());
        device.setPortConfiguration(managerDevice.getPortConfiguration());
        device.setRepConfiguration(managerDevice.getRepConfiguration());
        device.setExtensibleConfiguration(managerDevice.getExtensibleConfiguration());
        
        Optional.ofNullable(managerDevice.getAreaId())
                .ifPresent(managerAreaId -> areaId.stream().filter(existingArea -> existingArea.getId().equals(managerAreaId)).findFirst().ifPresent(device::setAreaControl));
        
        return device;
    }
    
    /**
     * @return Coleção imutável de dispositivos.
     */
    public Collection<DevicesCollection> getAllDevices() {
        return Collections.unmodifiableCollection(devices);
    }
    
    /**
     * @return Coleção imutável de áreas de controle.
     */
    public Collection<AreaControlList> getAllAreas() {
        return Collections.unmodifiableCollection(areaId);
    }
    
    /**
     * @param identifier Identificador do dispositivo (ID ou IP).
     * @return Optional contendo o DevicesCollection, caso encontrado.
     */
    public Optional<DevicesCollection> getDevicesCollection(Object identifier) {
        return devices.stream().filter(d -> (identifier instanceof Long && d.getId() == (long) identifier) || (identifier instanceof String && d.getNetworkIdentification().equals(identifier)))
                      .findFirst();
    }
    
    /**
     * @param searchTerm Valor de pesquisa (ID ou IP).
     * @return O dispositivo correspondente ou lança uma exceção se não encontrado.
     */
    public DevicesCollection findDevicesByIdOrIp(Object searchTerm) {
        return refreshListDevice(searchTerm);
    }
    
    public AreaControlList findAreaControlList(Object searchTerm) {
        return areaId.stream().filter(area -> (searchTerm instanceof Long && area.getId() == (long) searchTerm)).findFirst().orElse(null);
    }
    
    private DevicesCollection refreshListDevice(Object searchTerm) {
        
        if (searchTerm == null) {
            logger.warn("Search term is null.");
            return null;
        }
        
        if (isNumeric(searchTerm.toString())) {
            Long id = Long.valueOf(searchTerm.toString());
            return devices.stream().filter(d -> Objects.equals(d.getId(), id)).findFirst().orElse(null);
        }
        
        return devices.stream().filter(d -> searchTerm.toString().equals(d.getNetworkIdentification())).findFirst().orElse(null);
    }
    
    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
}
