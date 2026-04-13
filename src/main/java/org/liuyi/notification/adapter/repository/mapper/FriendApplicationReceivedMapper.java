package org.liuyi.notification.adapter.repository.mapper;

import com.liuyi.notification.notifications.FriendApplicationReceivedNotification;
import org.liuyi.notification.adapter.repository.persistence.FriendApplicationReceivedNotificationDO;

public class FriendApplicationReceivedMapper {

    public static FriendApplicationReceivedNotification toNotification(FriendApplicationReceivedNotificationDO doObj) {
        return FriendApplicationReceivedNotification.newBuilder()
                .setApplicantUserId(doObj.getApplicantUserId())
                .setApplicationId(doObj.getApplicationId())
                .setVerificationMessage(doObj.getVerificationMessage())
                .setSendTime(doObj.getSendTime())
                .build();
    }

    public static FriendApplicationReceivedNotificationDO toDO(
            FriendApplicationReceivedNotification notification,
            String toUserId
    ) {
        FriendApplicationReceivedNotificationDO doObj = new FriendApplicationReceivedNotificationDO();
        doObj.setToUserId(toUserId);
        doObj.setApplicantUserId(notification.getApplicantUserId());
        doObj.setApplicationId(notification.getApplicationId());
        doObj.setVerificationMessage(notification.getVerificationMessage());
        doObj.setSendTime(notification.getSendTime());
        return doObj;
    }
}
