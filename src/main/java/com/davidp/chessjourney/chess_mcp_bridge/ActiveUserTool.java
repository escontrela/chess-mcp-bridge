package com.davidp.chessjourney.chess_mcp_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

@Component
public class ActiveUserTool {

    private static final Logger log = LoggerFactory.getLogger(ActiveUserTool.class);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration READ_TIMEOUT    = Duration.ofSeconds(3);

    private final RestClient http;
    private final String activeUserPath;

    public ActiveUserTool(
            @Value("${chess.coreBaseUrl}") String baseUrl,
            @Value("${chess.activeUserPath}") String activeUserPath
    ) {
        var rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(CONNECT_TIMEOUT);
        rf.setReadTimeout(READ_TIMEOUT);

        this.http = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(rf)
                .build();

        this.activeUserPath = activeUserPath;
    }

    @Tool(name = "getActiveUser",
            description = "Obtiene el usuario activo (JSON) desde la app de ajedrez.")
    public String getActiveUser() {
        try {
            // devuelve el JSON tal cual
            return http.get().uri(activeUserPath).retrieve()
                    .onStatus(s -> s.value() == 404, (req, res) -> {
                        throw new NoActiveUser("No active user found");
                    })
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new RuntimeException("Upstream error: " + res.getStatusCode());
                    })
                    .body(String.class);
        } catch (NoActiveUser e) {
            log.info("No active user in core: {}", e.getMessage());
            return "{\"message\":\"No active user found\"}";
        } catch (Exception e) {
            log.error("Bridge error calling {}: {}", activeUserPath, e.toString());
            return "{\"message\":\"Bridge error calling core: " + e.getMessage() + "\"}";
        }
    }

    private static class NoActiveUser extends RuntimeException {
        NoActiveUser(String m){ super(m); }
    }
}