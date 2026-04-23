CREATE TABLE IF NOT EXISTS devices (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    device_id     VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    brand         VARCHAR(255) NOT NULL,
    state         VARCHAR(50)  NOT NULL,
    creation_time TIMESTAMP  NOT NULL
);
