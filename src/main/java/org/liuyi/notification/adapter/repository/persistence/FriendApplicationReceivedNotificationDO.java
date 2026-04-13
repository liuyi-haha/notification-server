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
@Table(name = "friend_application_received_notification", indexes = {
        @Index(name = "idx_to_user", columnList = "to_user_id, send_time")
})
public class FriendApplicationReceivedNotificationDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "to_user_id", nullable = false)
    private String toUserId;

    @Column(name = "applicant_user_id", nullable = false)
    private String applicantUserId;

    @Column(name = "application_id", nullable = false, unique = true)
    private String applicationId;

    @Column(name = "verification_message", length = 512)
    private String verificationMessage;

    @Column(name = "send_time", nullable = false)
    private String sendTime;
}