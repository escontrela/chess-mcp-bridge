package com.davidp.chessjourney.chess_mcp_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

@Component
public class ActiveUserTool {

    private static final Logger log = LoggerFactory.getLogger(ActiveUserTool.class);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(3);

    private final RestClient http;
    private final String activeUserPath;
    private final String quotePath;

    public ActiveUserTool(
            @Value("${chess.coreBaseUrl}") String baseUrl,
            @Value("${chess.activeUserPath}") String activeUserPath,
            @Value("${chess.quotePath}") String quotePath
    ) {
        var rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(CONNECT_TIMEOUT);
        rf.setReadTimeout(READ_TIMEOUT);

        this.http = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(rf)
                .build();

        this.activeUserPath = activeUserPath;
        this.quotePath = quotePath;
    }

    @Tool(name = "getActiveUser",
            description = "Obtiene el usuario activo (JSON) desde la app de ajedrez.")
    public String getActiveUser() {
        return executeGet(activeUserPath, "No active user found");
    }

    @Tool(name = "postQuote",
            description = "Publica una cita de un autor de ajedrez en el sistema para luego mostrar en la pantalla incial de chess-journey a modo de motivación.")
    public String postQuote(Quote quote) {
        try {
            return http.post()
                    .uri(quotePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(quote)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new RuntimeException("Error posting quote: " + res.getStatusCode());
                    })
                    .body(String.class);
        } catch (Exception e) {
            log.error("Bridge error posting quote: {}", e.toString());
            return "{\"message\":\"Bridge error posting quote: " + e.getMessage() + "\"}";
        }
    }

    private String executeGet(String path, String notFoundMessage) {
        try {
            return http.get()
                    .uri(path)
                    .retrieve()
                    .onStatus(s -> s.value() == 404, (req, res) -> {
                        throw new NoActiveUser(notFoundMessage);
                    })
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new RuntimeException("Upstream error: " + res.getStatusCode());
                    })
                    .body(String.class);
        } catch (NoActiveUser e) {
            log.info("Not found in core: {}", e.getMessage());
            return "{\"message\":\"" + notFoundMessage + "\"}";
        } catch (Exception e) {
            log.error("Bridge error calling {}: {}", path, e.toString());
            return "{\"message\":\"Bridge error calling core: " + e.getMessage() + "\"}";
        }
    }

    private static class NoActiveUser extends RuntimeException {
        NoActiveUser(String m) {
            super(m);
        }
    }

    public static class Quote {
        private String text;
        private String author;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }
}