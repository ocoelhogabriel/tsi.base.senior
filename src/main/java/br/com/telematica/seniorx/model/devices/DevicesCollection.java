package br.com.telematica.seniorx.model.devices;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import br.com.telematica.seniorx.model.AccessMessage;
import br.com.telematica.seniorx.model.AreaControlList;
import br.com.telematica.seniorx.model.ExtensibleConfiguration;
import br.com.telematica.seniorx.model.ModuleDevice;
import br.com.telematica.seniorx.model.PortConfiguration;
import br.com.telematica.seniorx.model.ReaderDevice;
import br.com.telematica.seniorx.model.RepConfiguration;

public class DevicesCollection {

	@SerializedName("id")
	private Long id = null;

	@SerializedName("areaControl")
	private AreaControlList areaControl;

	@SerializedName("networkIdentification")
	private String networkIdentification = null;

	@SerializedName("networkPort")
	private Integer networkPort = null;

	@SerializedName("displayMessage")
	private String displayMessage = null;

	@SerializedName("numberOfCardRecords")
	private Integer numberOfCardRecords = null;

	@SerializedName("turnstileBypassTime")
	private Integer turnstileBypassTime = null;

	@SerializedName("module")
	private List<ModuleDevice> module = null;

	@SerializedName("reader")
	private List<ReaderDevice> reader = null;

	@SerializedName("accessMessage")
	private List<AccessMessage> accessMessage = null;

	@SerializedName("portConfiguration")
	private List<PortConfiguration> portConfiguration = null;

	@SerializedName("repConfiguration")
	private RepConfiguration repConfiguration = null;

	@SerializedName("extensibleConfiguration")
	private ExtensibleConfiguration extensibleConfiguration = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AreaControlList getAreaControl() {
		return areaControl;
	}

	public void setAreaControl(AreaControlList areaControl) {
		this.areaControl = areaControl;
	}

	public String getNetworkIdentification() {
		return networkIdentification;
	}

	public void setNetworkIdentification(String networkIdentification) {
		this.networkIdentification = networkIdentification;
	}

	public Integer getNetworkPort() {
		return networkPort;
	}

	public void setNetworkPort(Integer networkPort) {
		this.networkPort = networkPort;
	}

	public String getDisplayMessage() {
		return displayMessage;
	}

	public void setDisplayMessage(String displayMessage) {
		this.displayMessage = displayMessage;
	}

	public Integer getNumberOfCardRecords() {
		return numberOfCardRecords;
	}

	public void setNumberOfCardRecords(Integer numberOfCardRecords) {
		this.numberOfCardRecords = numberOfCardRecords;
	}

	public Integer getTurnstileBypassTime() {
		return turnstileBypassTime;
	}

	public void setTurnstileBypassTime(Integer turnstileBypassTime) {
		this.turnstileBypassTime = turnstileBypassTime;
	}

	public List<ModuleDevice> getModule() {
		return module;
	}

	public void setModule(List<ModuleDevice> module) {
		this.module = module;
	}

	public List<ReaderDevice> getReader() {
		return reader;
	}

	public void setReader(List<ReaderDevice> reader) {
		this.reader = reader;
	}

	public List<AccessMessage> getAccessMessage() {
		return accessMessage;
	}

	public void setAccessMessage(List<AccessMessage> accessMessage) {
		this.accessMessage = accessMessage;
	}

	public List<PortConfiguration> getPortConfiguration() {
		return portConfiguration;
	}

	public void setPortConfiguration(List<PortConfiguration> portConfiguration) {
		this.portConfiguration = portConfiguration;
	}

	public RepConfiguration getRepConfiguration() {
		return repConfiguration;
	}

	public void setRepConfiguration(RepConfiguration repConfiguration) {
		this.repConfiguration = repConfiguration;
	}

	public ExtensibleConfiguration getExtensibleConfiguration() {
		return extensibleConfiguration;
	}

	public void setExtensibleConfiguration(ExtensibleConfiguration extensibleConfiguration) {
		this.extensibleConfiguration = extensibleConfiguration;
	}

}
