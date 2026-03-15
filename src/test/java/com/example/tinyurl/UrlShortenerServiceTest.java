package com.example.tinyurl;

import com.example.tinyurl.repository.InMemoryUrlRepository;
import com.example.tinyurl.service.IdGenerator;
import com.example.tinyurl.service.UrlShortenerService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlShortenerServiceTest {

    @Test
    void createAndResolveUrl() {
        IdGenerator sequential = new IdGenerator() {
            long id = 999;

            @Override
            public long nextId() {
                return ++id;
            }
        };

        UrlShortenerService service = new UrlShortenerService(
                new InMemoryUrlRepository(),
                sequential,
                "http://localhost:8080"
        );

        var mapping = service.createShortUrl("https://example.com");

        assertEquals("g8", mapping.shortCode());
        assertEquals("http://localhost:8080/g8", service.buildShortUrl(mapping.shortCode()));
        assertEquals("https://example.com", service.resolveLongUrl("g8").orElseThrow());
    }
}
