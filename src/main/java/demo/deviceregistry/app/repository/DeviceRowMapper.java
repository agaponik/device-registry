package demo.deviceregistry.app.repository;

import demo.deviceregistry.app.entity.Device;
import demo.deviceregistry.app.entity.DeviceState;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeviceRowMapper implements RowMapper<Device> {

    public static final DeviceRowMapper INSTANCE = new DeviceRowMapper();

    @Override
    public Device mapRow(ResultSet rs, int rowNum) throws SQLException {
        Device device = new Device(
                rs.getString("device_id"),
                rs.getString("name"),
                rs.getString("brand"),
                DeviceState.fromCode(rs.getString("state")),
                rs.getTimestamp("creation_time").toInstant()
        );
        device.setId(rs.getLong("id"));
        return device;
    }
}
