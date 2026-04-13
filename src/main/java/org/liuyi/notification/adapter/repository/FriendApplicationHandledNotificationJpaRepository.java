package org.liuyi.notification.adapter.repository;

import org.liuyi.notification.adapter.repository.persistence.FriendApplicationHandledNotificationDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendApplicationHandledNotificationJpaRepository extends JpaRepository<FriendApplicationHandledNotificationDO, Long> {

    List<FriendApplicationHandledNotificationDO> findByToUserIdOrderByOperateTimeAsc(String toUserId);

    void deleteByToUserId(String toUserId);
}
