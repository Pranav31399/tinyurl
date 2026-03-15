package com.example.tinyurl.http;

import com.example.tinyurl.model.UrlMapping;
import com.example.tinyurl.repository.InMemoryUrlRepository;
import com.example.tinyurl.service.SnowflakeIdGenerator;
import com.example.tinyurl.service.UrlShortenerService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TinyUrlHttpServer {
    private final HttpServer server;

    private TinyUrlHttpServer(HttpServer server) {
        this.server = server;
    }

    public static TinyUrlHttpServer create(int port, String baseUrl, long workerId) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            UrlShortenerService service = new UrlShortenerService(
                    new InMemoryUrlRepository(),
                    new SnowflakeIdGenerator(workerId),
                    baseUrl
            );

            server.createContext("/api/v1/health", exchange -> {
                if (!"GET".equals(exchange.getRequestMethod())) {
                    send(exchange, 405, "{\"error\":\"method not allowed\"}");
                    return;
                }
                send(exchange, 200, "{\"status\":\"ok\"}");
            });

            server.createContext("/api/v1/urls", exchange -> {
                if (!"POST".equals(exchange.getRequestMethod())) {
                    send(exchange, 405, "{\"error\":\"method not allowed\"}");
                    return;
                }
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                String longUrl = JsonUtil.extractLongUrl(body);
                if (longUrl == null || (!longUrl.startsWith("http://") && !longUrl.startsWith("https://"))) {
                    send(exchange, 400, "{\"error\":\"longUrl must start with http:// or https://\"}");
                    return;
                }

                UrlMapping mapping = service.createShortUrl(longUrl);
                String json = "{\"shortUrl\":\"" + service.buildShortUrl(mapping.shortCode())
                        + "\",\"shortCode\":\"" + mapping.shortCode()
                        + "\",\"longUrl\":\"" + JsonUtil.escapeJson(mapping.longUrl()) + "\"}";
                send(exchange, 201, json);
            });

            server.createContext("/", exchange -> handleRedirect(service, exchange));
            return new TinyUrlHttpServer(server);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create server", e);
        }
    }

    public void start() {
        server.start();
    }

    private static void handleRedirect(UrlShortenerService service, HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            send(exchange, 405, "{\"error\":\"method not allowed\"}");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (path == null || path.equals("/") || path.startsWith("/api/")) {
            send(exchange, 404, "{\"error\":\"not found\"}");
            return;
        }

        String shortCode = path.substring(1);
        Optional<String> longUrl = service.resolveLongUrl(shortCode);
        if (longUrl.isEmpty()) {
            send(exchange, 404, "{\"error\":\"not found\"}");
            return;
        }

        int statusCode = QueryUtil.getStatusCode(exchange.getRequestURI().getQuery());
        exchange.getResponseHeaders().add("Location", longUrl.get());
        exchange.sendResponseHeaders(statusCode, -1);
        exchange.close();
    }

    private static void send(HttpExchange exchange, int status, String json) throws IOException {
        byte[] payload = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, payload.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(payload);
        }
    }
}
