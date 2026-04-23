package demo.deviceregistry.app.repository;

import demo.deviceregistry.app.entity.Device;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends CrudRepository<Device, Long>, DeviceRepositoryCustom {

    Optional<Device> findByDeviceId(String deviceId);

    boolean existsByDeviceId(String deviceId);

    void deleteByDeviceId(String deviceId);
}


