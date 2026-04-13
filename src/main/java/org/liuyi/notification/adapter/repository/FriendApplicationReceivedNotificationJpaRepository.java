package org.liuyi.notification.adapter.repository;

import org.liuyi.notification.adapter.repository.persistence.FriendApplicationReceivedNotificationDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendApplicationReceivedNotificationJpaRepository extends JpaRepository<FriendApplicationReceivedNotificationDO, Long> {

    List<FriendApplicationReceivedNotificationDO> findByToUserIdOrderBySendTimeAsc(String toUserId);

    void deleteByToUserId(String toUserId);
}