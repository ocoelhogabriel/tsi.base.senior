package br.com.telematica.seniorx.apis.abst;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.telematica.seniorx.apis.ApisControllerAbstract;
import br.com.telematica.seniorx.apis.IApisController;
import br.com.telematica.seniorx.model.LprAccessRequest;
import br.com.telematica.seniorx.model.LprValidationResponse;

public abstract class ApisController extends ApisControllerAbstract implements IApisController {

	protected ApisController(RestTemplate restTemplate, String baseUrl) {
		super(restTemplate, baseUrl);
		// TODO Auto-generated constructor stub
	}

	public ResponseEntity<LprValidationResponse> createLprAccessRequest(LprAccessRequest model) {
		HttpEntity<LprAccessRequest> entity = new HttpEntity<>(model, createHeaders());
		return restTemplate.exchange(baseUrl + "/lpr/accessrequest", HttpMethod.POST, entity,
				LprValidationResponse.class);
	}

	@Override
	public ResponseEntity<Object> getServer() {
		HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
		return restTemplate.exchange(baseUrl + "/server/status", HttpMethod.GET, entity, Object.class);
	}
}
