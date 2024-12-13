package br.com.telematica.seniorx.model;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

@JsonAdapter(AccessTypeEnum.Adapter.class)
public enum AccessTypeEnum {
                            ACCESS_VALID("ACCESS_VALID"),
                            ACCESS_DENIED_PERMISSION("ACCESS_DENIED_PERMISSION"),
                            ACCESS_DENIED_SITUATION("ACCESS_DENIED_SITUATION"),
                            ACCESS_DENIED_CARD_VALIDITY("ACCESS_DENIED_CARD_VALIDITY"),
                            ACCESS_DENIED_LEVEL_CONTROLER("ACCESS_DENIED_LEVEL_CONTROLER"),
                            ACCESS_DENIED_CREDIT_ACCESS("ACCESS_DENIED_CREDIT_ACCESS"),
                            ACCESS_DENIED_ROLE_RANGE("ACCESS_DENIED_ROLE_RANGE"),
                            ACCESS_DENIED_LOCAL_RANGE("ACCESS_DENIED_LOCAL_RANGE"),
                            ACCESS_DENIED_CARD_NOT_FOUND("ACCESS_DENIED_CARD_NOT_FOUND"),
                            ACCESS_VALID_ACCOMPANY("ACCESS_VALID_ACCOMPANY"),
                            ACCESS_DENIED_ACCOMPANY("ACCESS_DENIED_ACCOMPANY"),
                            ACCESS_DENIED_INVALID_AUTHORIZER("ACCESS_DENIED_INVALID_AUTHORIZER"),
                            ACCESS_DENIED_WAITING_FOR_NEXT_VALIDATION("ACCESS_DENIED_WAITING_FOR_NEXT_VALIDATION"),
                            ACCESS_DENIED_ANTI_PASSBACK("ACCESS_DENIED_ANTI_PASSBACK"),
                            ACCESS_DENIED_CREDIT_RANGE("ACCESS_DENIED_CREDIT_RANGE"),
                            ACCESS_DENIED_STOCKING_CONTROL("ACCESS_DENIED_STOCKING_CONTROL"),
                            ACCESS_DENIED_NOT_PARKING_SPACE_TYPE("ACCESS_DENIED_NOT_PARKING_SPACE_TYPE"),
                            ACCESS_DENIED_NOT_PARKING_SPACE("ACCESS_DENIED_NOT_PARKING_SPACE"),
                            ACCESS_COERCION("ACCESS_COERCION"),
                            ACCESS_DENIED_BIOMETRY("ACCESS_DENIED_BIOMETRY"),
                            ACCESS_DENIED_CARD_FORMAT_ERROR("ACCESS_DENIED_CARD_FORMAT_ERROR"),
                            ACCESS_DENIED_FACILITY_CODE("ACCESS_DENIED_FACILITY_CODE"),
                            ACCESS_DENIED_PASSWORD("ACCESS_DENIED_PASSWORD"),
                            ACCESS_DENIED_SECOND_CARD_NOT_PRESENTED("ACCESS_DENIED_SECOND_CARD_NOT_PRESENTED"),
                            ACCESS_DESISTENCE("ACCESS_DESISTENCE"),
                            ACCESS_VALID_AUTHORIZER("ACCESS_VALID_AUTHORIZER"),
                            ACCESS_VALID_FACILITY_CODE("ACCESS_VALID_FACILITY_CODE"),
                            ACCESS_VALID_FRAUD("ACCESS_VALID_FRAUD"),
                            ACCESS_VALID_OUT_REPOSE("ACCESS_VALID_OUT_REPOSE"),
                            ACCESS_DENIED("ACCESS_DENIED"),
                            ACCESS_DENIED_CREDENTIAL_NOT_FOUND("ACCESS_DENIED_CREDENTIAL_NOT_FOUND"),
                            ACCESS_DENIED_CREDENTIAL_VALIDITY("ACCESS_DENIED_CREDENTIAL_VALIDITY"),
                            ACCESS_DENIED_INVALID_VEHICLE_CREDENTIAL_FORMAT("ACCESS_DENIED_INVALID_VEHICLE_CREDENTIAL_FORMAT"),
                            ACCESS_DENIED_INVALID_VEHICLE_CARD_CREDENTIAL("ACCESS_DENIED_INVALID_VEHICLE_CARD_CREDENTIAL"),
                            ACCESS_DENIED_WAITING_VEHICLE("ACCESS_DENIED_WAITING_VEHICLE"),
                            ACCESS_DENIED_READER_NOT_VALIDATE_VEHICLE("ACCESS_DENIED_READER_NOT_VALIDATE_VEHICLE"),
                            ACCESS_DENIED_PERSON_NOT_ASSOCIATED_WITH_VEHICLE("ACCESS_DENIED_PERSON_NOT_ASSOCIATED_WITH_VEHICLE"),
                            ACCESS_DENIED_CUSTOM_VALIDATION("ACCESS_DENIED_CUSTOM_VALIDATION"),
                            ACCESS_DENIED_BLOCK_PROVISORY_ON_EXIT("ACCESS_DENIED_BLOCK_PROVISORY_ON_EXIT"),
                            ACCESS_DENIED_CONTROLS_IN_BETWEEN_WORKDAYS("ACCESS_DENIED_CONTROLS_IN_BETWEEN_WORKDAYS"),
                            ACCESS_DENIED_WITHOUT_MASK("ACCESS_DENIED_WITHOUT_MASK"),
                            ACCESS_DENIED_ABNORMAL_TEMPERATURE("ACCESS_DENIED_ABNORMAL_TEMPERATURE");
    
    private String value;
    
    AccessTypeEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    public static AccessTypeEnum fromValue(String text) {
        for (AccessTypeEnum b : AccessTypeEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
    
    public static class Adapter extends TypeAdapter<AccessTypeEnum> {
        @Override
        public void write(final JsonWriter jsonWriter, final AccessTypeEnum enumeration) throws IOException {
            jsonWriter.value(enumeration.getValue());
        }
        
        @Override
        public AccessTypeEnum read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return AccessTypeEnum.fromValue(String.valueOf(value));
        }
        
    }
    
}
