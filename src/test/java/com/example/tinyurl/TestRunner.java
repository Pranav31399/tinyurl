package com.example.tinyurl;

import com.example.tinyurl.repository.InMemoryUrlRepository;
import com.example.tinyurl.service.IdGenerator;
import com.example.tinyurl.service.UrlShortenerService;
import com.example.tinyurl.util.Base62Codec;

public class TestRunner {
    public static void main(String[] args) {
        testBase62();
        testServiceCreateResolve();
        System.out.println("All tests passed");
    }

    private static void testBase62() {
        assert "g8".equals(Base62Codec.encode(1000));
        assert "0".equals(Base62Codec.encode(0));
        boolean thrown = false;
        try {
            Base62Codec.encode(-1);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        assert thrown;
    }

    private static void testServiceCreateResolve() {
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
        assert "g8".equals(mapping.shortCode());
        assert "http://localhost:8080/g8".equals(service.buildShortUrl(mapping.shortCode()));
        assert "https://example.com".equals(service.resolveLongUrl("g8").orElseThrow());
    }
}
