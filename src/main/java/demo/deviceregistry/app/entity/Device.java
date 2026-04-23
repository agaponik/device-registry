package demo.deviceregistry.app.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("devices")
public class Device {

    @Id
    private Long id;

    @Column("device_id")
    private String deviceId;

    private String name;
    private String brand;
    private DeviceState state;
    private Instant creationTime;

    public Device() {
    }

    public Device(String deviceId, String name, String brand, DeviceState state, Instant creationTime) {
        this.deviceId = deviceId;
        this.name = name;
        this.brand = brand;
        this.state = state;
        this.creationTime = creationTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public DeviceState getState() { return state; }
    public void setState(DeviceState state) { this.state = state; }

    public Instant getCreationTime() { return creationTime; }
    public void setCreationTime(Instant creationTime) { this.creationTime = creationTime; }
}
