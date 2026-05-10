package org.liuyi.notification.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionManager {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(String userId, WebSocketSession session) {
        // 先remove，再put
        removeSession(userId);
        sessions.put(userId, session);
    }

    public void removeSession(String userId) {
        WebSocketSession session = sessions.remove(userId);
        if (session != null && session.isOpen()) {
            try {
                session.close();  // 主动关闭连接
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public WebSocketSession getSession(String userId) {
        return sessions.get(userId);
    }

    public void sendToUser(String userId, byte[] data) throws IOException {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new BinaryMessage(data));
        }
    }
}