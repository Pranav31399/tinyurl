# tinyurl (Spring Boot)

A scalable URL shortener implementation migrated to Spring Boot.

## Implemented design points

- **Write path**: `POST /api/v1/urls` with `{"longUrl":"..."}` creates a new short URL.
- **Read path**: `GET /{shortCode}` redirects to original URL (default `302`, or `?statusCode=301`).
- **Short code generation**: Snowflake-like distributed ID + Base62 encoding (collision-free by construction).
- **Cache + storage**: in-memory cache and repository abstractions.
- **Scalable architecture alignment**: stateless web app + worker-id-based ID generation suitable for horizontal scale.

## Project structure

- `service/SnowflakeIdGenerator`: distributed-unique numeric IDs.
- `util/Base62Codec`: decimal-to-Base62 conversion.
- `service/UrlShortenerService`: core write/read path logic.
- `api/TinyUrlController`: Spring MVC REST + redirect endpoints.
- `config/AppConfig`: dependency wiring for repository + ID generator.

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```

## Configuration

- `server.port` (default: `8080`)
- `tinyurl.base-url` (default: `http://localhost:${server.port}`)
- `tinyurl.worker-id` (default: `1`)

## API usage

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H 'Content-Type: application/json' \
  -d '{"longUrl":"https://example.com/some/very/long/path"}'

curl -i http://localhost:8080/<shortCode>
curl -i 'http://localhost:8080/<shortCode>?statusCode=301'
```
