package org.liuyi.notification.remote.event;

import org.springframework.context.ApplicationEvent;

public class UserOnlineEvent extends ApplicationEvent {
    private final String userId;

    public UserOnlineEvent(Object source, String userId) {
        super(source);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}
