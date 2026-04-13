package org.liuyi.notification.adapter.repository;

import com.liuyi.notification.notifications.FriendApplicationHandledNotification;
import lombok.RequiredArgsConstructor;
import org.liuyi.notification.adapter.repository.mapper.FriendApplicationHandledMapper;
import org.liuyi.notification.adapter.repository.persistence.FriendApplicationHandledNotificationDO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendApplicationHandledNotificationRepository {

    private final FriendApplicationHandledNotificationJpaRepository jpaRepository;

    public FriendApplicationHandledNotificationDO save(FriendApplicationHandledNotification notification, String toUserId) {
        return jpaRepository.save(FriendApplicationHandledMapper.toDO(notification, toUserId));
    }

    public List<FriendApplicationHandledNotification> findByToUserId(String toUserId) {
        List<FriendApplicationHandledNotificationDO> doList = jpaRepository.findByToUserIdOrderByOperateTimeAsc(toUserId);
        return doList.stream()
                .map(FriendApplicationHandledMapper::toNotification)
                .toList();
    }

    public void deleteByToUserId(String toUserId) {
        jpaRepository.deleteByToUserId(toUserId);
    }
}
