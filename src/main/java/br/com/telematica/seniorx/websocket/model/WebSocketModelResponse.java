package br.com.telematica.seniorx.websocket.model;

public class WebSocketModelResponse {
    
    private String driverId;
    
    private String deviceId;
    
    private String pendencyType;
    
    public String getDriverId() {
        return driverId;
    }
    
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getPendencyType() {
        return pendencyType;
    }
    
    public void setPendencyType(String pendencyType) {
        this.pendencyType = pendencyType;
    }
    
    @Override
    public String toString() {
        return "[driverId=" + driverId + ", deviceId=" + deviceId + ", pendencyType=" + pendencyType + "]";
    }
    
    public WebSocketModelResponse(String driverId, String deviceId, String pendencyType) {
        super();
        this.driverId = driverId;
        this.deviceId = deviceId;
        this.pendencyType = pendencyType;
    }
    
    public WebSocketModelResponse() {
        super();
        // TODO Auto-generated constructor stub
    }
    
}
