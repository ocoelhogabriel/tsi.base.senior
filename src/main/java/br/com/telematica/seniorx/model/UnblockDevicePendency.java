
package br.com.telematica.seniorx.model;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * UnblockDevicePendency
 */

public class UnblockDevicePendency {
    @SerializedName("pendencyId")
    private Long pendencyId = null;
    
    @SerializedName("managerDeviceId")
    private Long managerDeviceId = null;
    
    @SerializedName("deviceId")
    private Long deviceId = null;
    
    public UnblockDevicePendency pendencyId(Long pendencyId) {
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
    
    public UnblockDevicePendency managerDeviceId(Long managerDeviceId) {
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
    
    public UnblockDevicePendency deviceId(Long deviceId) {
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
    
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UnblockDevicePendency unblockDevicePendency = (UnblockDevicePendency) o;
        return Objects.equals(this.pendencyId, unblockDevicePendency.pendencyId) && Objects.equals(this.managerDeviceId, unblockDevicePendency.managerDeviceId)
                                        && Objects.equals(this.deviceId, unblockDevicePendency.deviceId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pendencyId, managerDeviceId, deviceId);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UnblockDevicePendency {\n");
        
        sb.append("    pendencyId: ").append(toIndentedString(pendencyId)).append("\n");
        sb.append("    managerDeviceId: ").append(toIndentedString(managerDeviceId)).append("\n");
        sb.append("    deviceId: ").append(toIndentedString(deviceId)).append("\n");
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
