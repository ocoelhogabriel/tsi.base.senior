package br.com.telematica.seniorx.apis;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
import br.com.telematica.seniorx.model.IncludeBiometry;
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
public interface IApisController {
	ResponseEntity<List<ManagerDevice>> getDevice();

	ResponseEntity<Object> getDeviceStatus();

	ResponseEntity<Object> getPersonInfo();

	ResponseEntity<List<AreaControlList>> getAreaControl();

	ResponseEntity<ManagerDevice> getDeviceId(Long id);

	ResponseEntity<Object> getBiometry();

	ResponseEntity<Object> getCardFormat();

	ResponseEntity<Object> getAccessLevel();

	ResponseEntity<Object> getHolyday();

	ResponseEntity<Object> getTimezone();

	ResponseEntity<Object> getDeviceIdCard(Long id);

	ResponseEntity<Object> getDeviceIdAccessCard(Long id);

	ResponseEntity<Object> getDeviceIdAccessBiometry(Long id);

	ResponseEntity<Object> getDeviceIdRepCredential(Long id);

	ResponseEntity<Object> getDeviceIdAccessPhoto(Long id);

	ResponseEntity<Object> createIncludeBiometry(IncludeBiometry model);

	ResponseEntity<PersonValidationResponse> createAccessRequest(AccessRequest model);

	ResponseEntity<ValidationResponse> createVehicleAccessRequest(VehicleAccessRequest model);

	ResponseEntity<PersonValidationResponse> createQRCodeAccessRequest(QRCodeAccessRequest model);

	ResponseEntity<PersonValidationResponse> createCollectEventStatus(CollectEventStatus model);

	ResponseEntity<PersonValidationResponse> createCollectEventStatus(DeviceInputStatus model);

	ResponseEntity<AllPendency> getPendency();

	ResponseEntity<AllPendency> getPendencyIdDevice(Long id);

	ResponseEntity<Object> updatePendency(List<PendencyUpdated> model);

	ResponseEntity<Object> successPendency(PendencyExecuted model);

	ResponseEntity<Object> createDeviceStatus(DeviceStatus model);

	ResponseEntity<Driver> getDriver();

	ResponseEntity<DriverDateTime> getDriverDateTime();

	ResponseEntity<Object> postDriverDateTime(DriverManufacturer model);

	ResponseEntity<Object> postDriverStatusInput(DriverStatusInput model);

	ResponseEntity<Object> notifyAlarm(Alarm model);

	ResponseEntity<Object> notifyDeviceEvent(Event model);

	ResponseEntity<Object> notifyDeviceResource(Resource model);

	ResponseEntity<Object> notifyVehicleAccess(VehicleAccess model);

	ResponseEntity<Object> notifyPersonAccess(Access model);

	ResponseEntity<Object> notifyPersonEvent(PersonEvent model);

	ResponseEntity<Object> notifyClockIn(ClockIn model);

	ResponseEntity<Object> notifyQRCodeAccessNotification(QRCodeAccessNotification model);

	ResponseEntity<Object> notifyBluetoothAccessNotification(BluetoothAccessNotification model);

	ResponseEntity<Object> getServer();

}
