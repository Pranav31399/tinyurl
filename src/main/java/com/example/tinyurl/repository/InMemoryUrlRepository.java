package com.example.tinyurl.repository;

import com.example.tinyurl.model.UrlMapping;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUrlRepository implements UrlRepository {
    private final Map<String, UrlMapping> storage = new ConcurrentHashMap<>();

    @Override
    public UrlMapping save(UrlMapping mapping) {
        storage.put(mapping.shortCode(), mapping);
        return mapping;
    }

    @Override
    public Optional<UrlMapping> findByShortCode(String shortCode) {
        return Optional.ofNullable(storage.get(shortCode));
    }
}
