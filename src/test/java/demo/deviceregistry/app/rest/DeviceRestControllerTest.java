package demo.deviceregistry.app.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import demo.deviceregistry.app.service.DeviceService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceRestController.class)
@Import(DeviceService.class)
class DeviceRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnDeviceById() throws Exception {
        String deviceId = "550e8400-e29b-41d4-a716-446655440000";
        mockMvc.perform(get("/api/device/" + deviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deviceId))
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
                                  "brand": "Zebra",
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Warehouse Scanner"))
                .andExpect(jsonPath("$.brand").value("Zebra"))
                .andExpect(jsonPath("$.state").value("available"))
                .andExpect(jsonPath("$.creationTime").isString())
                .andExpect(jsonPath("$.creationTime").isNotEmpty());
    }

    @Test
    void shouldUpdateDevice() throws Exception {
        String deviceId = "550e8400-e29b-41d4-a716-446655440000";
        mockMvc.perform(patch("/api/device/" + deviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Scanner",
                                  "brand": "Honeywell"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.name").value("Updated Scanner"))
                .andExpect(jsonPath("$.brand").value("Honeywell"))
                .andExpect(jsonPath("$.state").value("available"))
                .andExpect(jsonPath("$.creationTime").value("2026-04-23T12:00:00Z"));
    }

    @Test
    void shouldUpdateDeviceWithPartialData() throws Exception {
        String deviceId = "550e8400-e29b-41d4-a716-446655440000";
        mockMvc.perform(patch("/api/device/" + deviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Partially Updated Scanner",
                                  "brand": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deviceId))
                .andExpect(jsonPath("$.name").value("Partially Updated Scanner"))
                .andExpect(jsonPath("$.brand").value("Zebra"));
    }

    @Test
    void shouldUpdateDeviceStatus() throws Exception {
        String deviceId = "550e8400-e29b-41d4-a716-446655440000";
        String newState = "in-use";
        mockMvc.perform(post("/api/device/" + deviceId + "/state/" + newState))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deviceId))
                .andExpect(jsonPath("$.state").value(newState))
                .andExpect(jsonPath("$.name").value("Warehouse Scanner"))
                .andExpect(jsonPath("$.brand").value("Zebra"))
                .andExpect(jsonPath("$.creationTime").value("2026-04-23T12:00:00Z"));
    }

    @Test
    void shouldReturnAllDevices() throws Exception {
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
        mockMvc.perform(get("/api/device/list")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));
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
        String deviceId = "550e8400-e29b-41d4-a716-446655440000";
        mockMvc.perform(delete("/api/device/" + deviceId))
                .andExpect(status().isNoContent());
    }
}
