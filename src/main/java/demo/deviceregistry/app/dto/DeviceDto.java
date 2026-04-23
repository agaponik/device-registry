package demo.deviceregistry.app.dto;

import java.time.Instant;

public record DeviceDto(
        String deviceId,
        String name,
        String brand,
        DeviceDtoState state,
        Instant creationTime
) {
}

