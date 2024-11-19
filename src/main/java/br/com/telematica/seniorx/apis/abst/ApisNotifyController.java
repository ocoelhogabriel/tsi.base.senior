package br.com.telematica.seniorx.apis.abst;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.telematica.seniorx.apis.ApisControllerAbstract;
import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.Access;
import br.com.telematica.seniorx.model.Alarm;
import br.com.telematica.seniorx.model.BluetoothAccessNotification;
import br.com.telematica.seniorx.model.ClockIn;
import br.com.telematica.seniorx.model.Event;
import br.com.telematica.seniorx.model.HealthcheckResponse;
import br.com.telematica.seniorx.model.PersonEvent;
import br.com.telematica.seniorx.model.QRCodeAccessNotification;
import br.com.telematica.seniorx.model.Resource;
import br.com.telematica.seniorx.model.VehicleAccess;

public abstract class ApisNotifyController extends ApisControllerAbstract implements IApisController {

	protected ApisNotifyController(RestTemplate restTemplate, String baseUrl) {
		super(restTemplate, baseUrl);
	}

	@Override
	public ResponseEntity<Object> notifyAlarm(Alarm model) {
		HttpEntity<Alarm> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/notify/input/alarm", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> notifyDeviceEvent(Event model) {
		HttpEntity<Event> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/notify/device/event", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> notifyDeviceResource(Resource model) {
		HttpEntity<Resource> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/notify/device/resource", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> notifyVehicleAccess(VehicleAccess model) {
		HttpEntity<VehicleAccess> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/notify/vehicle/access", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> notifyPersonAccess(Access model) {
		HttpEntity<Access> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/notify/person/access", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> notifyPersonEvent(PersonEvent model) {
		HttpEntity<PersonEvent> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/notify/person/event", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> notifyClockIn(ClockIn model) {
		HttpEntity<ClockIn> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/notify/person/clockin", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> notifyQRCodeAccessNotification(QRCodeAccessNotification model) {
		HttpEntity<QRCodeAccessNotification> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/notify/qrcode/access", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> notifyBluetoothAccessNotification(BluetoothAccessNotification model) {
		HttpEntity<BluetoothAccessNotification> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/notify/bluethooth/access", HttpMethod.POST, entity, Object.class);
	}

	public ResponseEntity<HealthcheckResponse> createDeviceAccessRequestHealthCheck() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/notify/healthcheck", HttpMethod.GET, entity,
				HealthcheckResponse.class);
	}

}
