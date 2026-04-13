package org.liuyi.notification.application.service;

import com.liuyi.notification.notifications.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.liuyi.chat_api.event.FriendApplicationHandledEvent;
import org.liuyi.chat_api.event.FriendApplicationSentEvent;
import org.liuyi.chat_api.event.MessageSentEvent;
import org.liuyi.common.domain.exception.DomainException;
import org.liuyi.notification.adapter.ConnectionManager;
import org.liuyi.notification.adapter.repository.FriendApplicationHandledNotificationRepository;
import org.liuyi.notification.adapter.repository.FriendApplicationReceivedNotificationRepository;
import org.liuyi.notification.adapter.repository.MessageReceivedNotificationRepository;
import org.liuyi.notification.port.client.ChatClient;
import org.liuyi.notification.remote.event.UserOnlineEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class Application {
    private final ConnectionManager connectionManager;
    private final ChatClient chatClient;
    // 三个仓库
    private final FriendApplicationReceivedNotificationRepository friendApplicationReceivedNotificationRepository;
    private final FriendApplicationHandledNotificationRepository friendApplicationHandledNotificationRepository;
    private final MessageReceivedNotificationRepository messageReceivedNotificationRepository;

    @Transactional
    public void handle(FriendApplicationSentEvent event) {
        // 看看要发送给谁
        String toUserId = event.getToUserId();
        var notification = FriendApplicationReceivedNotification.newBuilder()
                .setApplicantUserId(event.getFromUserId())
                .setApplicationId(event.getApplicationId())
                .setVerificationMessage(event.getVerificationMessage())
                .setSendTime(event.getSendTime().toString())
                .build();
        // 判断用户是否在线
        var session = connectionManager.getSession(toUserId);

        if (session != null && session.isOpen()) {
            try {
                // 在线则发送
                var websocketNotification = WebSocketNotification.newBuilder()
                        .setType(NotificationType.TYPE_FRIEND_APPLICATION_RECEIVED)
                        .setPayload(notification.toByteString())
                        .build();
                log.info("用户{}在线，发送好友申请通知: \n{} payload detail:\n{}", toUserId, websocketNotification, notification);
                connectionManager.sendToUser(toUserId, websocketNotification.toByteArray());
                return;
            } catch (Exception ex) {
                log.warn("发送失败, 兜底保存", ex);
            }
        }
        // 不在线则保存到数据库
        friendApplicationReceivedNotificationRepository.save(notification, toUserId);
    }

    @Transactional
    public void handle(FriendApplicationHandledEvent event) {
        // 看看要发送给谁
        String toUserId = event.getFromUserId(); // 要通知申请方，告知它申请结果
        HandleFriendApplicationResultType resultType = event.getResultType() == org.liuyi.chat_api.event.HandleFriendApplicationResultType.ACCEPTED ? HandleFriendApplicationResultType.HANDLE_RESULT_APPROVED : HandleFriendApplicationResultType.HANDLE_RESULT_REJECTED;
        var notification = FriendApplicationHandledNotification.newBuilder()
                .setOperateTime(event.getOperateTime().toString())
                .setApplicationId(event.getApplicationId())
                .setResultType(resultType)
                .setIsNewFriendship(event.isNewFriendShip())
                .setFriendshipId(Objects.toString(event.getFriendshipId(), ""))
                .setPrivateChatSessionId(Objects.toString(event.getFriendshipId(), ""))
                .setApplicantParticipantId(Objects.toString(event.getApplicantParticipantId(), ""))
                .setTargetUserParticipantId(Objects.toString(event.getTargetUserParticipantId(), ""))
                .build();
        // 判断用户是否在线
        var session = connectionManager.getSession(toUserId);
        if (session != null && session.isOpen()) {
            try {

                // 在线则发送
                var websocketNotification = WebSocketNotification.newBuilder()
                        .setType(NotificationType.TYPE_FRIEND_APPLICATION_HANDLED)
                        .setPayload(notification.toByteString())
                        .build();
                log.info("用户{}在线，发送好友申请出列结果通知: \n{}payload detail:\n{}", toUserId, websocketNotification, notification);
                connectionManager.sendToUser(toUserId, websocketNotification.toByteArray());
                return;
            } catch (Exception ex) {
                log.warn("发送失败, 兜底保存", ex);
            }
        }

        // 不在线则保存
        friendApplicationHandledNotificationRepository.save(notification, toUserId);
    }

    @Transactional
    public void handle(MessageSentEvent event) {
        // 看看要发送给谁
        Set<String> userIds = chatClient.getSessionUserIds(event.getSessionId());
        var messageType = switch (event.getMessageType()) {
            case TEXT -> MessageType.MESSAGE_TYPE_TEXT;
            default -> throw new DomainException("暂不支持的消息类型: " + event.getMessageType());
        };
        // 在线则发送
        var notification = MessageReceivedNotification.newBuilder()
                .setMessageType(messageType)
                .setSendTime(event.getSendTime().toString())
                .setSessionId(event.getSessionId())
                .setMessageId(event.getMessageId())
                .setSeqInSession(event.getSeqInSession())
                .setSenderUserId(event.getSenderUserId())
                .setTextContent(event.getTextContent())
                .build();
        // 把发送者排除在外
        for (String toUserId : userIds) {
            if (toUserId.equals(event.getSenderUserId())) {
                continue;
            }
            // 判断用户是否在线
            var session = connectionManager.getSession(toUserId);
            if (session != null && session.isOpen()) {
                try {
                    // 在线则发送
                    var websocketNotification = WebSocketNotification.newBuilder()
                            .setType(NotificationType.TYPE_MESSAGE_RECEIVED)
                            .setPayload(notification.toByteString())
                            .build();
                    log.info("用户{}在线，发送消息通知: \n{}payload detail:\n{}", toUserId, websocketNotification, notification);
                    connectionManager.sendToUser(toUserId, websocketNotification.toByteArray());
                    return;
                } catch (Exception ex) {
                    log.warn("发送失败, 兜底保存", ex);
                }
            }
            // 不在线则保存
            messageReceivedNotificationRepository.save(notification, toUserId);
            log.info("用户{}不在线，保存消息通知到数据库: \n{}payload detail:\n{}", toUserId, notification, notification);
        }
    }

    @EventListener
    @Transactional
    public void handle(UserOnlineEvent event) {
        // 用户上线后，主动推送通知
        String toUserId = event.getUserId();
        // 顺序不能乱
        // 先推送好友申请接收通知
        var friendApplicationReceivedNotifications = friendApplicationReceivedNotificationRepository.findByToUserId(toUserId);
        try {
            for (FriendApplicationReceivedNotification notification : friendApplicationReceivedNotifications) {
                var websocketNotification = WebSocketNotification.newBuilder()
                        .setType(NotificationType.TYPE_FRIEND_APPLICATION_RECEIVED)
                        .setPayload(notification.toByteString())
                        .build();
                log.info("用户{}上线，推送好友申请通知: \n{} payload detail:\n{}", toUserId, websocketNotification, notification);
                connectionManager.sendToUser(toUserId, websocketNotification.toByteArray());
            }
            friendApplicationReceivedNotificationRepository.deleteByToUserId(toUserId);
        } catch (Exception ex) {
            log.warn("推送好友申请失败", ex);
        }

        // 再推送好友申请出列结果通知
        var friendApplicationHandledNotifications = friendApplicationHandledNotificationRepository.findByToUserId(toUserId);
        try {
            for (FriendApplicationHandledNotification notification : friendApplicationHandledNotifications) {
                var websocketNotification = WebSocketNotification.newBuilder()
                        .setType(NotificationType.TYPE_FRIEND_APPLICATION_HANDLED)
                        .setPayload(notification.toByteString())
                        .build();
                log.info("用户{}上线，推送好友申请处理结果通知: \n{}payload detail:\n{}", toUserId, websocketNotification, notification);
                connectionManager.sendToUser(toUserId, websocketNotification.toByteArray());
            }
            friendApplicationHandledNotificationRepository.deleteByToUserId(toUserId);
        } catch (Exception ex) {
            log.warn("推送好友申请处理结果失败", ex);
        }

        // 最后推送消息通知
        var messageReceivedNotifications = messageReceivedNotificationRepository.findByToUserId(toUserId);
        try {
            for (MessageReceivedNotification notification : messageReceivedNotifications) {
                var websocketNotification = WebSocketNotification.newBuilder()
                        .setType(NotificationType.TYPE_MESSAGE_RECEIVED)
                        .setPayload(notification.toByteString())
                        .build();
                log.info("用户{}上线，推送消息通知: \n{}payload detail:\n{}", toUserId, websocketNotification, notification);
                connectionManager.sendToUser(toUserId, websocketNotification.toByteArray());
            }
            messageReceivedNotificationRepository.deleteByToUserId(toUserId);
        } catch (Exception ex) {
            log.warn("推送消息失败", ex);
        }
    }
}
