package org.liuyi.notification.adapter.repository.mapper;

import com.liuyi.notification.notifications.MessageReceivedNotification;
import org.liuyi.notification.adapter.repository.persistence.MessageReceivedNotificationDO;

public class MessageReceivedNotificationMapper {

    public static MessageReceivedNotificationDO toDO(MessageReceivedNotification notification, String toUserId) {
        MessageReceivedNotificationDO entity = new MessageReceivedNotificationDO();
        entity.setToUserId(toUserId);
        entity.setMessageType(notification.getMessageTypeValue());
        entity.setSendTime(notification.getSendTime());
        entity.setSessionId(notification.getSessionId());
        entity.setMessageId(notification.getMessageId());
        entity.setSeqInSession(notification.getSeqInSession());
        entity.setSenderUserId(notification.getSenderUserId());
        entity.setTextContent(notification.getTextContent());
        return entity;
    }

    public static MessageReceivedNotification toNotification(MessageReceivedNotificationDO entity) {
        return MessageReceivedNotification.newBuilder()
                .setMessageTypeValue(entity.getMessageType())
                .setSendTime(entity.getSendTime())
                .setSessionId(entity.getSessionId())
                .setMessageId(entity.getMessageId())
                .setSeqInSession(entity.getSeqInSession())
                .setSenderUserId(entity.getSenderUserId())
                .setTextContent(entity.getTextContent())
                .build();
    }
}