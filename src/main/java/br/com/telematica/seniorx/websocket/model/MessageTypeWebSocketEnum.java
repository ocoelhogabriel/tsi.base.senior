package br.com.telematica.seniorx.websocket.model;

public abstract class MessageTypeWebSocketEnum {

	private TypeWebSocketEnum pendencyType;

	public MessageTypeWebSocketEnum(TypeWebSocketEnum pendencyType) {
		super();
		this.pendencyType = pendencyType;
	}

	public TypeWebSocketEnum getPendencyType() {
		return pendencyType;
	}

	public void setPendencyType(TypeWebSocketEnum pendencyType) {
		this.pendencyType = pendencyType;
	}

	@Override
	public String toString() {
		return "pendencyType=" + pendencyType;
	}

}
