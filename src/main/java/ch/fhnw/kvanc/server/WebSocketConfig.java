package ch.fhnw.kvanc.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import ch.fhnw.kvanc.server.web.WebSocketEndpoint;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    /**
     * Will be called be @EnableWebSocket to register a handler as websocket
     * endpoint.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(websocketEndpoint(), "/wsVoting").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketEndpoint websocketEndpoint() {
        return new WebSocketEndpoint();
    }
}