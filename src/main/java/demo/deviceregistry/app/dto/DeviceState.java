package demo.deviceregistry.app.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DeviceState {
    AVAILABLE("available"),
    IN_USE("in-use"),
    INACTIVE("inactive");

    private final String value;

    DeviceState(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DeviceState fromValue(String value) {
        for (DeviceState state : values()) {
            if (state.value.equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown device state: " + value);
    }
}

