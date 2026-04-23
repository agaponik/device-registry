package demo.deviceregistry.app.repository;

import demo.deviceregistry.app.entity.Device;

import java.util.List;

public interface DeviceRepositoryCustom {

    List<Device> findByFilter(DeviceSearchFilter filter, int offset, int limit);

    long countByFilter(DeviceSearchFilter filter);
}
