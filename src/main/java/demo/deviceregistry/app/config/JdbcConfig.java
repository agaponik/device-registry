package demo.deviceregistry.app.config;

import demo.deviceregistry.app.entity.DeviceState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

import java.util.List;

@Configuration
public class JdbcConfig {

    @Bean
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(List.of(
                new DeviceStateWritingConverter(),
                new DeviceStateReadingConverter()
        ));
    }

    @WritingConverter
    static class DeviceStateWritingConverter implements Converter<DeviceState, String> {
        @Override
        public String convert(DeviceState source) {
            return source.getCode();
        }
    }

    @ReadingConverter
    static class DeviceStateReadingConverter implements Converter<String, DeviceState> {
        @Override
        public DeviceState convert(String source) {
            return DeviceState.fromCode(source);
        }
    }
}




