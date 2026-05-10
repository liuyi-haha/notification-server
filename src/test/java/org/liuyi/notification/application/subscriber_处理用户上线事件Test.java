package org.liuyi.notification.application;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liuyi.notification.notifications.*;
import com.liuyi.notification.notifications.DocumentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.liuyi.chat_api.event.*;
import org.liuyi.chat_api.event.HandleFriendApplicationResultType;
import org.liuyi.chat_api.event.MessageType;
import org.liuyi.notification.adapter.repository.FriendApplicationHandledNotificationRepository;
import org.liuyi.notification.adapter.repository.FriendApplicationReceivedNotificationRepository;
import org.liuyi.notification.adapter.repository.MessageReceivedNotificationRepository;
import org.liuyi.notification.application.fake.FakeWebsocketSession;
import org.liuyi.notification.application.service.Application;
import org.liuyi.notification.port.client.ChatClient;
import org.liuyi.notification.remote.WebSocketSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.BinaryMessage;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Slf4j
public class subscriber_处理用户上线事件Test {
    @Autowired
    WebSocketSubscriber subscriber;
    @Autowired
    private Application application;
    @Autowired
    private FriendApplicationReceivedNotificationRepository friendApplicationReceivedNotificationRepository;
    @Autowired
    private FriendApplicationHandledNotificationRepository friendApplicationHandledNotificationRepository;
    @Autowired
    private MessageReceivedNotificationRepository messageReceivedNotificationRepository;

    @MockitoBean
    private ChatClient chatClient;


    @Test
    void 用户上线后_离线期间的好友申请通知被收到_好友申请被处理_消息被收到通知应该按照一定顺序推送给用户() {
        // 设置 mock 行为
        // 为每个消息事件设置对应的 session 用户
        when(chatClient.getSessionUserIds("chat_session_001"))
                .thenReturn(Set.of("user_001", "123456789"));
        when(chatClient.getSessionUserIds("chat_session_002"))
                .thenReturn(Set.of("user_002", "123456789"));
        when(chatClient.getSessionUserIds("chat_session_003"))
                .thenReturn(Set.of("user_003", "123456789"));
        when(chatClient.getSessionUserIds("chat_session_004"))
                .thenReturn(Set.of("user_004", "123456789"));
        when(chatClient.getSessionUserIds("chat_session_005"))
                .thenReturn(Set.of("user_005", "123456789"));
        when(chatClient.getSessionUserIds("chat_session_006"))
                .thenReturn(Set.of("user_006", "123456789"));
        when(chatClient.getSessionUserIds("chat_session_007"))
                .thenReturn(Set.of("user_007", "123456789"));


        // 在用户离线期间收到一些事件
        var applicationSentEvents = receiveFriendApplicationSentEventsWhileOffline();
        var applicationHandledEvents = receiveFriendApplicationHandledEventsWhileOffline();
        var messageSentEvents = receiveMessageSentEventsWhileOffline();

        // 构建期望的通知列表
        List<WebSocketNotification> expectedNotifications = new ArrayList<>();
        // 添加好友申请通知
        applicationSentEvents.stream()
                .map(this::friendApplicationSentEventToWebsocketNotification)
                .forEach(expectedNotifications::add);

        // 添加好友申请处理通知
        applicationHandledEvents.stream()
                .map(this::friendApplicationHandledEventToWebsocketNotification)
                .forEach(expectedNotifications::add);

        // 添加消息通知
        messageSentEvents.stream()
                .map(this::messageSentEventToWebsocketNotification)
                .forEach(expectedNotifications::add);


        FakeWebsocketSession session;
        try {
            URI uri = new URI("ws://localhost?userId=123456789");
            session = new FakeWebsocketSession();
            session.setUri(uri);

            subscriber.afterConnectionEstablished(session);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // 验证实际发送的通知与期望的一致
        var binaryMessages = session.getBinaryMessages();
        assertEquals(13, binaryMessages.size());

        var actualNotifications = session.getBinaryMessages().stream()
                .map(BinaryMessage::getPayload)
                .map(this::parseWebSocketNotification)
                .collect(Collectors.toList());

        assertThat(actualNotifications)
                .usingRecursiveComparison()
                .isEqualTo(expectedNotifications);

        // 验证通知被删除
        assertThat(friendApplicationReceivedNotificationRepository.findByToUserId("123456789")).isEmpty();
        assertThat(friendApplicationHandledNotificationRepository.findByToUserId("123456789")).isEmpty();
        assertThat(messageReceivedNotificationRepository.findByToUserId("123456789")).isEmpty();


    }

    private WebSocketNotification parseWebSocketNotification(ByteBuffer data) {
        try {
            return WebSocketNotification.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    private List<FriendApplicationSentEvent> receiveFriendApplicationSentEventsWhileOffline() {
        // 模拟在用户离线期间发送一些通知
        FriendApplicationSentEvent event1 = FriendApplicationSentEvent.builder()
                .fromUserId("user_001")
                .toUserId("123456789")
                .applicationId("app_10001")
                .verificationMessage("你好，我是李明，想加你为好友")
                .sendTime(Instant.now())
                .build();

        FriendApplicationSentEvent event2 = FriendApplicationSentEvent.builder()
                .fromUserId("user_002")
                .toUserId("123456789")
                .applicationId("app_10002")
                .verificationMessage("我是王芳，你的同事")
                .sendTime(Instant.now())
                .build();

        FriendApplicationSentEvent event3 = FriendApplicationSentEvent.builder()
                .fromUserId("user_003")
                .toUserId("123456789")
                .applicationId("app_10003")
                .verificationMessage("喜欢你的分享，求通过")
                .sendTime(Instant.now())
                .build();

        application.handle(event1);
        application.handle(event2);
        application.handle(event3);
        return List.of(new FriendApplicationSentEvent[]{event1, event2, event3});
    }

    private List<FriendApplicationHandledEvent> receiveFriendApplicationHandledEventsWhileOffline() {
        // 模拟在用户离线期间发送一些好友申请处理通知

        // 事件1：同意好友申请，建立新好友关系
        FriendApplicationHandledEvent event1 = FriendApplicationHandledEvent.builder()
                .operateTime(Instant.now())
                .applicationId("app_10001")
                .fromUserId("123456789")
                .toUserId("user_001")
                .resultType(HandleFriendApplicationResultType.ACCEPTED)
                .isNewFriendShip(true)
                .friendshipId("friendship_001")
                .privateChatSessionId("chat_session_001")
                .applicantParticipantId("participant_001")
                .targetUserParticipantId("participant_789")
                .build();

        // 事件2：拒绝好友申请
        FriendApplicationHandledEvent event2 = FriendApplicationHandledEvent.builder()
                .operateTime(Instant.now())
                .applicationId("app_10002")
                .fromUserId("123456789")
                .toUserId("user_002")
                .resultType(HandleFriendApplicationResultType.REJECTED)
                .build();

        // 事件3：同意好友申请，但已是好友关系（重复添加场景）
        FriendApplicationHandledEvent event3 = FriendApplicationHandledEvent.builder()
                .operateTime(Instant.now())
                .applicationId("app_10003")
                .fromUserId("123456789")
                .toUserId("user_003")
                .resultType(HandleFriendApplicationResultType.ACCEPTED)
                .isNewFriendShip(false)  // 已经是好友，不建立新关系
                .build();

        application.handle(event1);
        application.handle(event2);
        application.handle(event3);

        return List.of(event1, event2, event3);
    }

    private List<MessageSentEvent> receiveMessageSentEventsWhileOffline() {
        // 模拟在用户离线期间发送一些消息通知

        // 事件1：用户001发送文本消息
        MessageSentEvent event1 = MessageSentEvent.builder()
                .messageType(MessageType.TEXT)
                .sendTime(Instant.now())
                .sessionId("chat_session_001")
                .messageId("msg_10001")
                .seqInSession(1)
                .textContent("你好，我是李明，看到好友申请通过了吗？")
                .senderUserId("user_001")
                .build();

        // 事件2：用户002发送文本消息
        MessageSentEvent event2 = MessageSentEvent.builder()
                .messageType(MessageType.TEXT)
                .sendTime(Instant.now())
                .sessionId("chat_session_002")
                .messageId("msg_10002")
                .seqInSession(2)
                .textContent("王芳：下午三点开会，记得参加")
                .senderUserId("user_002")
                .build();

        // 事件3：用户003发送文本消息
        MessageSentEvent event3 = MessageSentEvent.builder()
                .messageType(MessageType.TEXT)  // 假设有IMAGE类型
                .sendTime(Instant.now())
                .sessionId("chat_session_003")
                .messageId("msg_10003")
                .seqInSession(1)
                .textContent("好的")  // 图片消息可能包含URL或描述
                .senderUserId("user_003")
                .build();

        // 事件4: 用户004发送图片消息
        MessageSentEvent event4 = MessageSentEvent.builder()
                .messageType(MessageType.IMAGE)  // 假设有IMAGE类型
                .sendTime(Instant.now())
                .sessionId("chat_session_004")
                .messageId("msg_10004")
                .seqInSession(1)
                .senderUserId("user_004")
                .fileId("image-file-id-123")
                .imageWidth(600)
                .imageHeight(800)
                .build();

        // 事件5: 用户005发送语音消息
        MessageSentEvent event5 = MessageSentEvent.builder()
                .messageType(MessageType.SPEECH)  // 假设有IMAGE类型
                .sendTime(Instant.now())
                .sessionId("chat_session_005")
                .messageId("msg_10005")
                .seqInSession(1)
                .senderUserId("user_005")
                .fileId("speech-file-id-123")
                .speechDurationSeconds(20)
                .build();

        // 事件6: 用户006发送PDF类型的文档消息
        MessageSentEvent event6 = MessageSentEvent.builder()
                .messageType(MessageType.DOCUMENT)  // 假设有IMAGE类型
                .sendTime(Instant.now())
                .sessionId("chat_session_006")
                .messageId("msg_10006")
                .seqInSession(1)
                .senderUserId("user_006")
                .fileId("document-file-id-123")
                .documentName("项目计划.pdf")
                .documentBytes(1024L * 1024L) // 1MB
                .documentType(org.liuyi.chat_api.event.DocumentType.PDF)
                .build();

        // 事件6: 用户007发送其它类型的文档消息
        MessageSentEvent event7 = MessageSentEvent.builder()
                .messageType(MessageType.DOCUMENT)  // 假设有IMAGE类型
                .sendTime(Instant.now())
                .sessionId("chat_session_007")
                .messageId("msg_10007")
                .seqInSession(1)
                .senderUserId("user_007")
                .fileId("document-file-id-234")
                .documentName("其它类型的消息.wav")
                .documentBytes(1024L * 1024L) // 1MB
                .documentType(org.liuyi.chat_api.event.DocumentType.OTHER)
                .build();


        application.handle(event1);
        application.handle(event2);
        application.handle(event3);
        application.handle(event4);
        application.handle(event5);
        application.handle(event6);
        application.handle(event7);

        return List.of(event1, event2, event3, event4, event5, event6, event7);
    }

    private WebSocketNotification friendApplicationSentEventToWebsocketNotification(FriendApplicationSentEvent event) {
        var notification = FriendApplicationReceivedNotification.newBuilder()
                .setApplicantUserId(event.getFromUserId())
                .setApplicationId(event.getApplicationId())
                .setVerificationMessage(event.getVerificationMessage())
                .setSendTime(event.getSendTime().toString())
                .build();

        return WebSocketNotification.newBuilder()
                .setType(NotificationType.TYPE_FRIEND_APPLICATION_RECEIVED)
                .setPayload(notification.toByteString())
                .build();
    }

    private WebSocketNotification friendApplicationHandledEventToWebsocketNotification(FriendApplicationHandledEvent event) {
        com.liuyi.notification.notifications.HandleFriendApplicationResultType resultType = event.getResultType() == org.liuyi.chat_api.event.HandleFriendApplicationResultType.ACCEPTED ? com.liuyi.notification.notifications.HandleFriendApplicationResultType.HANDLE_RESULT_APPROVED : com.liuyi.notification.notifications.HandleFriendApplicationResultType.HANDLE_RESULT_REJECTED;
        var notification = FriendApplicationHandledNotification.newBuilder()
                .setOperateTime(event.getOperateTime().toString())
                .setApplicationId(event.getApplicationId())
                .setResultType(resultType)
                .setIsNewFriendship(event.isNewFriendShip())
                .setFriendshipId(Objects.toString(event.getFriendshipId(), ""))
                .setPrivateChatSessionId(Objects.toString(event.getPrivateChatSessionId(), ""))
                .setApplicantParticipantId(Objects.toString(event.getApplicantParticipantId(), ""))
                .setTargetUserParticipantId(Objects.toString(event.getTargetUserParticipantId(), ""))
                .build();

        return WebSocketNotification.newBuilder()
                .setType(NotificationType.TYPE_FRIEND_APPLICATION_HANDLED)
                .setPayload(notification.toByteString())
                .build();
    }

    private WebSocketNotification messageSentEventToWebsocketNotification(MessageSentEvent event) {
        // 这里假设我们有一个方法将MessageSentEvent转换为WebSocketNotification
        // 实际实现可能需要根据消息类型构建不同的通知内容
        var messageType = switch (event.getMessageType()) {
            case TEXT -> com.liuyi.notification.notifications.MessageType.MESSAGE_TYPE_TEXT;
            case IMAGE -> com.liuyi.notification.notifications.MessageType.MESSAGE_TYPE_IMAGE;
            case SPEECH -> com.liuyi.notification.notifications.MessageType.MESSAGE_TYPE_AUDIO;
            case DOCUMENT -> com.liuyi.notification.notifications.MessageType.MESSAGE_TYPE_DOCUMENT;
        };
        var notification = MessageReceivedNotification.newBuilder()
                .setMessageType(messageType)
                .setSendTime(event.getSendTime().toString())
                .setSessionId(event.getSessionId())
                .setMessageId(event.getMessageId())
                .setSeqInSession(event.getSeqInSession())
                .setSenderUserId(event.getSenderUserId())
                .setTextContent(Optional.ofNullable(event.getTextContent()).orElse(""))
                .setFileId(Optional.ofNullable(event.getFileId()).orElse(""))
                .setImageWidth(Optional.ofNullable(event.getImageWidth()).orElse(0))
                .setImageHeight(Optional.ofNullable(event.getImageHeight()).orElse(0))
                .setAudioDurationSeconds(Optional.ofNullable(event.getSpeechDurationSeconds()).orElse(0))
                .setDocumentName(Optional.ofNullable(event.getDocumentName()).orElse(""))
                .setDocumentSizeBytes(Optional.ofNullable(event.getDocumentBytes()).orElse(0L))
                .setDocumentType(eventDocumentTypeToNotificationDocumentType(event.getDocumentType()))
                .build();


        log.info("构建消息通知: {}", notification);

        return WebSocketNotification.newBuilder()
                .setType(NotificationType.TYPE_MESSAGE_RECEIVED)
                .setPayload(notification.toByteString())
                .build();
    }

    private DocumentType eventDocumentTypeToNotificationDocumentType(org.liuyi.chat_api.event.DocumentType documentType) {
        if (documentType == null) {
            return DocumentType.DOCUMENT_TYPE_UNKNOWN;
        }
        return switch (documentType) {
            case WORD -> DocumentType.DOCUMENT_TYPE_WORD;
            case PDF -> DocumentType.DOCUMENT_TYPE_PDF;
            case TXT -> DocumentType.DOCUMENT_TYPE_TXT;
            case OTHER -> DocumentType.DOCUMENT_TYPE_OTHER;
        };
    }
}