package org.liuyi.notification.adapter.client;

import org.apache.dubbo.config.annotation.DubboReference;
import org.liuyi.chat_api.dubbo.ChatService;
import org.liuyi.chat_api.dubbo.get_session_userIds.GetSessionUserIdsRequest;
import org.liuyi.chat_api.dubbo.get_session_userIds.GetSessionUserIdsResponse;
import org.springframework.stereotype.Component;

@Component
public class ChatApiGatewayImpl implements ChatApiGateway {

    @DubboReference
    private ChatService chatService;

    @Override
    public GetSessionUserIdsResponse getSessionUserIds(GetSessionUserIdsRequest req) {
        return chatService.getSessionUserIds(req);
    }
}
