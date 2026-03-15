# tinyurl (Java)

A scalable URL shortener implementation in pure Java.

## Implemented design points

- **Write path**: `POST /api/v1/urls` with `{"longUrl":"..."}` creates a new short URL.
- **Read path**: `GET /{shortCode}` redirects to original URL (default `302`, or `?statusCode=301`).
- **Short code generation**: Snowflake-like distributed ID + Base62 encoding (collision-free by construction).
- **Cache + storage**: in-memory cache and repository abstractions (can be swapped for Redis + Cassandra/MongoDB).
- **Scalable architecture alignment**: stateless HTTP app + worker-id-based ID generation suitable for horizontal scale.

## Project structure

- `service/SnowflakeIdGenerator`: distributed-unique numeric IDs.
- `util/Base62Codec`: decimal-to-Base62 conversion.
- `service/UrlShortenerService`: core write/read path logic.
- `http/TinyUrlHttpServer`: lightweight HTTP API.

## Run

```bash
mkdir -p out
javac -d out $(find src/main/java -name '*.java')
java -cp out com.example.tinyurl.TinyUrlApplication
```

## Test

```bash
mkdir -p out
javac -d out $(find src/main/java src/test/java -name '*.java')
java -ea -cp out com.example.tinyurl.TestRunner
```

## API usage

```bash
curl -X POST http://localhost:8080/api/v1/urls \
  -H 'Content-Type: application/json' \
  -d '{"longUrl":"https://example.com/some/very/long/path"}'

curl -i http://localhost:8080/<shortCode>
curl -i 'http://localhost:8080/<shortCode>?statusCode=301'
```
