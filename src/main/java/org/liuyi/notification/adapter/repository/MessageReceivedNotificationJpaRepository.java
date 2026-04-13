package org.liuyi.notification.adapter.repository;

import org.liuyi.notification.adapter.repository.persistence.MessageReceivedNotificationDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageReceivedNotificationJpaRepository extends JpaRepository<MessageReceivedNotificationDO, Long> {

    // 根据 toUserId 查询，按 sendTime 升序
    List<MessageReceivedNotificationDO> findByToUserIdOrderBySendTimeAsc(String toUserId);

    void deleteByToUserId(String toUserId);
}