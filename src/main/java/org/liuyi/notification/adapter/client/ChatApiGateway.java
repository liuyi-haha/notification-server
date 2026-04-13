package org.liuyi.notification.adapter.client;

import org.liuyi.chat_api.dubbo.get_session_userIds.GetSessionUserIdsRequest;
import org.liuyi.chat_api.dubbo.get_session_userIds.GetSessionUserIdsResponse;

public interface ChatApiGateway {
    GetSessionUserIdsResponse getSessionUserIds(GetSessionUserIdsRequest req);

}
