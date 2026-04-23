package demo.deviceregistry.app.entity;

public enum DeviceState {
    AVAILABLE("A"),
    IN_USE("U"),
    INACTIVE("I");

    private final String code;

    DeviceState(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static DeviceState fromCode(String code) {
        for (DeviceState state : values()) {
            if (state.code.equalsIgnoreCase(code)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown device state code: " + code);
    }
}

