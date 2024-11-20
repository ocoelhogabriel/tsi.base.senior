
package br.com.telematica.seniorx.model;

import java.io.IOException;
import java.util.Objects;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ClockIn
 */

public class ClockIn {
	@SerializedName("deviceId")
	private Long deviceId = null;

	@SerializedName("date")
	private String date = null;

	@SerializedName("timezoneOffset")
	private Integer timezoneOffset = null;

	/**
	 * Gets or Sets status
	 */
	@JsonAdapter(ClockStatusEnum.Adapter.class)
	public enum ClockStatusEnum {
		ONLINE("ONLINE"),

		OFFLINE("OFFLINE");

		private String value;

		ClockStatusEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		public static ClockStatusEnum fromValue(String text) {
			for (ClockStatusEnum b : ClockStatusEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}

		public static class Adapter extends TypeAdapter<ClockStatusEnum> {
			@Override
			public void write(final JsonWriter jsonWriter, final ClockStatusEnum enumeration) throws IOException {
				jsonWriter.value(enumeration.getValue());
			}

			@Override
			public ClockStatusEnum read(final JsonReader jsonReader) throws IOException {
				String value = jsonReader.nextString();
				return ClockStatusEnum.fromValue(String.valueOf(value));
			}
		}
	}

	@SerializedName("status")
	private ClockStatusEnum status = null;

	@SerializedName("nsr")
	private Long nsr = null;

	@SerializedName("pis")
	private Long pis = null;

	@SerializedName("cpf")
	private Long cpf = null;

	public ClockIn deviceId(Long deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	/**
	 * Identificador do dispositivo
	 * 
	 * @return deviceId
	 **/
	@Schema(description = "Identificador do dispositivo")
	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public ClockIn date(String date) {
		this.date = date;
		return this;
	}

	/**
	 * Data da notificação em UTC
	 * 
	 * @return date
	 **/
	@Schema(description = "Data da notificação em UTC")
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ClockIn timezoneOffset(Integer timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
		return this;
	}

	/**
	 * Offset em minutos
	 * 
	 * @return timezoneOffset
	 **/
	@Schema(description = "Offset em minutos")
	public Integer getTimezoneOffset() {
		return timezoneOffset;
	}

	public void setTimezoneOffset(Integer timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
	}

	public ClockIn status(ClockStatusEnum status) {
		this.status = status;
		return this;
	}

	/**
	 * Get status
	 * 
	 * @return status
	 **/
	@Schema(description = "")
	public ClockStatusEnum getStatus() {
		return status;
	}

	public void setStatus(ClockStatusEnum status) {
		this.status = status;
	}

	public ClockIn nsr(Long nsr) {
		this.nsr = nsr;
		return this;
	}

	/**
	 * Número sequencial de registro
	 * 
	 * @return nsr
	 **/
	@Schema(description = "Número sequencial de registro")
	public Long getNsr() {
		return nsr;
	}

	public void setNsr(Long nsr) {
		this.nsr = nsr;
	}

	public ClockIn pis(Long pis) {
		this.pis = pis;
		return this;
	}

	/**
	 * PIS da pessoa
	 * 
	 * @return pis
	 **/
	@Schema(description = "PIS da pessoa")
	public Long getPis() {
		return pis;
	}

	public void setPis(Long pis) {
		this.pis = pis;
	}

	public ClockIn cpf(Long cpf) {
		this.cpf = cpf;
		return this;
	}

	/**
	 * CPF da pessoa
	 * 
	 * @return cpf
	 **/
	@Schema(description = "CPF da pessoa")
	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ClockIn clockIn = (ClockIn) o;
		return Objects.equals(this.deviceId, clockIn.deviceId) && Objects.equals(this.date, clockIn.date)
				&& Objects.equals(this.timezoneOffset, clockIn.timezoneOffset)
				&& Objects.equals(this.status, clockIn.status) && Objects.equals(this.nsr, clockIn.nsr)
				&& Objects.equals(this.pis, clockIn.pis) && Objects.equals(this.cpf, clockIn.cpf);
	}

	@Override
	public int hashCode() {
		return Objects.hash(deviceId, date, timezoneOffset, status, nsr, pis, cpf);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");

		sb.append("    deviceId: ").append(toIndentedString(deviceId)).append("\n");
		sb.append("    date: ").append(toIndentedString(date)).append("\n");
		sb.append("    timezoneOffset: ").append(toIndentedString(timezoneOffset)).append("\n");
		sb.append("    status: ").append(toIndentedString(status)).append("\n");
		sb.append("    nsr: ").append(toIndentedString(nsr)).append("\n");
		sb.append("    pis: ").append(toIndentedString(pis)).append("\n");
		sb.append("    cpf: ").append(toIndentedString(cpf)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

}
