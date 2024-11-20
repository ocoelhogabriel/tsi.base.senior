package br.com.telematica.seniorx.websocket.model;

public abstract class MessageTypeWebSocketEnum {

	public enum TypeWebSocketEnum {
		DEVICE_STATUS, DEVICE_DATE_TIME,

		SET_DEVICE_EMERGENCY, UNSET_DEVICE_EMERGENCY,

		INCLUDE_BIOMETRY, EXCLUDE_BIOMETRY, RESET_DEVICE,

		UPDATE_PERSON_REP, ALLOW_CARD_LIST, BIOMETRY_LIST,

		REMOVE_ALLOW_CARD_LIST, REMOVE_BIOMETRY_LIST,

		INCLUDE_CARD, EXCLUDE_CARD,

		DEVICE, REP_MANUFACTURER_UPDATED,

		LOAD_HOLIDAY_LIST, REMOVE_HOLIDAY_LIST,

		ACTIVATE_DEVICE_OUTPUT, COLLECT_EVENTS,

		DEVICE_INPUT_STATUS, DEACTIVATE_DEVICE_OUTPUT,

		PERSON_AREA_UPDATED, DATAMART_UPDATED, BLOCK_DEVICE,

		UNBLOCK_DEVICE, DEVICE_DISPLAY_MESSAGE, UPDATE_FIRMWARE,

		INCLUDE_PHOTO, EXCLUDE_PHOTO, CREDENTIAL_FACIAL_LIST;

	}

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
