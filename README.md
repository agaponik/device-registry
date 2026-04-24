# Device Registry

A Spring Boot REST API for managing devices. Devices have a name, brand, state (`available`, `in-use`, `inactive`), and a creation timestamp. The API supports creating, reading, updating, deleting, and listing devices with filtering and pagination.

## Tech Stack

- Java 21
- Spring Boot 4.x (Web MVC, Data JDBC, Actuator)
- PostgreSQL
- Docker / Docker Compose

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose

## Build

```bash
mvn clean package
```

The JAR will be produced at `target/device-registry-<version>.jar`.

To skip tests during the build:

```bash
mvn clean package -DskipTests
```

---

## Ports

The following host ports must be free before starting the containers:

| Port   | Used by                  |
|--------|--------------------------|
| `8080` | Device Registry REST API |
| `5432` | PostgreSQL               |

> **Warning:** If either port is already in use on your machine, Docker will fail to start the corresponding container.  
> To use different host ports, edit the `ports` mapping in `docker-compose.yml`:
>
> ```yaml
> # app — change the left-hand (host) port
> ports:
>   - "9090:8080"   # API will be available on http://localhost:9090
>
> # postgres — change the left-hand (host) port
> ports:
>   - "5433:5432"   # PostgreSQL reachable at localhost:5433
> ```
>
> When running the app locally (Option 2) and you changed the PostgreSQL host port, update `application.yaml` accordingly:
> ```yaml
> spring:
>   datasource:
>     url: jdbc:postgresql://localhost:5433/device_registry
> ```

---

## Run

### Option 1 — Full Docker Compose (PostgreSQL + App in containers)

Build the JAR first, then start both services:

```bash
mvn clean package
docker compose up --build -d
```

The application will be available at `http://localhost:8080`.

To stop and remove containers:

```bash
docker compose down
```
### Option 2 — PostgreSQL in Docker, App running locally (e.g. from IDE)

Start only the PostgreSQL container:

```bash
docker compose up postgres -d
```

The database will be reachable at `localhost:5432`. The default credentials match what `application.yaml` already expects:

| Property | Value                                              |
|----------|----------------------------------------------------|
| URL      | `jdbc:postgresql://localhost:5432/device_registry` |
| Username | `postgres`                                         |
| Password | `postgres`                                         |

Then run the application from your IDE (`DeviceRegistryApplication`) or via Maven:

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`.

---

## Reset database

To **reset the database** (remove the container and delete all stored data):

```bash
docker compose down -v
```

The `-v` flag removes the named volume (`postgres_dr_data`), so the database will be recreated from scratch on the next startup.


## API

The full OpenAPI 3.0 specification is located at:

```
src/main/resources/openapi/device-registry.yaml
```

### Endpoints summary

| Method   | Path                                      | Description                                     |
|----------|-------------------------------------------|-------------------------------------------------|
| `POST`   | `/api/device`                             | Create a new device                             |
| `GET`    | `/api/device/list`                        | List devices (filter by brand/state, paginated) |
| `GET`    | `/api/device/{deviceId}`                  | Get device by ID                                |
| `PATCH`  | `/api/device/{deviceId}`                  | Update device name and/or brand                 |
| `DELETE` | `/api/device/{deviceId}`                  | Delete a device                                 |
| `POST`   | `/api/device/{deviceId}/state/{newState}` | Change device state                             |

Device states: `available`, `in-use`, `inactive`.

> Devices in state `in-use` cannot be updated or deleted (returns `409 Conflict`).

You can import `src/main/resources/openapi/device-registry.yaml` into tools like [Swagger Editor](https://editor.swagger.io) or [Postman](https://www.postman.com) to explore and test the API interactively.

