package com.example.tinyurl.model;

import java.time.Instant;

public record UrlMapping(long id, String shortCode, String longUrl, Instant createdAt) {
}
