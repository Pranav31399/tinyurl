package com.example.tinyurl;

import com.example.tinyurl.http.TinyUrlHttpServer;

public class TinyUrlApplication {
    public static void main(String[] args) {
        int port = Integer.parseInt(System.getProperty("tinyurl.port", "8080"));
        String baseUrl = System.getProperty("tinyurl.base-url", "http://localhost:" + port);
        long workerId = Long.parseLong(System.getProperty("tinyurl.worker-id", "1"));

        TinyUrlHttpServer server = TinyUrlHttpServer.create(port, baseUrl, workerId);
        server.start();
        System.out.println("TinyURL server started on port " + port);
    }
}
