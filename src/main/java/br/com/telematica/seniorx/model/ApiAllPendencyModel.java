package br.com.telematica.seniorx.model;

public class ApiAllPendencyModel {

	private AllPendency allPendency;

	public ApiAllPendencyModel(AllPendency allPendency) {
		this.allPendency = allPendency;
	}

	public AllPendency getAllPendency() {
		return allPendency;
	}

	public void setAllPendency(AllPendency allPendency) {
		this.allPendency = allPendency;
	}

}
