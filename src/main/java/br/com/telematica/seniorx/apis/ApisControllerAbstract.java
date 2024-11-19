package br.com.telematica.seniorx.apis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public abstract class ApisControllerAbstract {

	@Value("${partner_key}")
	private String partner;
	@Value("${driver_key}")
	private String driver;
	@Value("${api.sdk.senior}")
	private String url;

	protected final RestTemplate restTemplate;
	protected final String baseUrl;

	protected ApisControllerAbstract(RestTemplate restTemplate, String baseUrl) {
		super();
		this.restTemplate = restTemplate;
		this.baseUrl = url;
	}

	protected HttpHeaders createHeaders() {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("partner_key", partner);
			headers.add("driver_key", driver);
			return headers;
		} catch (Exception e) {
			return HttpHeaders.EMPTY;
		}
	}
}
