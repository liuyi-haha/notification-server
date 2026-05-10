package org.liuyi.notification.remote.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private final String userId;

    public UserOnlineEvent(Object source, String userId) {
        super(source);
        this.userId = userId;
    }

}
