package br.com.telematica.seniorx.apis.abst;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.telematica.seniorx.apis.ApisControllerAbstract;
import br.com.telematica.seniorx.model.Access;
import br.com.telematica.seniorx.model.AccessRequest;
import br.com.telematica.seniorx.model.Alarm;
import br.com.telematica.seniorx.model.AllPendency;
import br.com.telematica.seniorx.model.AreaControlList;
import br.com.telematica.seniorx.model.BluetoothAccessNotification;
import br.com.telematica.seniorx.model.ClockIn;
import br.com.telematica.seniorx.model.CollectEventStatus;
import br.com.telematica.seniorx.model.DeviceInputStatus;
import br.com.telematica.seniorx.model.DeviceStatus;
import br.com.telematica.seniorx.model.Driver;
import br.com.telematica.seniorx.model.DriverDateTime;
import br.com.telematica.seniorx.model.DriverManufacturer;
import br.com.telematica.seniorx.model.DriverStatusInput;
import br.com.telematica.seniorx.model.Event;
import br.com.telematica.seniorx.model.HealthcheckResponse;
import br.com.telematica.seniorx.model.IncludeBiometry;
import br.com.telematica.seniorx.model.LprAccessRequest;
import br.com.telematica.seniorx.model.LprValidationResponse;
import br.com.telematica.seniorx.model.ManagerDevice;
import br.com.telematica.seniorx.model.PendencyExecuted;
import br.com.telematica.seniorx.model.PendencyUpdated;
import br.com.telematica.seniorx.model.PersonEvent;
import br.com.telematica.seniorx.model.PersonValidationResponse;
import br.com.telematica.seniorx.model.QRCodeAccessNotification;
import br.com.telematica.seniorx.model.QRCodeAccessRequest;
import br.com.telematica.seniorx.model.Resource;
import br.com.telematica.seniorx.model.ValidationResponse;
import br.com.telematica.seniorx.model.VehicleAccess;
import br.com.telematica.seniorx.model.VehicleAccessRequest;

@Service
public class ApisControllerImpl extends ApisControllerAbstract {
    
    private static final RestTemplate RESTTEMPLATE = new RestTemplate();
    
    public ApisControllerImpl(@Value("${api.sdk.senior}") String baseUrl, @Value("${partner_key}") String partner, @Value("${driver_key}") String driver) {
        super(baseUrl, partner, driver);
    }
    
    @Override
    public ResponseEntity<List<ManagerDevice>> getDevice() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device", HttpMethod.GET, entity, new ParameterizedTypeReference<List<ManagerDevice>>() {
        });
    }
    
    @Override
    public ResponseEntity<Object> getDeviceIdCard(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/" + id + "/card", HttpMethod.GET, entity, Object.class);
    }
    
    public ResponseEntity<LprValidationResponse> createLprAccessRequest(LprAccessRequest model) {
        HttpEntity<LprAccessRequest> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/lpr/accessrequest", HttpMethod.POST, entity, LprValidationResponse.class);
    }
    
    @Override
    public ResponseEntity<Object> getServer() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/server/status", HttpMethod.GET, entity, Object.class);
    }
    
    // Datamart
    @Override
    public ResponseEntity<Object> getBiometry() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/datamart/biometry", HttpMethod.GET, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> getCardFormat() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/datamart/cardformat", HttpMethod.GET, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<List<AreaControlList>> getAreaControl() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/datamart/areacontrol", HttpMethod.GET, entity, new ParameterizedTypeReference<List<AreaControlList>>() {
        });
    }
    
    @Override
    public ResponseEntity<Object> getAccessLevel() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/datamart/acceslevel", HttpMethod.GET, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> getHolyday() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/datamart/holiday", HttpMethod.GET, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> getTimezone() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/datamart/timezone", HttpMethod.GET, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> getPersonInfo() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/datamart/person/info", HttpMethod.GET, entity, Object.class);
    }
    
    // Devices
    @Override
    public ResponseEntity<ManagerDevice> getDeviceId(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/" + id, HttpMethod.GET, entity, ManagerDevice.class);
    }
    
    @Override
    public ResponseEntity<Object> getDeviceIdRepCredential(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/rep/" + id + "/credential", HttpMethod.GET, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> getDeviceIdAccessCard(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/access/" + id + "/card", HttpMethod.GET, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> getDeviceIdAccessBiometry(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/access/" + id + "/biometry", HttpMethod.GET, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> getDeviceIdAccessPhoto(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/access/" + id + "/photo", HttpMethod.GET, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> getDeviceStatus() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/status", HttpMethod.GET, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> createIncludeBiometry(IncludeBiometry model) {
        HttpEntity<IncludeBiometry> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/biometry", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<PersonValidationResponse> createAccessRequest(AccessRequest model) {
        HttpEntity<AccessRequest> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/accessrequest", HttpMethod.POST, entity, PersonValidationResponse.class);
    }
    
    @Override
    public ResponseEntity<ValidationResponse> createVehicleAccessRequest(VehicleAccessRequest model) {
        HttpEntity<VehicleAccessRequest> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/accessrequest/vehicle", HttpMethod.POST, entity, ValidationResponse.class);
    }
    
    @Override
    public ResponseEntity<PersonValidationResponse> createQRCodeAccessRequest(QRCodeAccessRequest model) {
        HttpEntity<QRCodeAccessRequest> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/accessrequest/qrcode", HttpMethod.POST, entity, PersonValidationResponse.class);
    }
    
    @Override
    public ResponseEntity<PersonValidationResponse> createCollectEventStatus(CollectEventStatus model) {
        HttpEntity<CollectEventStatus> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/collect/status", HttpMethod.POST, entity, PersonValidationResponse.class);
    }
    
    @Override
    public ResponseEntity<PersonValidationResponse> createCollectEventStatus(DeviceInputStatus model) {
        HttpEntity<DeviceInputStatus> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/collect/status", HttpMethod.POST, entity, PersonValidationResponse.class);
    }
    
    @Override
    public ResponseEntity<Object> createDeviceStatus(DeviceStatus model) {
        HttpEntity<DeviceStatus> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/status", HttpMethod.POST, entity, Object.class);
    }
    
    public ResponseEntity<HealthcheckResponse> createDeviceAccessRequestHealthCheck() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/device/accessrequest/healthcheck", HttpMethod.GET, entity, HealthcheckResponse.class);
    }
    
    // Driver
    @Override
    public ResponseEntity<Driver> getDriver() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/driver", HttpMethod.GET, entity, Driver.class);
    }
    
    @Override
    public ResponseEntity<DriverDateTime> getDriverDateTime() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/driver/datetime", HttpMethod.GET, entity, DriverDateTime.class);
    }
    
    @Override
    public ResponseEntity<Object> postDriverDateTime(DriverManufacturer model) {
        HttpEntity<DriverManufacturer> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/driver/manufacture", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> postDriverStatusInput(DriverStatusInput model) {
        HttpEntity<DriverStatusInput> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/driver/status", HttpMethod.POST, entity, Object.class);
    }
    
    // Notify
    @Override
    public ResponseEntity<Object> notifyAlarm(Alarm model) {
        HttpEntity<Alarm> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/notify/input/alarm", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> notifyDeviceEvent(Event model) {
        HttpEntity<Event> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/notify/device/event", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> notifyDeviceResource(Resource model) {
        HttpEntity<Resource> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/notify/device/resource", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> notifyVehicleAccess(VehicleAccess model) {
        HttpEntity<VehicleAccess> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/notify/vehicle/access", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> notifyPersonAccess(Access model) {
        HttpEntity<Access> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/notify/person/access", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> notifyPersonEvent(PersonEvent model) {
        HttpEntity<PersonEvent> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/notify/person/event", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> notifyClockIn(ClockIn model) {
        HttpEntity<ClockIn> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/notify/person/clockin", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> notifyQRCodeAccessNotification(QRCodeAccessNotification model) {
        HttpEntity<QRCodeAccessNotification> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/notify/qrcode/access", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> notifyBluetoothAccessNotification(BluetoothAccessNotification model) {
        HttpEntity<BluetoothAccessNotification> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/notify/bluethooth/access", HttpMethod.POST, entity, Object.class);
    }
    
    public ResponseEntity<HealthcheckResponse> createNotifyHealthCheck() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/notify/healthcheck", HttpMethod.GET, entity, HealthcheckResponse.class);
    }
    
    // Pendency
    @Override
    public ResponseEntity<AllPendency> getPendency() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/pendency", HttpMethod.GET, entity, AllPendency.class);
    }
    
    @Override
    public ResponseEntity<AllPendency> getPendencyIdDevice(Long id) {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/pendency/device/" + id, HttpMethod.GET, entity, AllPendency.class);
    }
    
    @Override
    public ResponseEntity<Object> updatePendency(List<PendencyUpdated> model) {
        HttpEntity<List<PendencyUpdated>> entity = new HttpEntity<>(model, createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/pendency/update", HttpMethod.POST, entity, Object.class);
    }
    
    @Override
    public ResponseEntity<Object> successPendency(PendencyExecuted model) {
        HttpEntity<PendencyExecuted> entity = new HttpEntity<>(createHeaders());
        return RESTTEMPLATE.exchange(baseUrl + "/pendency/success", HttpMethod.POST, entity, Object.class);
    }
    
}
