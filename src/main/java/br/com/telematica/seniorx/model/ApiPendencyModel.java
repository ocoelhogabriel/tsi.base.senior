package br.com.telematica.seniorx.model;

public class ApiPendencyModel {
    
    private Long idDevice;
    
    private Long pendencyInternal;
    
    private String pendencyExternal;
    
    public ApiPendencyModel(Long idDevice, Long pendencySenior, String pendencyConex) {
        this.idDevice = idDevice;
        this.pendencyInternal = pendencySenior;
        this.pendencyExternal = pendencyConex;
    }
    
    public Long getIdDevice() {
        return this.idDevice;
    }
    
    public void setIdDevice(Long idDevice) {
        this.idDevice = idDevice;
    }
    
    public Long getPendencyInternal() {
        return pendencyInternal;
    }
    
    public void setPendencyInternal(Long pendencyInternal) {
        this.pendencyInternal = pendencyInternal;
    }
    
    public String getPendencyExternal() {
        return pendencyExternal;
    }
    
    public void setPendencyExternal(String pendencyExternal) {
        this.pendencyExternal = pendencyExternal;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ApiPendencyModel [");
        if (idDevice != null) {
            builder.append("idDevice=").append(idDevice).append(", ");
        }
        if (pendencyInternal != null) {
            builder.append("pendencyInternal=").append(pendencyInternal).append(", ");
        }
        if (pendencyExternal != null) {
            builder.append("pendencyExternal=").append(pendencyExternal);
        }
        builder.append("]");
        return builder.toString();
    }
    
}
