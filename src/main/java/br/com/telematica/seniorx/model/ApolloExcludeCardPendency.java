
package br.com.telematica.seniorx.model;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ApolloExcludeCardPendency
 */

public class ApolloExcludeCardPendency {
    @SerializedName("pendencyId")
    private Long pendencyId = null;
    
    @SerializedName("managerDeviceId")
    private Long managerDeviceId = null;
    
    @SerializedName("card")
    private Long card = null;
    
    @SerializedName("personId")
    private Long personId = null;
    
    public ApolloExcludeCardPendency pendencyId(Long pendencyId) {
        this.pendencyId = pendencyId;
        return this;
    }
    
    /**
     * Identificador da pendência
     * 
     * @return pendencyId
     **/
    @Schema(description = "Identificador da pendência")
    public Long getPendencyId() {
        return pendencyId;
    }
    
    public void setPendencyId(Long pendencyId) {
        this.pendencyId = pendencyId;
    }
    
    public ApolloExcludeCardPendency managerDeviceId(Long managerDeviceId) {
        this.managerDeviceId = managerDeviceId;
        return this;
    }
    
    /**
     * Identificador do dispositivo gerenciador
     * 
     * @return managerDeviceId
     **/
    @Schema(description = "Identificador do dispositivo gerenciador")
    public Long getManagerDeviceId() {
        return managerDeviceId;
    }
    
    public void setManagerDeviceId(Long managerDeviceId) {
        this.managerDeviceId = managerDeviceId;
    }
    
    public ApolloExcludeCardPendency card(Long card) {
        this.card = card;
        return this;
    }
    
    /**
     * Número do cartão
     * 
     * @return card
     **/
    @Schema(description = "Número do cartão")
    public Long getCard() {
        return card;
    }
    
    public void setCard(Long card) {
        this.card = card;
    }
    
    public ApolloExcludeCardPendency personId(Long personId) {
        this.personId = personId;
        return this;
    }
    
    /**
     * Identificador da pessoa
     * 
     * @return personId
     **/
    @Schema(description = "Identificador da pessoa")
    public Long getPersonId() {
        return personId;
    }
    
    public void setPersonId(Long personId) {
        this.personId = personId;
    }
    
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApolloExcludeCardPendency apolloExcludeCardPendency = (ApolloExcludeCardPendency) o;
        return Objects.equals(this.pendencyId, apolloExcludeCardPendency.pendencyId) && Objects.equals(this.managerDeviceId, apolloExcludeCardPendency.managerDeviceId)
                                        && Objects.equals(this.card, apolloExcludeCardPendency.card) && Objects.equals(this.personId, apolloExcludeCardPendency.personId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pendencyId, managerDeviceId, card, personId);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ApolloExcludeCardPendency {\n");
        
        sb.append("    pendencyId: ").append(toIndentedString(pendencyId)).append("\n");
        sb.append("    managerDeviceId: ").append(toIndentedString(managerDeviceId)).append("\n");
        sb.append("    card: ").append(toIndentedString(card)).append("\n");
        sb.append("    personId: ").append(toIndentedString(personId)).append("\n");
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
