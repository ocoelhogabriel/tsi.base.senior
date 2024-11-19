package br.com.telematica.seniorx.apis.abst;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.telematica.seniorx.apis.ApisControllerAbstract;
import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.Driver;
import br.com.telematica.seniorx.model.DriverDateTime;
import br.com.telematica.seniorx.model.DriverManufacturer;
import br.com.telematica.seniorx.model.DriverStatusInput;

public abstract class ApisDriverController extends ApisControllerAbstract implements IApisController {

	protected ApisDriverController(RestTemplate restTemplate, String baseUrl) {
		super(restTemplate, baseUrl);
	}

	@Override
	public ResponseEntity<Driver> getDriver() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/driver", HttpMethod.GET, entity, Driver.class);
	}

	@Override
	public ResponseEntity<DriverDateTime> getDriverDateTime() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/driver/datetime", HttpMethod.GET, entity, DriverDateTime.class);
	}

	@Override
	public ResponseEntity<Object> postDriverDateTime(DriverManufacturer model) {
		HttpEntity<DriverManufacturer> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/driver/manufacture", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> postDriverStatusInput(DriverStatusInput model) {
		HttpEntity<DriverStatusInput> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/driver/status", HttpMethod.POST, entity, Object.class);
	}

}
