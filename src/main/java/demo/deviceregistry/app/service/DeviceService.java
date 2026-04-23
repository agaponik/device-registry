package demo.deviceregistry.app.service;

import demo.deviceregistry.app.dto.DeviceCreateDto;
import demo.deviceregistry.app.dto.DeviceDto;
import demo.deviceregistry.app.dto.DeviceSearchCriteriaDto;
import demo.deviceregistry.app.dto.DeviceUpdateDto;
import demo.deviceregistry.app.dto.PageResponseDto;
import demo.deviceregistry.app.dto.DeviceState;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceService {

    private static final Instant SAMPLE_CREATION_TIME = Instant.parse("2026-04-23T12:00:00Z");

    public DeviceDto create(DeviceCreateDto deviceDto) {
        // TODO: replace with real implementation
        return new DeviceDto(
                UUID.randomUUID().toString(),
                deviceDto.name(),
                deviceDto.brand(),
                DeviceState.AVAILABLE,
                Instant.now()
        );
    }

    public DeviceDto getById(String id) {
        // TODO: replace with real implementation
        return new DeviceDto(
                id,
                "Warehouse Scanner",
                "Zebra",
                DeviceState.AVAILABLE,
                SAMPLE_CREATION_TIME
        );
    }

    public DeviceDto update(String id, DeviceUpdateDto updateDto) {
        // TODO: replace with real implementation
        DeviceDto existing = getById(id);
        return new DeviceDto(
                id,
                updateDto.name() != null ? updateDto.name() : existing.name(),
                updateDto.brand() != null ? updateDto.brand() : existing.brand(),
                existing.state(),
                existing.creationTime()
        );
    }

    public DeviceDto updateState(String id, DeviceState newState) {
        // TODO: replace with real implementation
        DeviceDto existing = getById(id);
        return new DeviceDto(
                id,
                existing.name(),
                existing.brand(),
                newState,
                existing.creationTime()
        );
    }

    public PageResponseDto<DeviceDto> list(DeviceSearchCriteriaDto criteria, int page, int size) {
        // TODO: replace with real implementation
        if (criteria.brand().isPresent()) {
            String brand = criteria.brand().get();
            List<DeviceDto> sample = List.of(
                    new DeviceDto("1", "Warehouse Scanner", brand, DeviceState.AVAILABLE, SAMPLE_CREATION_TIME)
            );
            return toPage(sample, page, size);
        }
        if (criteria.state().isPresent()) {
            DeviceState state = criteria.state().get();
            List<DeviceDto> sample = List.of(
                    new DeviceDto("1", "Warehouse Scanner", "Zebra", state, SAMPLE_CREATION_TIME)
            );
            return toPage(sample, page, size);
        }
        List<DeviceDto> sample = List.of(
                new DeviceDto("1", "Warehouse Scanner", "Zebra", DeviceState.AVAILABLE, SAMPLE_CREATION_TIME),
                new DeviceDto("2", "Handheld Terminal", "Honeywell", DeviceState.IN_USE, SAMPLE_CREATION_TIME)
        );
        return toPage(sample, page, size);
    }

    public void delete(String id) {
        // TODO: replace with real implementation
    }

    private PageResponseDto<DeviceDto> toPage(List<DeviceDto> all, int page, int size) {
        int total = all.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<DeviceDto> content = all.subList(fromIndex, toIndex);
        return new PageResponseDto<>(content, page, size, total, totalPages);
    }
}
