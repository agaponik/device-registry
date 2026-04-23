package demo.deviceregistry.app.rest;

import demo.deviceregistry.app.dto.DeviceCreateDto;
import demo.deviceregistry.app.dto.DeviceDto;
import demo.deviceregistry.app.dto.DeviceSearchCriteriaDto;
import demo.deviceregistry.app.dto.DeviceUpdateDto;
import demo.deviceregistry.app.dto.PageResponseDto;
import demo.deviceregistry.app.dto.DeviceDtoState;
import demo.deviceregistry.app.service.DeviceService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api")
public class DeviceRestController {

    private final DeviceService deviceService;

    public DeviceRestController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * Creates a new device.
     *
     * @param deviceDto the device creation payload containing name and brand
     * @return the created device; HTTP 201 Created
     */
    @PostMapping("/device")
    @ResponseStatus(CREATED)
    public DeviceDto createDevice(@RequestBody DeviceCreateDto deviceDto) {
        return deviceService.create(deviceDto);
    }

    /**
     * Retrieves a device by its unique identifier.
     *
     * @param deviceId the unique device identifier
     * @return the device with the given {@code deviceId}
     */
    @GetMapping("/device/{deviceId}")
    public DeviceDto getDeviceById(@PathVariable String deviceId) {
        return deviceService.getById(deviceId);
    }

    /**
     * Partially updates a device's name and/or brand.
     *
     * @param deviceId  the unique device identifier
     * @param updateDto the update payload; {@code null} fields are ignored
     * @return the updated device
     */
    @PatchMapping("/device/{deviceId}")
    public DeviceDto updateDevice(@PathVariable String deviceId, @RequestBody DeviceUpdateDto updateDto) {
        return deviceService.update(deviceId, updateDto);
    }

    /**
     * Transitions a device to a new state.
     *
     * @param deviceId the unique device identifier
     * @param newState the target state to set on the device
     * @return the device with the updated state
     */
    @PostMapping("/device/{deviceId}/state/{newState}")
    public DeviceDto updateDeviceState(@PathVariable String deviceId, @PathVariable DeviceDtoState newState) {
        return deviceService.updateState(deviceId, newState);
    }

    /**
     * Returns a paginated list of devices, optionally filtered by brand and/or state.
     *
     * @param brand optional brand filter
     * @param state optional state filter
     * @param page  zero-based page index (default: {@code 0})
     * @param size  maximum number of items per page (default: {@code 20})
     * @return a page of matching devices
     */
    @GetMapping("/device/list")
    public PageResponseDto<DeviceDto> listDevices(
            @RequestParam Optional<String> brand,
            @RequestParam Optional<DeviceDtoState> state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return deviceService.list(new DeviceSearchCriteriaDto(brand, state), page, size);
    }

    /**
     * Deletes a device by its unique identifier.
     *
     * @param deviceId the unique device identifier
     */
    @DeleteMapping("/device/{deviceId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteDevice(@PathVariable String deviceId) {
        deviceService.delete(deviceId);
    }
}
