package br.com.telematica.seniorx.apis.abst;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.telematica.seniorx.apis.ApisControllerAbstract;
import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.AllPendency;
import br.com.telematica.seniorx.model.PendencyExecuted;
import br.com.telematica.seniorx.model.PendencyUpdated;

public abstract class ApisPendencyController extends ApisControllerAbstract implements IApisController {

	protected ApisPendencyController(RestTemplate restTemplate, String baseUrl) {
		super(restTemplate, baseUrl);
	}

	@Override
	public ResponseEntity<AllPendency> getPendency() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/pendency", HttpMethod.GET, entity, AllPendency.class);
	}

	@Override
	public ResponseEntity<AllPendency> getPendencyIdDevice(Long id) {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/pendency/device/" + id, HttpMethod.GET, entity, AllPendency.class);
	}

	@Override
	public ResponseEntity<Object> updatePendency(PendencyUpdated model) {
		HttpEntity<PendencyUpdated> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/pendency/update", HttpMethod.POST, entity, Object.class);
	}

	@Override
	public ResponseEntity<Object> successPendency(PendencyExecuted model) {
		HttpEntity<PendencyExecuted> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/pendency/success", HttpMethod.POST, entity, Object.class);
	}

}
