package com.example.tinyurl.repository;

import com.example.tinyurl.model.UrlMapping;

import java.util.Optional;

public interface UrlRepository {
    UrlMapping save(UrlMapping mapping);

    Optional<UrlMapping> findByShortCode(String shortCode);
}
