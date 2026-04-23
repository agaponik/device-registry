package demo.deviceregistry.app.dto;

import java.util.Optional;

public record DeviceSearchCriteriaDto(
        Optional<String> brand,
        Optional<DeviceState> state
) {
}

