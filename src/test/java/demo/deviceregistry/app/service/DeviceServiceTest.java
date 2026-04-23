package demo.deviceregistry.app.service;

import demo.deviceregistry.app.dto.DeviceCreateDto;
import demo.deviceregistry.app.dto.DeviceDto;
import demo.deviceregistry.app.dto.DeviceDtoState;
import demo.deviceregistry.app.dto.DeviceSearchCriteriaDto;
import demo.deviceregistry.app.dto.DeviceUpdateDto;
import demo.deviceregistry.app.dto.PageResponseDto;
import demo.deviceregistry.app.entity.Device;
import demo.deviceregistry.app.entity.DeviceState;
import demo.deviceregistry.app.repository.DeviceRepository;
import demo.deviceregistry.app.repository.DeviceSearchFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private static final String DEVICE_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final Instant CREATION_TIME = Instant.parse("2026-04-23T12:00:00Z");

    private Device device;

    @BeforeEach
    void setUp() {
        device = new Device(DEVICE_ID, "Warehouse Scanner", "Zebra", DeviceState.AVAILABLE, CREATION_TIME);
        device.setId(1L);
    }

    // --- create ---

    @Test
    void create_shouldSaveDeviceAndReturnDto() {
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        DeviceCreateDto createDto = new DeviceCreateDto("Warehouse Scanner", "Zebra");
        DeviceDto result = deviceService.create(createDto);

        assertThat(result.name()).isEqualTo("Warehouse Scanner");
        assertThat(result.brand()).isEqualTo("Zebra");
        assertThat(result.state()).isEqualTo(DeviceDtoState.AVAILABLE);
        assertThat(result.deviceId()).isNotBlank();
        assertThat(result.creationTime()).isNotNull();

        ArgumentCaptor<Device> captor = ArgumentCaptor.forClass(Device.class);
        verify(deviceRepository).save(captor.capture());
        assertThat(captor.getValue().getState()).isEqualTo(DeviceState.AVAILABLE);
    }

    // --- getById ---

    @Test
    void getById_shouldReturnDtoWhenFound() {
        when(deviceRepository.findByDeviceId(DEVICE_ID)).thenReturn(Optional.of(device));

        DeviceDto result = deviceService.getById(DEVICE_ID);

        assertThat(result.deviceId()).isEqualTo(DEVICE_ID);
        assertThat(result.name()).isEqualTo("Warehouse Scanner");
        assertThat(result.brand()).isEqualTo("Zebra");
        assertThat(result.state()).isEqualTo(DeviceDtoState.AVAILABLE);
        assertThat(result.creationTime()).isEqualTo(CREATION_TIME);
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(deviceRepository.findByDeviceId(DEVICE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.getById(DEVICE_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(DEVICE_ID);
    }

    // --- update ---

    @Test
    void update_shouldUpdateNameAndBrand() {
        when(deviceRepository.findByDeviceId(DEVICE_ID)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        DeviceUpdateDto updateDto = new DeviceUpdateDto("New Name", "New Brand");
        DeviceDto result = deviceService.update(DEVICE_ID, updateDto);

        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.brand()).isEqualTo("New Brand");
    }

    @Test
    void update_shouldUpdateOnlyName_whenBrandIsNull() {
        when(deviceRepository.findByDeviceId(DEVICE_ID)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        DeviceUpdateDto updateDto = new DeviceUpdateDto("New Name", null);
        DeviceDto result = deviceService.update(DEVICE_ID, updateDto);

        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.brand()).isEqualTo("Zebra");
    }

    @Test
    void update_shouldUpdateOnlyBrand_whenNameIsNull() {
        when(deviceRepository.findByDeviceId(DEVICE_ID)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        DeviceUpdateDto updateDto = new DeviceUpdateDto(null, "New Brand");
        DeviceDto result = deviceService.update(DEVICE_ID, updateDto);

        assertThat(result.name()).isEqualTo("Warehouse Scanner");
        assertThat(result.brand()).isEqualTo("New Brand");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(deviceRepository.findByDeviceId(DEVICE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.update(DEVICE_ID, new DeviceUpdateDto("x", "y")))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(DEVICE_ID);
    }

    // --- updateState ---

    @Test
    void updateState_shouldChangeState() {
        when(deviceRepository.findByDeviceId(DEVICE_ID)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        DeviceDto result = deviceService.updateState(DEVICE_ID, DeviceDtoState.IN_USE);

        assertThat(result.state()).isEqualTo(DeviceDtoState.IN_USE);
    }

    @Test
    void updateState_shouldThrowWhenNotFound() {
        when(deviceRepository.findByDeviceId(DEVICE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.updateState(DEVICE_ID, DeviceDtoState.IN_USE))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(DEVICE_ID);
    }

    // --- list ---

    @Test
    void list_shouldReturnPagedResults() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.empty(), Optional.empty());
        when(deviceRepository.countByFilter(filter)).thenReturn(2L);
        when(deviceRepository.findByFilter(eq(filter), eq(0), eq(10))).thenReturn(List.of(device));

        DeviceSearchCriteriaDto criteria = new DeviceSearchCriteriaDto(Optional.empty(), Optional.empty());
        PageResponseDto<DeviceDto> result = deviceService.list(criteria, 0, 10);

        assertThat(result.data()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(2L);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);
    }

    @Test
    void list_shouldFilterByBrand() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.of("Zebra"), Optional.empty());
        when(deviceRepository.countByFilter(filter)).thenReturn(1L);
        when(deviceRepository.findByFilter(eq(filter), eq(0), eq(20))).thenReturn(List.of(device));

        DeviceSearchCriteriaDto criteria = new DeviceSearchCriteriaDto(Optional.of("Zebra"), Optional.empty());
        PageResponseDto<DeviceDto> result = deviceService.list(criteria, 0, 20);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data().getFirst().brand()).isEqualTo("Zebra");
    }

    @Test
    void list_shouldFilterByState() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.empty(), Optional.of(DeviceState.AVAILABLE));
        when(deviceRepository.countByFilter(filter)).thenReturn(1L);
        when(deviceRepository.findByFilter(eq(filter), eq(0), eq(20))).thenReturn(List.of(device));

        DeviceSearchCriteriaDto criteria = new DeviceSearchCriteriaDto(Optional.empty(), Optional.of(DeviceDtoState.AVAILABLE));
        PageResponseDto<DeviceDto> result = deviceService.list(criteria, 0, 20);

        assertThat(result.data()).hasSize(1);
        assertThat(result.data().getFirst().state()).isEqualTo(DeviceDtoState.AVAILABLE);
    }

    // --- delete ---

    @Test
    void delete_shouldCallRepositoryDelete() {
        when(deviceRepository.existsByDeviceId(DEVICE_ID)).thenReturn(true);

        deviceService.delete(DEVICE_ID);

        verify(deviceRepository).deleteByDeviceId(DEVICE_ID);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(deviceRepository.existsByDeviceId(DEVICE_ID)).thenReturn(false);

        assertThatThrownBy(() -> deviceService.delete(DEVICE_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(DEVICE_ID);

        verify(deviceRepository, never()).deleteByDeviceId(any());
    }
}

