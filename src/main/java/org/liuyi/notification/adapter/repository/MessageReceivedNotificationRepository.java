package org.liuyi.notification.adapter.repository;

import com.liuyi.notification.notifications.MessageReceivedNotification;
import lombok.RequiredArgsConstructor;
import org.liuyi.notification.adapter.repository.mapper.MessageReceivedNotificationMapper;
import org.liuyi.notification.adapter.repository.persistence.MessageReceivedNotificationDO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class MessageReceivedNotificationRepository {
    private final MessageReceivedNotificationJpaRepository jpaRepository;

    // 保存
    public void save(MessageReceivedNotification notification, String toUserId) {
        jpaRepository.save(MessageReceivedNotificationMapper.toDO(notification, toUserId));
    }

    // 查询
    public List<MessageReceivedNotification> findByToUserId(String toUserId) {
        List<MessageReceivedNotificationDO> doList = jpaRepository.findByToUserIdOrderBySendTimeAsc(toUserId);
        return doList.stream()
                .map(MessageReceivedNotificationMapper::toNotification)
                .collect(Collectors.toList());
    }

    // 删除
    public void deleteByToUserId(String toUserId) {
        jpaRepository.deleteByToUserId(toUserId);
    }
}
