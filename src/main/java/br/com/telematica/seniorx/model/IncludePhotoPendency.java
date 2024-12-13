
package br.com.telematica.seniorx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * IncludePhotoPendency
 */

public class IncludePhotoPendency {
    @SerializedName("pendencyId")
    private Long pendencyId = null;
    
    @SerializedName("managerDeviceId")
    private Long managerDeviceId = null;
    
    @SerializedName("personId")
    private Long personId = null;
    
    @SerializedName("personName")
    private String personName = null;
    
    @SerializedName("photoUrl")
    private String photoUrl = null;
    
    @SerializedName("cardList")
    private List<CardInformation> cardList = null;
    
    public IncludePhotoPendency pendencyId(Long pendencyId) {
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
    
    public IncludePhotoPendency managerDeviceId(Long managerDeviceId) {
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
    
    public IncludePhotoPendency personId(Long personId) {
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
    
    public IncludePhotoPendency personName(String personName) {
        this.personName = personName;
        return this;
    }
    
    /**
     * Nome da pessoa
     * 
     * @return personName
     **/
    @Schema(description = "Nome da pessoa")
    public String getPersonName() {
        return personName;
    }
    
    public void setPersonName(String personName) {
        this.personName = personName;
    }
    
    public IncludePhotoPendency photoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }
    
    /**
     * URL da foto
     * 
     * @return photoUrl
     **/
    @Schema(description = "URL da foto")
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    
    public IncludePhotoPendency cardList(List<CardInformation> cardList) {
        this.cardList = cardList;
        return this;
    }
    
    public IncludePhotoPendency addCardListItem(CardInformation cardListItem) {
        if (this.cardList == null) {
            this.cardList = new ArrayList<CardInformation>();
        }
        this.cardList.add(cardListItem);
        return this;
    }
    
    /**
     * Get cardList
     * 
     * @return cardList
     **/
    @Schema(description = "")
    public List<CardInformation> getCardList() {
        return cardList;
    }
    
    public void setCardList(List<CardInformation> cardList) {
        this.cardList = cardList;
    }
    
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IncludePhotoPendency includePhotoPendency = (IncludePhotoPendency) o;
        return Objects.equals(this.pendencyId, includePhotoPendency.pendencyId) && Objects.equals(this.managerDeviceId, includePhotoPendency.managerDeviceId)
                                        && Objects.equals(this.personId, includePhotoPendency.personId) && Objects.equals(this.personName, includePhotoPendency.personName)
                                        && Objects.equals(this.photoUrl, includePhotoPendency.photoUrl) && Objects.equals(this.cardList, includePhotoPendency.cardList);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pendencyId, managerDeviceId, personId, personName, photoUrl, cardList);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class IncludePhotoPendency {\n");
        
        sb.append("    pendencyId: ").append(toIndentedString(pendencyId)).append("\n");
        sb.append("    managerDeviceId: ").append(toIndentedString(managerDeviceId)).append("\n");
        sb.append("    personId: ").append(toIndentedString(personId)).append("\n");
        sb.append("    personName: ").append(toIndentedString(personName)).append("\n");
        sb.append("    photoUrl: ").append(toIndentedString(photoUrl)).append("\n");
        sb.append("    cardList: ").append(toIndentedString(cardList)).append("\n");
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
