
package br.com.telematica.seniorx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * CardList
 */

public class CardList {
	@SerializedName("cardNumber")
	private Long cardNumber = null;

	/**
	 * Gets or Sets cardTechnology
	 */

	@SerializedName("cardTechnology")
	private CardTechnologyEnum cardTechnology = null;

	@SerializedName("reader")
	private List<Integer> reader = null;

	/**
	 * Gets or Sets ownerType
	 */
	@SerializedName("ownerType")
	private OwnerTypeEnum ownerType = null;

	@SerializedName("ownerId")
	private Long ownerId = null;

	public CardList cardNumber(Long cardNumber) {
		this.cardNumber = cardNumber;
		return this;
	}

	/**
	 * Número do cartão
	 * 
	 * @return cardNumber
	 **/
	@Schema(description = "Número do cartão")
	public Long getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(Long cardNumber) {
		this.cardNumber = cardNumber;
	}

	public CardList cardTechnology(CardTechnologyEnum cardTechnology) {
		this.cardTechnology = cardTechnology;
		return this;
	}

	/**
	 * Get cardTechnology
	 * 
	 * @return cardTechnology
	 **/
	@Schema(description = "")
	public CardTechnologyEnum getCardTechnology() {
		return cardTechnology;
	}

	public void setCardTechnology(CardTechnologyEnum cardTechnology) {
		this.cardTechnology = cardTechnology;
	}

	public CardList reader(List<Integer> reader) {
		this.reader = reader;
		return this;
	}

	public CardList addReaderItem(Integer readerItem) {
		if (this.reader == null) {
			this.reader = new ArrayList<Integer>();
		}
		this.reader.add(readerItem);
		return this;
	}

	/**
	 * Leitoras que o cartão possui acesso
	 * 
	 * @return reader
	 **/
	@Schema(description = "Leitoras que o cartão possui acesso")
	public List<Integer> getReader() {
		return reader;
	}

	public void setReader(List<Integer> reader) {
		this.reader = reader;
	}

	public CardList ownerType(OwnerTypeEnum ownerType) {
		this.ownerType = ownerType;
		return this;
	}

	/**
	 * Get ownerType
	 * 
	 * @return ownerType
	 **/
	@Schema(description = "")
	public OwnerTypeEnum getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(OwnerTypeEnum ownerType) {
		this.ownerType = ownerType;
	}

	public CardList ownerId(Long ownerId) {
		this.ownerId = ownerId;
		return this;
	}

	/**
	 * Identificador do proprietário da credencial (pessoa ou veículo)
	 * 
	 * @return ownerId
	 **/
	@Schema(description = "Identificador do proprietário da credencial (pessoa ou veículo)")
	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CardList cardList = (CardList) o;
		return Objects.equals(this.cardNumber, cardList.cardNumber)
				&& Objects.equals(this.cardTechnology, cardList.cardTechnology)
				&& Objects.equals(this.reader, cardList.reader) && Objects.equals(this.ownerType, cardList.ownerType)
				&& Objects.equals(this.ownerId, cardList.ownerId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cardNumber, cardTechnology, reader, ownerType, ownerId);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class CardList {\n");

		sb.append("    cardNumber: ").append(toIndentedString(cardNumber)).append("\n");
		sb.append("    cardTechnology: ").append(toIndentedString(cardTechnology)).append("\n");
		sb.append("    reader: ").append(toIndentedString(reader)).append("\n");
		sb.append("    ownerType: ").append(toIndentedString(ownerType)).append("\n");
		sb.append("    ownerId: ").append(toIndentedString(ownerId)).append("\n");
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
