
package br.com.telematica.seniorx.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ReaderDevice
 */

public class ReaderDevice {
	@SerializedName("id")
	private Long id = null;

	@SerializedName("areaId")
	private Long areaId = null;

	@SerializedName("output")
	private List<OutputDevice> output = null;

	@SerializedName("intput")
	private List<InputDevice> intput = null;

	/**
	 * Gets or Sets readerTechnology
	 */

	@SerializedName("readerTechnology")
	private ReaderTechnologyEnum readerTechnology = null;

	@SerializedName("readerDirection")
	private AccessDirectionEnum readerDirection = null;

	/**
	 * Gets or Sets biometricManufacturer
	 */

	@SerializedName("biometricManufacturer")
	private ManufacturerEnum biometricManufacturer = null;

	/**
	 * Gets or Sets biometricValidation
	 */
	@JsonAdapter(BiometricValidationEnum.Adapter.class)
	public enum BiometricValidationEnum {
		NONE("NONE"),

		ONE_TO_ONE("ONE_TO_ONE"),

		BOTH("BOTH");

		private String value;

		BiometricValidationEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		public static BiometricValidationEnum fromValue(String text) {
			for (BiometricValidationEnum b : BiometricValidationEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}

		public static class Adapter extends TypeAdapter<BiometricValidationEnum> {
			@Override
			public void write(final JsonWriter jsonWriter, final BiometricValidationEnum enumeration)
					throws IOException {
				jsonWriter.value(enumeration.getValue());
			}

			@Override
			public BiometricValidationEnum read(final JsonReader jsonReader) throws IOException {
				String value = jsonReader.nextString();
				return BiometricValidationEnum.fromValue(String.valueOf(value));
			}
		}
	}

	@SerializedName("biometricValidation")
	private BiometricValidationEnum biometricValidation = null;

	@SerializedName("biometricSecurityLevel")
	private Integer biometricSecurityLevel = null;

	@SerializedName("biometricReaderId")
	private Long biometricReaderId = null;

	@SerializedName("physicalAddress")
	private Integer physicalAddress = null;

	@SerializedName("areaLeaving")
	private Long areaLeaving = null;

	@SerializedName("areaEntering")
	private Long areaEntering = null;

	@SerializedName("cardFormatList")
	private List<Long> cardFormatList = null;

	@SerializedName("strikeTime")
	private Integer strikeTime = null;

	@SerializedName("heldOpenTime")
	private Integer heldOpenTime = null;

	/**
	 * Gets or Sets offlineMode
	 */
	@JsonAdapter(OfflineModeEnum.Adapter.class)
	public enum OfflineModeEnum {
		LOCKED("LOCKED"),

		CARD_ONLY("CARD_ONLY"),

		PIN_OR_CARD("PIN_OR_CARD"),

		CARD_AND_PIN("CARD_AND_PIN"),

		UNLOCKED("UNLOCKED"),

		FACILITY_CODE_ONLY("FACILITY_CODE_ONLY"),

		CIPHER_LOCK_EMULATION("CIPHER_LOCK_EMULATION");

		private String value;

		OfflineModeEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		public static OfflineModeEnum fromValue(String text) {
			for (OfflineModeEnum b : OfflineModeEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}

		public static class Adapter extends TypeAdapter<OfflineModeEnum> {
			@Override
			public void write(final JsonWriter jsonWriter, final OfflineModeEnum enumeration) throws IOException {
				jsonWriter.value(enumeration.getValue());
			}

			@Override
			public OfflineModeEnum read(final JsonReader jsonReader) throws IOException {
				String value = jsonReader.nextString();
				return OfflineModeEnum.fromValue(String.valueOf(value));
			}
		}
	}

	@SerializedName("offlineMode")
	private OfflineModeEnum offlineMode = null;

	/**
	 * Gets or Sets accessMode
	 */
	@JsonAdapter(AccessModeEnum.Adapter.class)
	public enum AccessModeEnum {
		LOCKED("LOCKED"),

		CARD_ONLY("CARD_ONLY"),

		PIN_OR_CARD("PIN_OR_CARD"),

		CARD_AND_PIN("CARD_AND_PIN"),

		UNLOCKED("UNLOCKED"),

		FACILITY_CODE_ONLY("FACILITY_CODE_ONLY"),

		CIPHER_LOCK_EMULATION("CIPHER_LOCK_EMULATION");

		private String value;

		AccessModeEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		public static AccessModeEnum fromValue(String text) {
			for (AccessModeEnum b : AccessModeEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}

		public static class Adapter extends TypeAdapter<AccessModeEnum> {
			@Override
			public void write(final JsonWriter jsonWriter, final AccessModeEnum enumeration) throws IOException {
				jsonWriter.value(enumeration.getValue());
			}

			@Override
			public AccessModeEnum read(final JsonReader jsonReader) throws IOException {
				String value = jsonReader.nextString();
				return AccessModeEnum.fromValue(String.valueOf(value));
			}
		}
	}

	@SerializedName("accessMode")
	private AccessModeEnum accessMode = null;

	@SerializedName("antiPassbackTime")
	private Integer antiPassbackTime = null;

	@SerializedName("enablePairing")
	private Boolean enablePairing = false;

	@SerializedName("validateVehicle")
	private Boolean validateVehicle = false;

	@SerializedName("activations")
	private List<ReaderActivation> activations = null;

	@SerializedName("extensibleConfiguration")
	private ExtensibleConfiguration extensibleConfiguration = null;

	@SerializedName("urnSensor")
	private Long urnSensor = null;

	@SerializedName("urnSensorTime")
	private Integer urnSensorTime = null;

	public ReaderDevice id(Long id) {
		this.id = id;
		return this;
	}

	/**
	 * Identificador do dispositivo
	 * 
	 * @return id
	 **/
	@Schema(description = "Identificador do dispositivo")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReaderDevice areaId(Long areaId) {
		this.areaId = areaId;
		return this;
	}

	/**
	 * Identificador da área
	 * 
	 * @return areaId
	 **/
	@Schema(description = "Identificador da área")
	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public ReaderDevice output(List<OutputDevice> output) {
		this.output = output;
		return this;
	}

	public ReaderDevice addOutputItem(OutputDevice outputItem) {
		if (this.output == null) {
			this.output = new ArrayList<>();
		}
		this.output.add(outputItem);
		return this;
	}

	/**
	 * Dispositivo de saída
	 * 
	 * @return output
	 **/
	@Schema(description = "Dispositivo de saída")
	public List<OutputDevice> getOutput() {
		return output;
	}

	public void setOutput(List<OutputDevice> output) {
		this.output = output;
	}

	public ReaderDevice intput(List<InputDevice> intput) {
		this.intput = intput;
		return this;
	}

	public ReaderDevice addIntputItem(InputDevice intputItem) {
		if (this.intput == null) {
			this.intput = new ArrayList<>();
		}
		this.intput.add(intputItem);
		return this;
	}

	/**
	 * Dispositivo de entrada
	 * 
	 * @return intput
	 **/
	@Schema(description = "Dispositivo de entrada")
	public List<InputDevice> getIntput() {
		return intput;
	}

	public void setIntput(List<InputDevice> intput) {
		this.intput = intput;
	}

	public ReaderDevice readerTechnology(ReaderTechnologyEnum readerTechnology) {
		this.readerTechnology = readerTechnology;
		return this;
	}

	/**
	 * Get readerTechnology
	 * 
	 * @return readerTechnology
	 **/
	@Schema(description = "")
	public ReaderTechnologyEnum getReaderTechnology() {
		return readerTechnology;
	}

	public void setReaderTechnology(ReaderTechnologyEnum readerTechnology) {
		this.readerTechnology = readerTechnology;
	}

	public ReaderDevice readerDirection(AccessDirectionEnum readerDirection) {
		this.readerDirection = readerDirection;
		return this;
	}

	/**
	 * Get readerDirection
	 * 
	 * @return readerDirection
	 **/
	@Schema(description = "")
	public AccessDirectionEnum getReaderDirection() {
		return readerDirection;
	}

	public void setReaderDirection(AccessDirectionEnum readerDirection) {
		this.readerDirection = readerDirection;
	}

	public ReaderDevice biometricManufacturer(ManufacturerEnum biometricManufacturer) {
		this.biometricManufacturer = biometricManufacturer;
		return this;
	}

	/**
	 * Get biometricManufacturer
	 * 
	 * @return biometricManufacturer
	 **/
	@Schema(description = "")
	public ManufacturerEnum getBiometricManufacturer() {
		return biometricManufacturer;
	}

	public void setBiometricManufacturer(ManufacturerEnum biometricManufacturer) {
		this.biometricManufacturer = biometricManufacturer;
	}

	public ReaderDevice biometricValidation(BiometricValidationEnum biometricValidation) {
		this.biometricValidation = biometricValidation;
		return this;
	}

	/**
	 * Get biometricValidation
	 * 
	 * @return biometricValidation
	 **/
	@Schema(description = "")
	public BiometricValidationEnum getBiometricValidation() {
		return biometricValidation;
	}

	public void setBiometricValidation(BiometricValidationEnum biometricValidation) {
		this.biometricValidation = biometricValidation;
	}

	public ReaderDevice biometricSecurityLevel(Integer biometricSecurityLevel) {
		this.biometricSecurityLevel = biometricSecurityLevel;
		return this;
	}

	/**
	 * Nível de conferência
	 * 
	 * @return biometricSecurityLevel
	 **/
	@Schema(description = "Nível de conferência")
	public Integer getBiometricSecurityLevel() {
		return biometricSecurityLevel;
	}

	public void setBiometricSecurityLevel(Integer biometricSecurityLevel) {
		this.biometricSecurityLevel = biometricSecurityLevel;
	}

	public ReaderDevice biometricReaderId(Long biometricReaderId) {
		this.biometricReaderId = biometricReaderId;
		return this;
	}

	/**
	 * Leitora biométrica que será utilizado por este leitor para validar a
	 * biometria 1:1
	 * 
	 * @return biometricReaderId
	 **/
	@Schema(description = "Leitora biométrica que será utilizado por este leitor para validar a biometria 1:1")
	public Long getBiometricReaderId() {
		return biometricReaderId;
	}

	public void setBiometricReaderId(Long biometricReaderId) {
		this.biometricReaderId = biometricReaderId;
	}

	public ReaderDevice physicalAddress(Integer physicalAddress) {
		this.physicalAddress = physicalAddress;
		return this;
	}

	/**
	 * Endereço
	 * 
	 * @return physicalAddress
	 **/
	@Schema(description = "Endereço")
	public Integer getPhysicalAddress() {
		return physicalAddress;
	}

	public void setPhysicalAddress(Integer physicalAddress) {
		this.physicalAddress = physicalAddress;
	}

	public ReaderDevice areaLeaving(Long areaLeaving) {
		this.areaLeaving = areaLeaving;
		return this;
	}

	/**
	 * Local físico de origem
	 * 
	 * @return areaLeaving
	 **/
	@Schema(description = "Local físico de origem")
	public Long getAreaLeaving() {
		return areaLeaving;
	}

	public void setAreaLeaving(Long areaLeaving) {
		this.areaLeaving = areaLeaving;
	}

	public ReaderDevice areaEntering(Long areaEntering) {
		this.areaEntering = areaEntering;
		return this;
	}

	/**
	 * Local físico de destino
	 * 
	 * @return areaEntering
	 **/
	@Schema(description = "Local físico de destino")
	public Long getAreaEntering() {
		return areaEntering;
	}

	public void setAreaEntering(Long areaEntering) {
		this.areaEntering = areaEntering;
	}

	public ReaderDevice cardFormatList(List<Long> cardFormatList) {
		this.cardFormatList = cardFormatList;
		return this;
	}

	public ReaderDevice addCardFormatListItem(Long cardFormatListItem) {
		if (this.cardFormatList == null) {
			this.cardFormatList = new ArrayList<>();
		}
		this.cardFormatList.add(cardFormatListItem);
		return this;
	}

	/**
	 * Lista de formato de cartão
	 * 
	 * @return cardFormatList
	 **/
	@Schema(description = "Lista de formato de cartão")
	public List<Long> getCardFormatList() {
		return cardFormatList;
	}

	public void setCardFormatList(List<Long> cardFormatList) {
		this.cardFormatList = cardFormatList;
	}

	public ReaderDevice strikeTime(Integer strikeTime) {
		this.strikeTime = strikeTime;
		return this;
	}

	/**
	 * Tempo
	 * 
	 * @return strikeTime
	 **/
	@Schema(description = "Tempo")
	public Integer getStrikeTime() {
		return strikeTime;
	}

	public void setStrikeTime(Integer strikeTime) {
		this.strikeTime = strikeTime;
	}

	public ReaderDevice heldOpenTime(Integer heldOpenTime) {
		this.heldOpenTime = heldOpenTime;
		return this;
	}

	/**
	 * Tempo aberto
	 * 
	 * @return heldOpenTime
	 **/
	@Schema(description = "Tempo aberto")
	public Integer getHeldOpenTime() {
		return heldOpenTime;
	}

	public void setHeldOpenTime(Integer heldOpenTime) {
		this.heldOpenTime = heldOpenTime;
	}

	public ReaderDevice offlineMode(OfflineModeEnum offlineMode) {
		this.offlineMode = offlineMode;
		return this;
	}

	/**
	 * Get offlineMode
	 * 
	 * @return offlineMode
	 **/
	@Schema(description = "")
	public OfflineModeEnum getOfflineMode() {
		return offlineMode;
	}

	public void setOfflineMode(OfflineModeEnum offlineMode) {
		this.offlineMode = offlineMode;
	}

	public ReaderDevice accessMode(AccessModeEnum accessMode) {
		this.accessMode = accessMode;
		return this;
	}

	/**
	 * Get accessMode
	 * 
	 * @return accessMode
	 **/
	@Schema(description = "")
	public AccessModeEnum getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(AccessModeEnum accessMode) {
		this.accessMode = accessMode;
	}

	public ReaderDevice antiPassbackTime(Integer antiPassbackTime) {
		this.antiPassbackTime = antiPassbackTime;
		return this;
	}

	/**
	 * É o tempo mínimo de acesso para o mesmo destino, a fim de evitar que duas
	 * pessoas utilizem o mesmo cartão para acessar o mesmo local
	 * 
	 * @return antiPassbackTime
	 **/
	@Schema(description = "É o tempo mínimo de acesso para o mesmo destino, a fim de evitar que duas pessoas utilizem o mesmo cartão para acessar o mesmo local")
	public Integer getAntiPassbackTime() {
		return antiPassbackTime;
	}

	public void setAntiPassbackTime(Integer antiPassbackTime) {
		this.antiPassbackTime = antiPassbackTime;
	}

	public ReaderDevice enablePairing(Boolean enablePairing) {
		this.enablePairing = enablePairing;
		return this;
	}

	/**
	 * Ativa o emparelhamento
	 * 
	 * @return enablePairing
	 **/
	@Schema(description = "Ativa o emparelhamento")
	public Boolean isEnablePairing() {
		return enablePairing;
	}

	public void setEnablePairing(Boolean enablePairing) {
		this.enablePairing = enablePairing;
	}

	public ReaderDevice validateVehicle(Boolean validateVehicle) {
		this.validateVehicle = validateVehicle;
		return this;
	}

	/**
	 * Valida veículo
	 * 
	 * @return validateVehicle
	 **/
	@Schema(description = "Valida veículo")
	public Boolean isValidateVehicle() {
		return validateVehicle;
	}

	public void setValidateVehicle(Boolean validateVehicle) {
		this.validateVehicle = validateVehicle;
	}

	public ReaderDevice activations(List<ReaderActivation> activations) {
		this.activations = activations;
		return this;
	}

	public ReaderDevice addActivationsItem(ReaderActivation activationsItem) {
		if (this.activations == null) {
			this.activations = new ArrayList<>();
		}
		this.activations.add(activationsItem);
		return this;
	}

	/**
	 * Leitoras ativas
	 * 
	 * @return activations
	 **/
	@Schema(description = "Leitoras ativas")
	public List<ReaderActivation> getActivations() {
		return activations;
	}

	public void setActivations(List<ReaderActivation> activations) {
		this.activations = activations;
	}

	public ReaderDevice extensibleConfiguration(ExtensibleConfiguration extensibleConfiguration) {
		this.extensibleConfiguration = extensibleConfiguration;
		return this;
	}

	/**
	 * Get extensibleConfiguration
	 * 
	 * @return extensibleConfiguration
	 **/
	@Schema(description = "")
	public ExtensibleConfiguration getExtensibleConfiguration() {
		return extensibleConfiguration;
	}

	public void setExtensibleConfiguration(ExtensibleConfiguration extensibleConfiguration) {
		this.extensibleConfiguration = extensibleConfiguration;
	}

	public ReaderDevice urnSensor(Long urnSensor) {
		this.urnSensor = urnSensor;
		return this;
	}

	/**
	 * Identificador do sensor de urna
	 * 
	 * @return urnSensor
	 **/
	@Schema(description = "Identificador do sensor de urna")
	public Long getUrnSensor() {
		return urnSensor;
	}

	public void setUrnSensor(Long urnSensor) {
		this.urnSensor = urnSensor;
	}

	public ReaderDevice urnSensorTime(Integer urnSensorTime) {
		this.urnSensorTime = urnSensorTime;
		return this;
	}

	/**
	 * Tempo sensor urna
	 * 
	 * @return urnSensorTime
	 **/
	@Schema(description = "Tempo sensor urna")
	public Integer getUrnSensorTime() {
		return urnSensorTime;
	}

	public void setUrnSensorTime(Integer urnSensorTime) {
		this.urnSensorTime = urnSensorTime;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ReaderDevice readerDevice = (ReaderDevice) o;
		return Objects.equals(this.id, readerDevice.id) && Objects.equals(this.areaId, readerDevice.areaId)
				&& Objects.equals(this.output, readerDevice.output) && Objects.equals(this.intput, readerDevice.intput)
				&& Objects.equals(this.readerTechnology, readerDevice.readerTechnology)
				&& Objects.equals(this.readerDirection, readerDevice.readerDirection)
				&& Objects.equals(this.biometricManufacturer, readerDevice.biometricManufacturer)
				&& Objects.equals(this.biometricValidation, readerDevice.biometricValidation)
				&& Objects.equals(this.biometricSecurityLevel, readerDevice.biometricSecurityLevel)
				&& Objects.equals(this.biometricReaderId, readerDevice.biometricReaderId)
				&& Objects.equals(this.physicalAddress, readerDevice.physicalAddress)
				&& Objects.equals(this.areaLeaving, readerDevice.areaLeaving)
				&& Objects.equals(this.areaEntering, readerDevice.areaEntering)
				&& Objects.equals(this.cardFormatList, readerDevice.cardFormatList)
				&& Objects.equals(this.strikeTime, readerDevice.strikeTime)
				&& Objects.equals(this.heldOpenTime, readerDevice.heldOpenTime)
				&& Objects.equals(this.offlineMode, readerDevice.offlineMode)
				&& Objects.equals(this.accessMode, readerDevice.accessMode)
				&& Objects.equals(this.antiPassbackTime, readerDevice.antiPassbackTime)
				&& Objects.equals(this.enablePairing, readerDevice.enablePairing)
				&& Objects.equals(this.validateVehicle, readerDevice.validateVehicle)
				&& Objects.equals(this.activations, readerDevice.activations)
				&& Objects.equals(this.extensibleConfiguration, readerDevice.extensibleConfiguration)
				&& Objects.equals(this.urnSensor, readerDevice.urnSensor)
				&& Objects.equals(this.urnSensorTime, readerDevice.urnSensorTime);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, areaId, output, intput, readerTechnology, readerDirection, biometricManufacturer,
				biometricValidation, biometricSecurityLevel, biometricReaderId, physicalAddress, areaLeaving,
				areaEntering, cardFormatList, strikeTime, heldOpenTime, offlineMode, accessMode, antiPassbackTime,
				enablePairing, validateVehicle, activations, extensibleConfiguration, urnSensor, urnSensorTime);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ReaderDevice {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    areaId: ").append(toIndentedString(areaId)).append("\n");
		sb.append("    output: ").append(toIndentedString(output)).append("\n");
		sb.append("    intput: ").append(toIndentedString(intput)).append("\n");
		sb.append("    readerTechnology: ").append(toIndentedString(readerTechnology)).append("\n");
		sb.append("    readerDirection: ").append(toIndentedString(readerDirection)).append("\n");
		sb.append("    biometricManufacturer: ").append(toIndentedString(biometricManufacturer)).append("\n");
		sb.append("    biometricValidation: ").append(toIndentedString(biometricValidation)).append("\n");
		sb.append("    biometricSecurityLevel: ").append(toIndentedString(biometricSecurityLevel)).append("\n");
		sb.append("    biometricReaderId: ").append(toIndentedString(biometricReaderId)).append("\n");
		sb.append("    physicalAddress: ").append(toIndentedString(physicalAddress)).append("\n");
		sb.append("    areaLeaving: ").append(toIndentedString(areaLeaving)).append("\n");
		sb.append("    areaEntering: ").append(toIndentedString(areaEntering)).append("\n");
		sb.append("    cardFormatList: ").append(toIndentedString(cardFormatList)).append("\n");
		sb.append("    strikeTime: ").append(toIndentedString(strikeTime)).append("\n");
		sb.append("    heldOpenTime: ").append(toIndentedString(heldOpenTime)).append("\n");
		sb.append("    offlineMode: ").append(toIndentedString(offlineMode)).append("\n");
		sb.append("    accessMode: ").append(toIndentedString(accessMode)).append("\n");
		sb.append("    antiPassbackTime: ").append(toIndentedString(antiPassbackTime)).append("\n");
		sb.append("    enablePairing: ").append(toIndentedString(enablePairing)).append("\n");
		sb.append("    validateVehicle: ").append(toIndentedString(validateVehicle)).append("\n");
		sb.append("    activations: ").append(toIndentedString(activations)).append("\n");
		sb.append("    extensibleConfiguration: ").append(toIndentedString(extensibleConfiguration)).append("\n");
		sb.append("    urnSensor: ").append(toIndentedString(urnSensor)).append("\n");
		sb.append("    urnSensorTime: ").append(toIndentedString(urnSensorTime)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

}
