package demo.deviceregistry.app.rest;

import demo.deviceregistry.app.entity.Device;
import demo.deviceregistry.app.entity.DeviceState;
import demo.deviceregistry.app.repository.DeviceRepository;
import demo.deviceregistry.app.repository.DeviceSearchFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import demo.deviceregistry.app.service.DeviceService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceRestController.class)
@Import(DeviceService.class)
class DeviceRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceRepository deviceRepository;

    private static final String DEVICE_ID_1 = "550e8400-e29b-41d4-a716-446655440000";
    private static final String DEVICE_ID_2 = "660e8400-e29b-41d4-a716-446655440001";
    private static final Instant CREATION_TIME = Instant.parse("2026-04-23T12:00:00Z");

    private Device device1;
    private Device device2;

    @BeforeEach
    void setUp() {
        device1 = new Device(DEVICE_ID_1, "Warehouse Scanner", "Zebra", DeviceState.AVAILABLE, CREATION_TIME);
        device1.setId(1L);
        device2 = new Device(DEVICE_ID_2, "Handheld Terminal", "Honeywell", DeviceState.IN_USE, CREATION_TIME);
        device2.setId(2L);

        when(deviceRepository.findByDeviceId(DEVICE_ID_1)).thenReturn(Optional.of(device1));
        when(deviceRepository.findByDeviceId(DEVICE_ID_2)).thenReturn(Optional.of(device2));
        when(deviceRepository.existsByDeviceId(DEVICE_ID_1)).thenReturn(true);
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        // brand filter
        DeviceSearchFilter brandFilter = new DeviceSearchFilter(Optional.of("Zebra"), Optional.empty());
        when(deviceRepository.countByFilter(eq(brandFilter))).thenReturn(1L);
        when(deviceRepository.findByFilter(eq(brandFilter), anyInt(), anyInt())).thenReturn(List.of(device1));
        // state filter
        DeviceSearchFilter stateFilter = new DeviceSearchFilter(Optional.empty(), Optional.of(DeviceState.AVAILABLE));
        when(deviceRepository.countByFilter(eq(stateFilter))).thenReturn(1L);
        when(deviceRepository.findByFilter(eq(stateFilter), anyInt(), anyInt())).thenReturn(List.of(device1));
    }

    @Test
    void shouldReturnDeviceById() throws Exception {
        mockMvc.perform(get("/api/device/" + DEVICE_ID_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(DEVICE_ID_1))
                .andExpect(jsonPath("$.name").value("Warehouse Scanner"))
                .andExpect(jsonPath("$.brand").value("Zebra"))
                .andExpect(jsonPath("$.state").value("available"))
                .andExpect(jsonPath("$.creationTime").value("2026-04-23T12:00:00Z"));
    }

    @Test
    void shouldCreateDevice() throws Exception {
        mockMvc.perform(post("/api/device")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Warehouse Scanner",
                                  "brand": "Zebra"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.deviceId").isString())
                .andExpect(jsonPath("$.deviceId").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Warehouse Scanner"))
                .andExpect(jsonPath("$.brand").value("Zebra"))
                .andExpect(jsonPath("$.state").value("available"))
                .andExpect(jsonPath("$.creationTime").isString())
                .andExpect(jsonPath("$.creationTime").isNotEmpty());
    }

    @Test
    void shouldUpdateDevice() throws Exception {
        mockMvc.perform(patch("/api/device/" + DEVICE_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Scanner",
                                  "brand": "Honeywell"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(DEVICE_ID_1))
                .andExpect(jsonPath("$.name").value("Updated Scanner"))
                .andExpect(jsonPath("$.brand").value("Honeywell"))
                .andExpect(jsonPath("$.state").value("available"))
                .andExpect(jsonPath("$.creationTime").value("2026-04-23T12:00:00Z"));
    }

    @Test
    void shouldUpdateDeviceWithPartialData() throws Exception {
        mockMvc.perform(patch("/api/device/" + DEVICE_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Partially Updated Scanner",
                                  "brand": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(DEVICE_ID_1))
                .andExpect(jsonPath("$.name").value("Partially Updated Scanner"))
                .andExpect(jsonPath("$.brand").value("Zebra"));
    }

    @Test
    void shouldUpdateDeviceStatus() throws Exception {
        String newState = "in-use";
        mockMvc.perform(post("/api/device/" + DEVICE_ID_1 + "/state/" + newState))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceId").value(DEVICE_ID_1))
                .andExpect(jsonPath("$.state").value(newState))
                .andExpect(jsonPath("$.name").value("Warehouse Scanner"))
                .andExpect(jsonPath("$.brand").value("Zebra"))
                .andExpect(jsonPath("$.creationTime").value("2026-04-23T12:00:00Z"));
    }

    @Test
    void shouldReturnAllDevices() throws Exception {
        // no filter
        DeviceSearchFilter noFilter = new DeviceSearchFilter(Optional.empty(), Optional.empty());
        when(deviceRepository.countByFilter(eq(noFilter))).thenReturn(2L);
        when(deviceRepository.findByFilter(eq(noFilter), anyInt(), anyInt())).thenReturn(List.of(device1, device2));

        mockMvc.perform(get("/api/device/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Warehouse Scanner"))
                .andExpect(jsonPath("$.data[0].brand").value("Zebra"))
                .andExpect(jsonPath("$.data[0].state").value("available"))
                .andExpect(jsonPath("$.data[1].name").value("Handheld Terminal"))
                .andExpect(jsonPath("$.data[1].brand").value("Honeywell"))
                .andExpect(jsonPath("$.data[1].state").value("in-use"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldReturnAllDevicesWithCustomPagination() throws Exception {
        //no filter but page limit
        DeviceSearchFilter noFilter = new DeviceSearchFilter(Optional.empty(), Optional.empty());
        when(deviceRepository.countByFilter(eq(noFilter))).thenReturn(1L);
        when(deviceRepository.findByFilter(eq(noFilter), anyInt(), anyInt())).thenReturn(List.of(device1));

        mockMvc.perform(get("/api/device/list")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldReturnDevicesByBrand() throws Exception {
        mockMvc.perform(get("/api/device/list").param("brand", "Zebra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].brand").value("Zebra"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldReturnDevicesByState() throws Exception {
        mockMvc.perform(get("/api/device/list").param("state", "available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].state").value("available"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldDeleteDevice() throws Exception {
        mockMvc.perform(delete("/api/device/" + DEVICE_ID_1))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn409WhenUpdatingDeviceInUse() throws Exception {
        mockMvc.perform(patch("/api/device/" + DEVICE_ID_2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "New Name",
                                  "brand": "New Brand"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Device cannot be updated while it is in use: " + DEVICE_ID_2));
    }

    @Test
    void shouldReturn409WhenDeletingDeviceInUse() throws Exception {
        mockMvc.perform(delete("/api/device/" + DEVICE_ID_2))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Device cannot be deleted while it is in use: " + DEVICE_ID_2));
    }
}
