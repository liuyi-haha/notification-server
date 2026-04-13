package org.liuyi.notification.remote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.liuyi.notification.adapter.ConnectionManager;
import org.liuyi.notification.remote.event.UserOnlineEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketSubscriber extends AbstractWebSocketHandler {
    private final ConnectionManager connectionManager;
    private final ApplicationEventPublisher publisher;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = UriComponentsBuilder.fromUri(session.getUri())
                .build()
                .getQueryParams()
                .getFirst("userId");
        log.info("连接成功, userId: {}", userId);
        connectionManager.addSession(userId, session);

        // 事件通知，主动推送离线期间的通知
        publisher.publishEvent(new UserOnlineEvent(this, userId));

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = UriComponentsBuilder.fromUri(session.getUri())
                .build()
                .getQueryParams()
                .getFirst("userId");
        log.warn("连接关闭, userId: {}", userId);
        connectionManager.removeSession(userId);

    }
}
