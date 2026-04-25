package demo.deviceregistry.app.service;

import demo.deviceregistry.app.dto.DeviceCreateDto;
import demo.deviceregistry.app.dto.DeviceDto;
import demo.deviceregistry.app.dto.DeviceSearchCriteriaDto;
import demo.deviceregistry.app.dto.DeviceUpdateDto;
import demo.deviceregistry.app.dto.PageResponseDto;
import demo.deviceregistry.app.dto.DeviceDtoState;
import demo.deviceregistry.app.entity.Device;
import demo.deviceregistry.app.entity.DeviceState;
import demo.deviceregistry.app.repository.DeviceRepository;
import demo.deviceregistry.app.repository.DeviceSearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Transactional
@Service
public class DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * Creates a new device with the given details and persists it.
     * The device is initialized with a randomly generated ID, the current timestamp,
     * and {@link DeviceState#AVAILABLE} as its initial state.
     *
     * @param deviceDto the data required to create the device
     * @return the created device as a {@link DeviceDto}
     */
    public DeviceDto create(DeviceCreateDto deviceDto) {
        Device device = new Device(
                UUID.randomUUID().toString(),
                deviceDto.name(),
                deviceDto.brand(),
                DeviceState.AVAILABLE,
                Instant.now()
        );
        Device saved = deviceRepository.save(device);
        return toDeviceDto(saved);
    }

    /**
     * Retrieves a device by its unique identifier.
     *
     * @param deviceId the unique ID of the device
     * @return the matching device as a {@link DeviceDto}
     * @throws java.util.NoSuchElementException if no device with the given ID exists
     */
    public DeviceDto getById(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> {
                    log.warn("Device not found: {}", deviceId);
                    return new NoSuchElementException("Device not found: " + deviceId);
                });
        return toDeviceDto(device);
    }

    /**
     * Updates the name and/or brand of an existing device.
     * Only non-null fields in the update DTO are applied.
     *
     * @param deviceId  the unique ID of the device to update
     * @param updateDto the fields to update; {@code null} values are ignored
     * @return the updated device as a {@link DeviceDto}
     * @throws java.util.NoSuchElementException if no device with the given ID exists
     * @throws IllegalStateException            if the device is currently in use
     */
    public DeviceDto update(String deviceId, DeviceUpdateDto updateDto) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> {
                    log.warn("Device not found: {}", deviceId);
                    return new NoSuchElementException("Device not found: " + deviceId);
                });
        if (DeviceState.IN_USE.equals(device.getState())) {
            log.warn("Device cannot be updated while it is in use: {}", deviceId);
            throw new IllegalStateException("Device cannot be updated while it is in use: " + deviceId);
        }
        if (updateDto.name() != null) device.setName(updateDto.name());
        if (updateDto.brand() != null) device.setBrand(updateDto.brand());
        Device saved = deviceRepository.save(device);
        return toDeviceDto(saved);
    }

    /**
     * Updates the state of an existing device.
     *
     * @param deviceId the unique ID of the device
     * @param newState the new state to assign to the device
     * @return the updated device as a {@link DeviceDto}
     * @throws java.util.NoSuchElementException if no device with the given ID exists
     */
    public DeviceDto updateState(String deviceId, DeviceDtoState newState) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> {
                    log.warn("Device not found: {}", deviceId);
                    return new NoSuchElementException("Device not found: " + deviceId);
                });
        device.setState(DeviceState.valueOf(newState.name()));
        Device saved = deviceRepository.save(device);
        return toDeviceDto(saved);
    }

    /**
     * Returns a paginated list of devices matching the given search criteria.
     *
     * @param criteria filters to apply (e.g. brand, state); {@code null} or empty criteria return all devices
     * @param page     zero-based page index
     * @param size     maximum number of devices per page
     * @return a {@link PageResponseDto} containing the matching devices and pagination metadata
     */
    public PageResponseDto<DeviceDto> list(DeviceSearchCriteriaDto criteria, int page, int size) {
        DeviceSearchFilter filter = toFilter(criteria);
        int offset = page * size;
        long total = deviceRepository.countByFilter(filter);
        List<Device> devices = deviceRepository.findByFilter(filter, offset, size);
        List<DeviceDto> dtos = devices.stream().map(this::toDeviceDto).toList();
        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        return new PageResponseDto<>(dtos, page, size, total, totalPages);
    }

    /**
     * Deletes the device with the given identifier.
     *
     * @param deviceId the unique ID of the device to delete
     * @throws java.util.NoSuchElementException if no device with the given ID exists
     * @throws IllegalStateException            if the device is currently in use
     */
    public void delete(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> {
                    log.warn("Device not found: {}", deviceId);
                    return new NoSuchElementException("Device not found: " + deviceId);
                });
        if (DeviceState.IN_USE.equals(device.getState())) {
            log.warn("Device cannot be deleted while it is in use: {}", deviceId);
            throw new IllegalStateException("Device cannot be deleted while it is in use: " + deviceId);
        }
        deviceRepository.deleteByDeviceId(deviceId);
    }

    private DeviceDto toDeviceDto(Device saved) {
        return new DeviceDto(
                saved.getDeviceId(),
                saved.getName(),
                saved.getBrand(),
                DeviceDtoState.valueOf(saved.getState().name()),
                saved.getCreationTime()
        );
    }

    private DeviceSearchFilter toFilter(DeviceSearchCriteriaDto criteria) {
        return new DeviceSearchFilter(
                criteria.brand(),
                criteria.state().map(s -> DeviceState.valueOf(s.name()))
        );
    }
}
