package org.liuyi.notification.adapter.repository;

import com.liuyi.notification.notifications.FriendApplicationReceivedNotification;
import lombok.RequiredArgsConstructor;
import org.liuyi.notification.adapter.repository.mapper.FriendApplicationReceivedMapper;
import org.liuyi.notification.adapter.repository.persistence.FriendApplicationReceivedNotificationDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendApplicationReceivedNotificationRepository {

    private final FriendApplicationReceivedNotificationJpaRepository jpaRepository;

    public FriendApplicationReceivedNotificationDO save(FriendApplicationReceivedNotification notification, String toUserId) {
        return jpaRepository.save(FriendApplicationReceivedMapper.toDO(notification, toUserId));
    }

    public List<FriendApplicationReceivedNotification> findByToUserId(String toUserId) {
        List<FriendApplicationReceivedNotificationDO> doList = jpaRepository.findByToUserIdOrderBySendTimeAsc(toUserId);
        return doList.stream()
                .map(FriendApplicationReceivedMapper::toNotification)
                .toList();
    }

    public void deleteByToUserId(String toUserId) {
        jpaRepository.deleteByToUserId(toUserId);
    }
}