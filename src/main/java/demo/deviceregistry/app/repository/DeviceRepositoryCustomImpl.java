package demo.deviceregistry.app.repository;

import demo.deviceregistry.app.entity.Device;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DeviceRepositoryCustomImpl implements DeviceRepositoryCustom {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DeviceRepositoryCustomImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Retrieves a paginated list of devices matching the given filter criteria.
     *
     * @param filter the search filter containing optional brand and state constraints
     * @param offset zero-based index of the first record to return
     * @param limit  maximum number of records to return
     * @return list of matching {@link Device} entities, ordered by id
     */
    @Override
    public List<Device> findByFilter(DeviceSearchFilter filter, int offset, int limit) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sql = "SELECT * FROM devices " +
                buildCondition(filter, params) +
                " ORDER BY id LIMIT :limit OFFSET :offset";
        params.addValue("limit", limit);
        params.addValue("offset", offset);
        return jdbcTemplate.query(sql, params, DeviceRowMapper.INSTANCE);
    }

    /**
     * Returns the total count of devices matching the given filter criteria.
     *
     * @param filter the search filter containing optional brand and state constraints
     * @return number of devices that satisfy the filter; {@code 0} if none found
     */
    @Override
    public long countByFilter(DeviceSearchFilter filter) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sql = "SELECT COUNT(*) FROM devices " + buildCondition(filter, params);
        Long count = jdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null ? count : 0L;
    }

    /**
     * Build condition(s) for filter and add required parameters
     * @param filter filter to build where-condion
     * @param params query parameters
     * @return where-condition
     */
    private String buildCondition(DeviceSearchFilter filter, MapSqlParameterSource params) {
        List<String> conditions = new ArrayList<>();
        filter.brand().ifPresent(b -> {
            conditions.add("brand = :brand");
            params.addValue("brand", b);
        });

        filter.state().ifPresent(s -> {
            conditions.add("state = :state");
            params.addValue("state", s.getCode());
        });
        return conditions.isEmpty() ? "" :
                " WHERE " + String.join(" AND ", conditions);
    }

}
