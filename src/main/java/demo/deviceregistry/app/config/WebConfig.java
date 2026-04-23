package demo.deviceregistry.app.config;

import demo.deviceregistry.app.dto.DeviceState;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, DeviceState.class, DeviceState::fromValue);
    }
}

