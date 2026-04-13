package org.liuyi.notification.remote;

import lombok.RequiredArgsConstructor;
import org.liuyi.chat_api.event.FriendApplicationHandledEvent;
import org.liuyi.chat_api.event.FriendApplicationSentEvent;
import org.liuyi.chat_api.event.MessageSentEvent;
import org.liuyi.notification.application.service.Application;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSubscriber {
    private final Application application;

    @KafkaListener(topics = "friend-application-sent")
    public void subscribe(FriendApplicationSentEvent event) {
        application.handle(event);
    }

    @KafkaListener(topics = "friend_application_handled_event")
    public void subscribe(FriendApplicationHandledEvent event) {
        application.handle(event);
    }

    @KafkaListener(topics = "message-sent")
    public void subscribe(MessageSentEvent event) {
        application.handle(event);
    }
}
