package org.liuyi.notification.config;

import lombok.RequiredArgsConstructor;
import org.liuyi.notification.remote.WebSocketSubscriber;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketSubscriber webSocketSubscriber;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketSubscriber, "/ws").setAllowedOrigins("*").setAllowedOriginPatterns("*");
    }
}
