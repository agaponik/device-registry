package demo.deviceregistry.app.dto;

import java.time.Instant;

public record DeviceDto(
        String id,
        String name,
        String brand,
        DeviceState state,
        Instant creationTime
) {
}

