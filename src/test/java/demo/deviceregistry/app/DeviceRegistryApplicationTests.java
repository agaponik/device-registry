package demo.deviceregistry.app;

import demo.deviceregistry.app.repository.DeviceRepository;
import demo.deviceregistry.app.rest.DeviceRestController;
import demo.deviceregistry.app.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.sql.init.mode=never",
        "spring.autoconfigure.exclude=org.springframework.boot.data.jdbc.autoconfigure.DataJdbcRepositoriesAutoConfiguration"
})
class DeviceRegistryApplicationTests {

    @MockitoBean
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceRestController deviceRestController;

    @Test
    void contextLoads() {
        assertThat(deviceService).isNotNull();
        assertThat(deviceRestController).isNotNull();
    }

}
