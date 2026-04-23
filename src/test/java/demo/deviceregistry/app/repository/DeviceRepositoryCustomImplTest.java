package demo.deviceregistry.app.repository;

import demo.deviceregistry.app.entity.Device;
import demo.deviceregistry.app.entity.DeviceState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceRepositoryCustomImplTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    private DeviceRepositoryCustomImpl repository;

    private static final Instant CREATION_TIME = Instant.parse("2026-04-23T12:00:00Z");

    private Device device;

    @BeforeEach
    void setUp() {
        repository = new DeviceRepositoryCustomImpl(jdbcTemplate);
        device = new Device("dev-001", "Scanner", "Zebra", DeviceState.AVAILABLE, CREATION_TIME);
        device.setId(1L);
    }

    // --- findByFilter ---

    @Test
    void findByFilter_noFilters_shouldQueryWithoutWhereClause() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.empty(), Optional.empty());
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), eq(DeviceRowMapper.INSTANCE)))
                .thenReturn(List.of(device));

        List<Device> result = repository.findByFilter(filter, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDeviceId()).isEqualTo("dev-001");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), eq(DeviceRowMapper.INSTANCE));
        assertThat(sqlCaptor.getValue()).doesNotContain("WHERE");
        assertThat(sqlCaptor.getValue()).contains("LIMIT :limit").contains("OFFSET :offset");
    }

    @Test
    void findByFilter_withBrandFilter_shouldContainBrandCondition() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.of("Zebra"), Optional.empty());
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), eq(DeviceRowMapper.INSTANCE)))
                .thenReturn(List.of(device));

        List<Device> result = repository.findByFilter(filter, 0, 10);

        assertThat(result).hasSize(1);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), paramsCaptor.capture(), eq(DeviceRowMapper.INSTANCE));

        assertThat(sqlCaptor.getValue()).contains("WHERE").contains("brand = :brand");
        assertThat(paramsCaptor.getValue().getValue("brand")).isEqualTo("Zebra");
        assertThat(paramsCaptor.getValue().getValue("limit")).isEqualTo(10);
        assertThat(paramsCaptor.getValue().getValue("offset")).isEqualTo(0);
    }

    @Test
    void findByFilter_withStateFilter_shouldContainStateCondition() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.empty(), Optional.of(DeviceState.IN_USE));
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), eq(DeviceRowMapper.INSTANCE)))
                .thenReturn(List.of());

        List<Device> result = repository.findByFilter(filter, 0, 5);

        assertThat(result).isEmpty();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), paramsCaptor.capture(), eq(DeviceRowMapper.INSTANCE));

        assertThat(sqlCaptor.getValue()).contains("WHERE").contains("state = :state");
        assertThat(paramsCaptor.getValue().getValue("state")).isEqualTo(DeviceState.IN_USE.getCode());
        assertThat(paramsCaptor.getValue().getValue("limit")).isEqualTo(5);
        assertThat(paramsCaptor.getValue().getValue("offset")).isEqualTo(0);
    }

    @Test
    void findByFilter_withBrandAndStateFilters_shouldContainBothConditions() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.of("Zebra"), Optional.of(DeviceState.AVAILABLE));
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), eq(DeviceRowMapper.INSTANCE)))
                .thenReturn(List.of(device));

        List<Device> result = repository.findByFilter(filter, 10, 20);

        assertThat(result).hasSize(1);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), paramsCaptor.capture(), eq(DeviceRowMapper.INSTANCE));

        assertThat(sqlCaptor.getValue())
                .contains("WHERE")
                .contains("brand = :brand")
                .contains("state = :state")
                .contains("AND");
        assertThat(paramsCaptor.getValue().getValue("brand")).isEqualTo("Zebra");
        assertThat(paramsCaptor.getValue().getValue("state")).isEqualTo(DeviceState.AVAILABLE.getCode());
        assertThat(paramsCaptor.getValue().getValue("limit")).isEqualTo(20);
        assertThat(paramsCaptor.getValue().getValue("offset")).isEqualTo(10);
    }

    @Test
    void findByFilter_shouldRespectOffsetAndLimit() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.empty(), Optional.empty());
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), eq(DeviceRowMapper.INSTANCE)))
                .thenReturn(List.of());

        repository.findByFilter(filter, 40, 20);

        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), eq(DeviceRowMapper.INSTANCE));
        assertThat(paramsCaptor.getValue().getValue("offset")).isEqualTo(40);
        assertThat(paramsCaptor.getValue().getValue("limit")).isEqualTo(20);
    }

    // --- countByFilter ---

    @Test
    void countByFilter_noFilters_shouldQueryWithoutWhereClause() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.empty(), Optional.empty());
        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                .thenReturn(5L);

        long count = repository.countByFilter(filter);

        assertThat(count).isEqualTo(5L);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForObject(sqlCaptor.capture(), any(MapSqlParameterSource.class), eq(Long.class));
        assertThat(sqlCaptor.getValue()).contains("COUNT(*)").doesNotContain("WHERE");
    }

    @Test
    void countByFilter_withBrandFilter_shouldContainBrandCondition() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.of("Zebra"), Optional.empty());
        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                .thenReturn(3L);

        long count = repository.countByFilter(filter);

        assertThat(count).isEqualTo(3L);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForObject(sqlCaptor.capture(), any(MapSqlParameterSource.class), eq(Long.class));
        assertThat(sqlCaptor.getValue()).contains("WHERE").contains("brand = :brand");
    }

    @Test
    void countByFilter_withStateFilter_shouldContainStateCondition() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.empty(), Optional.of(DeviceState.INACTIVE));
        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                .thenReturn(0L);

        long count = repository.countByFilter(filter);

        assertThat(count).isZero();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForObject(sqlCaptor.capture(), any(MapSqlParameterSource.class), eq(Long.class));
        assertThat(sqlCaptor.getValue()).contains("WHERE").contains("state = :state");
    }

    @Test
    void countByFilter_whenQueryReturnsNull_shouldReturnZero() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.empty(), Optional.empty());
        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                .thenReturn(null);

        long count = repository.countByFilter(filter);

        assertThat(count).isZero();
    }

    @Test
    void countByFilter_withBrandAndStateFilters_shouldContainBothConditions() {
        DeviceSearchFilter filter = new DeviceSearchFilter(Optional.of("Honeywell"), Optional.of(DeviceState.IN_USE));
        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                .thenReturn(2L);

        long count = repository.countByFilter(filter);

        assertThat(count).isEqualTo(2L);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).queryForObject(sqlCaptor.capture(), paramsCaptor.capture(), eq(Long.class));

        assertThat(sqlCaptor.getValue())
                .contains("WHERE")
                .contains("brand = :brand")
                .contains("state = :state");
        assertThat(paramsCaptor.getValue().getValue("brand")).isEqualTo("Honeywell");
        assertThat(paramsCaptor.getValue().getValue("state")).isEqualTo(DeviceState.IN_USE.getCode());
    }
}
