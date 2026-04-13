package org.liuyi.notification.adapter.repository.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message_received_notification", indexes = {
        @Index(name = "idx_to_user", columnList = "to_user_id, send_time")
})
public class MessageReceivedNotificationDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "to_user_id", nullable = false)
    private String toUserId;

    @Column(name = "message_type", nullable = false)
    private Integer messageType;  // protobuf的enum是int

    @Column(name = "send_time", nullable = false)
    private String sendTime;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;

    @Column(name = "seq_in_session", nullable = false)
    private Integer seqInSession;

    @Column(name = "sender_user_id", nullable = false)
    private String senderUserId;

    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;
}