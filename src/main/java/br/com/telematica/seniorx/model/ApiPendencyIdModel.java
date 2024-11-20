package br.com.telematica.seniorx.model;

public class ApiPendencyIdModel {

	private ApiPendencyModel pendency;

	public void setPendency(ApiPendencyModel pendency) {
		this.pendency = pendency;
	}

	public ApiPendencyModel getPendency() {
		return pendency;
	}

	@Override
	public String toString() {
		return "pendency=" + pendency + "]";
	}

}
