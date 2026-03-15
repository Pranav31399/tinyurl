package com.example.tinyurl.api;

import com.example.tinyurl.model.UrlMapping;
import com.example.tinyurl.service.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
public class TinyUrlController {
    private final UrlShortenerService service;

    public TinyUrlController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping("/api/v1/urls")
    public ResponseEntity<?> create(@RequestBody(required = false) CreateUrlRequest request) {
        if (request == null || request.getLongUrl() == null || !isValidLongUrl(request.getLongUrl())) {
            return ResponseEntity.badRequest().body(Map.of("error", "longUrl must start with http:// or https://"));
        }

        UrlMapping mapping = service.createShortUrl(request.getLongUrl());
        CreateUrlResponse response = new CreateUrlResponse(
                service.buildShortUrl(mapping.shortCode()),
                mapping.shortCode(),
                mapping.longUrl()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(
            @PathVariable String shortCode,
            @RequestParam(required = false) Integer statusCode) {

        Optional<String> longUrl = service.resolveLongUrl(shortCode);
        if (longUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "not found"));
        }

        HttpStatus redirectStatus = (statusCode != null && statusCode == 301)
                ? HttpStatus.MOVED_PERMANENTLY
                : HttpStatus.FOUND;

        return ResponseEntity.status(redirectStatus)
                .location(URI.create(longUrl.get()))
                .build();
    }

    private boolean isValidLongUrl(String longUrl) {
        return longUrl.startsWith("http://") || longUrl.startsWith("https://");
    }
}
