package com.example.tinyurl.http;

public final class QueryUtil {
    private QueryUtil() {
    }

    public static int getStatusCode(String query) {
        if (query == null || query.isBlank()) {
            return 302;
        }
        String[] parts = query.split("&");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && "statusCode".equals(kv[0]) && "301".equals(kv[1])) {
                return 301;
            }
        }
        return 302;
    }
}
