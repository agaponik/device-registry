package demo.deviceregistry.app.rest;

import demo.deviceregistry.app.dto.DeviceCreateDto;
import demo.deviceregistry.app.dto.DeviceDto;
import demo.deviceregistry.app.dto.DeviceSearchCriteriaDto;
import demo.deviceregistry.app.dto.DeviceUpdateDto;
import demo.deviceregistry.app.dto.PageResponseDto;
import demo.deviceregistry.app.dto.DeviceState;
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

    @PostMapping("/device")
    @ResponseStatus(CREATED)
    public DeviceDto createDevice(@RequestBody DeviceCreateDto deviceDto) {
        return deviceService.create(deviceDto);
    }

    @GetMapping("/device/{id}")
    public DeviceDto getDeviceById(@PathVariable String id) {
        return deviceService.getById(id);
    }

    @PatchMapping("/device/{id}")
    public DeviceDto updateDevice(@PathVariable String id, @RequestBody DeviceUpdateDto updateDto) {
        return deviceService.update(id, updateDto);
    }

    @PostMapping("/device/{id}/state/{newState}")
    public DeviceDto updateDeviceState(@PathVariable String id, @PathVariable DeviceState newState) {
        return deviceService.updateState(id, newState);
    }

    @GetMapping("/device/list")
    public PageResponseDto<DeviceDto> listDevices(
            @RequestParam Optional<String> brand,
            @RequestParam Optional<DeviceState> state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return deviceService.list(new DeviceSearchCriteriaDto(brand, state), page, size);
    }

    @DeleteMapping("/device/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteDevice(@PathVariable String id) {
        deviceService.delete(id);
    }
}
