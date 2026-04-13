package org.liuyi.notification.adapter.client;

import lombok.RequiredArgsConstructor;
import org.liuyi.chat_api.dubbo.get_session_userIds.GetSessionUserIdsRequest;
import org.liuyi.common.domain.exception.DomainException;
import org.liuyi.notification.port.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ChatClientAdapter implements ChatClient {
    private final ChatApiGateway apiGateway;

    @Override
    public Set<String> getSessionUserIds(String sessionId) {
        // 构造request
        GetSessionUserIdsRequest request = GetSessionUserIdsRequest.builder()
                .sessionId(sessionId)
                .build();

        // 发起调用
        var resp = apiGateway.getSessionUserIds(request);
        if (!resp.isSuccess()) {
            throw new DomainException("调用ChatApiGateway.getSessionUserIds出现领域错误: " + resp.getErrMsg());
        }
        return resp.getUserIds();
    }
}
