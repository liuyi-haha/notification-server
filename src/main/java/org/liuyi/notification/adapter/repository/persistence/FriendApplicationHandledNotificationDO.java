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
@Table(name = "friend_application_handled_notification", indexes = {
        @Index(name = "idx_to_user", columnList = "to_user_id, operate_time")
})
public class FriendApplicationHandledNotificationDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "to_user_id", nullable = false)
    private String toUserId;

    @Column(name = "operate_time", nullable = false)
    private String operateTime;

    @Column(name = "application_id", nullable = false, unique = true)
    private String applicationId;

    @Column(name = "result_type", nullable = false)
    private Integer resultType;

    @Column(name = "is_new_friendship", nullable = false)
    private Boolean isNewFriendship;

    @Column(name = "friendship_id", nullable = false)
    private String friendshipId;

    @Column(name = "private_chat_session_id", nullable = false)
    private String privateChatSessionId;

    @Column(name = "applicant_participant_id", nullable = false)
    private String applicantParticipantId;

    @Column(name = "target_user_participant_id", nullable = false)
    private String targetUserParticipantId;
}