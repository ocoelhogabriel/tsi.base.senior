package br.com.telematica.seniorx.apis.abst;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.telematica.seniorx.apis.ApisControllerAbstract;
import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.AreaControlList;

public abstract class ApisDatamartController extends ApisControllerAbstract implements IApisController {

	protected ApisDatamartController(RestTemplate restTemplate, String baseUrl) {
		super(restTemplate, baseUrl);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ResponseEntity<Object> getBiometry() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/datamart/biometry", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> getCardFormat() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/datamart/cardformat", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<List<AreaControlList>> getAreaControl() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/datamart/areacontrol", HttpMethod.GET, entity, new ParameterizedTypeReference<List<AreaControlList>>() {
		});
	}

	@Override
	public ResponseEntity<Object> getAccessLevel() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/datamart/acceslevel", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> getHolyday() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/datamart/holiday", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> getTimezone() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/datamart/timezone", HttpMethod.GET, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> getPersonInfo() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/datamart/person/info", HttpMethod.GET, entity, Object.class);
	}
}
