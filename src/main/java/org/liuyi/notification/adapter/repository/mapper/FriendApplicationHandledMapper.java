package org.liuyi.notification.adapter.repository.mapper;

import com.liuyi.notification.notifications.FriendApplicationHandledNotification;
import org.liuyi.notification.adapter.repository.persistence.FriendApplicationHandledNotificationDO;

public class FriendApplicationHandledMapper {

    public static FriendApplicationHandledNotificationDO toDO(
            FriendApplicationHandledNotification notification,
            String toUserId
    ) {
        FriendApplicationHandledNotificationDO doObj = new FriendApplicationHandledNotificationDO();
        doObj.setToUserId(toUserId);
        doObj.setOperateTime(notification.getOperateTime());
        doObj.setApplicationId(notification.getApplicationId());
        doObj.setResultType(notification.getResultTypeValue());
        doObj.setIsNewFriendship(notification.getIsNewFriendship());
        doObj.setFriendshipId(notification.getFriendshipId());
        doObj.setPrivateChatSessionId(notification.getPrivateChatSessionId());
        doObj.setApplicantParticipantId(notification.getApplicantParticipantId());
        doObj.setTargetUserParticipantId(notification.getTargetUserParticipantId());
        return doObj;
    }

    public static FriendApplicationHandledNotification toNotification(FriendApplicationHandledNotificationDO doObj) {
        return FriendApplicationHandledNotification.newBuilder()
                .setOperateTime(doObj.getOperateTime())
                .setApplicationId(doObj.getApplicationId())
                .setResultTypeValue(doObj.getResultType())
                .setIsNewFriendship(doObj.getIsNewFriendship())
                .setFriendshipId(doObj.getFriendshipId())
                .setPrivateChatSessionId(doObj.getPrivateChatSessionId())
                .setApplicantParticipantId(doObj.getApplicantParticipantId())
                .setTargetUserParticipantId(doObj.getTargetUserParticipantId())
                .build();
    }
}