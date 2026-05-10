package org.liuyi.notification.adapter.repository.mapper;

import com.liuyi.notification.notifications.DocumentType;
import com.liuyi.notification.notifications.MessageReceivedNotification;
import org.liuyi.notification.adapter.repository.persistence.MessageReceivedNotificationDO;

public class MessageReceivedNotificationMapper {

    public static MessageReceivedNotificationDO toDO(MessageReceivedNotification notification, String toUserId) {
        // 实际上MessageReceivedNotification的所有字段都不可能为null
        MessageReceivedNotificationDO entity = new MessageReceivedNotificationDO();
        entity.setToUserId(toUserId);
        entity.setMessageType(notification.getMessageTypeValue());
        entity.setSendTime(notification.getSendTime());
        entity.setSessionId(notification.getSessionId());
        entity.setMessageId(notification.getMessageId());
        entity.setSeqInSession(notification.getSeqInSession());
        entity.setSenderUserId(notification.getSenderUserId());
        entity.setTextContent(notification.getTextContent());
        entity.setFileId(notification.getFileId());
        entity.setImageWidth(notification.getImageWidth());
        entity.setImageHeight(notification.getImageHeight());
        entity.setAudioDurationSeconds(notification.getAudioDurationSeconds());
        entity.setDocumentName(notification.getDocumentName());
        entity.setDocumentSizeBytes(notification.getDocumentSizeBytes());
        entity.setDocumentType(String.valueOf(notification.getDocumentType()));

        return entity;
    }

    public static MessageReceivedNotification toNotification(MessageReceivedNotificationDO entity) {
        // 实际上MessageReceivedNotificationDO的所有字段都不可能为null，因为这些字段来自MessageReceivedNotification
        return MessageReceivedNotification.newBuilder()
                .setMessageTypeValue(entity.getMessageType())
                .setSendTime(entity.getSendTime())
                .setSessionId(entity.getSessionId())
                .setMessageId(entity.getMessageId())
                .setSeqInSession(entity.getSeqInSession())
                .setSenderUserId(entity.getSenderUserId())
                .setTextContent(entity.getTextContent())
                .setFileId(entity.getFileId())
                .setImageWidth(entity.getImageWidth())
                .setImageHeight(entity.getImageHeight())
                .setAudioDurationSeconds(entity.getAudioDurationSeconds())
                .setDocumentName(entity.getDocumentName())
                .setDocumentSizeBytes(entity.getDocumentSizeBytes())
                .setDocumentType(DocumentType.valueOf(entity.getDocumentType()))
                .build();
    }
}