CREATE TABLE IF NOT EXISTS devices (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    device_id     VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    brand         VARCHAR(255) NOT NULL,
    state         VARCHAR(50)  NOT NULL,
    creation_time TIMESTAMP  NOT NULL
);

-- Supports filtering by state with ORDER BY id (used by paginated list queries)
CREATE INDEX IF NOT EXISTS idx_devices_state_id ON devices (state, id);

-- Supports filtering by brand with ORDER BY id
CREATE INDEX IF NOT EXISTS idx_devices_brand_id ON devices (brand, id);
