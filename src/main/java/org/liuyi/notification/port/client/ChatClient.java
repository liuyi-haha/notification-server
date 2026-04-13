package org.liuyi.notification.port.client;

import java.util.Set;

public interface ChatClient {
    Set<String> getSessionUserIds(String sessionId);
}
