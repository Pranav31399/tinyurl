package com.example.tinyurl.service;

import com.example.tinyurl.model.UrlMapping;
import com.example.tinyurl.repository.UrlRepository;
import com.example.tinyurl.util.Base62Codec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlShortenerService {
    private final UrlRepository repository;
    private final IdGenerator idGenerator;
    private final String baseUrl;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public UrlShortenerService(
            UrlRepository repository,
            IdGenerator idGenerator,
            @Value("${tinyurl.base-url:http://localhost:${server.port:8080}}") String baseUrl) {
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.baseUrl = baseUrl;
    }

    public UrlMapping createShortUrl(String longUrl) {
        long id = idGenerator.nextId();
        String shortCode = Base62Codec.encode(id);
        UrlMapping mapping = new UrlMapping(id, shortCode, longUrl, Instant.now());
        repository.save(mapping);
        cache.put(shortCode, longUrl);
        return mapping;
    }

    public Optional<String> resolveLongUrl(String shortCode) {
        String cached = cache.get(shortCode);
        if (cached != null) {
            return Optional.of(cached);
        }

        Optional<UrlMapping> fromDb = repository.findByShortCode(shortCode);
        fromDb.ifPresent(mapping -> cache.put(mapping.shortCode(), mapping.longUrl()));
        return fromDb.map(UrlMapping::longUrl);
    }

    public String buildShortUrl(String shortCode) {
        return baseUrl + "/" + shortCode;
    }
}
