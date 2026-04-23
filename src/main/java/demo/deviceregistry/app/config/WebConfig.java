package demo.deviceregistry.app.config;

import demo.deviceregistry.app.dto.DeviceDtoState;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, DeviceDtoState.class, DeviceDtoState::fromValue);
    }
}

