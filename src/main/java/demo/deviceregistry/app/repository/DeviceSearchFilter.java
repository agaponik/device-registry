package demo.deviceregistry.app.repository;

import demo.deviceregistry.app.entity.DeviceState;

import java.util.Optional;

public record DeviceSearchFilter(
    Optional<String> brand,
    Optional<DeviceState> state
) {
}
