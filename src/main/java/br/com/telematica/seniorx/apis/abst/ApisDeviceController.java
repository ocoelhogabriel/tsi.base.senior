package br.com.telematica.seniorx.apis.abst;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.telematica.seniorx.apis.ApisControllerAbstract;
import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.AccessRequest;
import br.com.telematica.seniorx.model.CollectEventStatus;
import br.com.telematica.seniorx.model.DeviceInputStatus;
import br.com.telematica.seniorx.model.DeviceStatus;
import br.com.telematica.seniorx.model.HealthcheckResponse;
import br.com.telematica.seniorx.model.IncludeBiometry;
import br.com.telematica.seniorx.model.ManagerDevice;
import br.com.telematica.seniorx.model.PersonValidationResponse;
import br.com.telematica.seniorx.model.QRCodeAccessRequest;
import br.com.telematica.seniorx.model.ValidationResponse;
import br.com.telematica.seniorx.model.VehicleAccessRequest;

public abstract class ApisDeviceController extends ApisControllerAbstract implements IApisController {

	protected ApisDeviceController(RestTemplate restTemplate, String baseUrl) {
		super(restTemplate, baseUrl);
	}

	@Override
	public ResponseEntity<ManagerDevice> getDeviceId(Long id) {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/device/" + id, HttpMethod.GET, entity, ManagerDevice.class);
	}

	@Override
	public ResponseEntity<List<ManagerDevice>> getDevice() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/device/", HttpMethod.GET, entity, new ParameterizedTypeReference<List<ManagerDevice>>() {
		});
	}

	@Override
	public ResponseEntity<Object> getDeviceIdCard(Long id) {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/device/" + id + "/card", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> getDeviceIdRepCredential(Long id) {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/device/rep/" + id + "/credential", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> getDeviceIdAccessCard(Long id) {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/device/access/" + id + "/card", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> getDeviceIdAccessBiometry(Long id) {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/device/access/" + id + "/biometry", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> getDeviceIdAccessPhoto(Long id) {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/device/access/" + id + "/photo", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> getDeviceStatus() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/device/status", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> createIncludeBiometry(IncludeBiometry model) {
		HttpEntity<IncludeBiometry> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/device/biometry", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<PersonValidationResponse> createAccessRequest(AccessRequest model) {
		HttpEntity<AccessRequest> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/device/accessrequest", HttpMethod.POST, entity, PersonValidationResponse.class);
	}

	@Override
	public ResponseEntity<ValidationResponse> createVehicleAccessRequest(VehicleAccessRequest model) {
		HttpEntity<VehicleAccessRequest> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/device/accessrequest/vehicle", HttpMethod.POST, entity, ValidationResponse.class);
	}

	@Override
	public ResponseEntity<PersonValidationResponse> createQRCodeAccessRequest(QRCodeAccessRequest model) {
		HttpEntity<QRCodeAccessRequest> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/device/accessrequest/qrcode", HttpMethod.POST, entity, PersonValidationResponse.class);
	}

	@Override
	public ResponseEntity<PersonValidationResponse> createCollectEventStatus(CollectEventStatus model) {
		HttpEntity<CollectEventStatus> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/device/collect/status", HttpMethod.POST, entity, PersonValidationResponse.class);
	}

	@Override
	public ResponseEntity<PersonValidationResponse> createCollectEventStatus(DeviceInputStatus model) {
		HttpEntity<DeviceInputStatus> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/device/collect/status", HttpMethod.POST, entity, PersonValidationResponse.class);
	}

	@Override
	public ResponseEntity<Object> createDeviceStatus(DeviceStatus model) {
		HttpEntity<DeviceStatus> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/device/status", HttpMethod.POST, entity, Object.class);
	}

	public ResponseEntity<HealthcheckResponse> createDeviceAccessRequestHealthCheck() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/device/accessrequest/healthcheck", HttpMethod.GET, entity, HealthcheckResponse.class);
	}

}
