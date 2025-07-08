package org.hdstart.cloud.chat.handler;

import lombok.extern.slf4j.Slf4j;
import org.hdstart.cloud.chat.feign.MemberFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private MemberFeignClient memberFeignClient;

    // 存储所有在线用户的会话
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    // 存储会话ID与用户名的映射
    private final Map<String, String> memberIdMapSessionId = new ConcurrentHashMap<>();
    //村粗昵称
    private final Map<String, String> memberIdMapNickName = new ConcurrentHashMap<>();
    //
    private final Map<String, String> sessionIdMapMemberId = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从URL参数中获取用户名（如果前端在连接时提供）
        String memberId = session.getUri().getQuery();
        if (memberId != null && memberId.startsWith("memberId=")) {
            memberId = memberId.substring(9);
            if (memberId == null || memberId.isEmpty()) {
                return;
            }
            String nickName = memberFeignClient.getMemberNickName(Integer.valueOf(memberId));
            // 存储会话和用户名映射
            String sessionId = session.getId();
            sessions.put(sessionId, session);
            sessionIdMapMemberId.put(sessionId,memberId);
            memberIdMapSessionId.put(memberId, sessionId);
            memberIdMapNickName.put(memberId,nickName);
            log.warn(nickName + "加入了聊天");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        log.info("接收到：" + message.getPayload());
        //拿到被发送人的的memberId和信息
        String json = message.getPayload();
        String[] split = json.split(":");
        String sendMemberId = split[0];
        String receiveMemberId = split[1];
        String sendMessage = message.getPayload();
        String returnMessage = memberIdMapNickName.get(sendMemberId) + ":" + sendMessage;

        if (sendMemberId == null || receiveMemberId == null) {
            log.info("接收者或发送者信息不足，停止发送");
            return;
        }

        //拿到被发送人的sessionId
        String toSessionId = memberIdMapSessionId.get(receiveMemberId);
        if (toSessionId != null) {
            WebSocketSession toSession = sessions.get(toSessionId);
            if (toSession != null && toSession.isOpen()) {
                try {
                    memberFeignClient.storeMessage(Integer.valueOf(sendMemberId),Integer.valueOf(receiveMemberId),split[2],1);
                    pointSendMessage(returnMessage,toSession);
                    log.info("发送成功");
                } catch (Exception e) {
                    log.info("发送或持久化消息失败");
                }
            } else {
                try {
                    memberFeignClient.storeMessage(Integer.valueOf(sendMemberId),Integer.valueOf(receiveMemberId),split[2],0);
                } catch (Exception e) {
                    log.info("持久化消息失败");
                }
            }
        } else {
            try {
                memberFeignClient.storeMessage(Integer.valueOf(sendMemberId),Integer.valueOf(receiveMemberId),split[2],0);
            } catch (Exception e) {
                log.info("持久化消息失败");
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String sessionId = session.getId();
//        String username = sessionIdToUsername.get(sessionId);

//        if (username != null) {
//            sessions.remove(sessionId);
//            sessionIdToUsername.remove(sessionId);
//            broadcastMessage(username + " 离开了聊天");
//        }
        String memberId = sessionIdMapMemberId.get(sessionId);
        sessionIdMapMemberId.remove(sessionId);
        memberIdMapNickName.remove(memberId);
        memberIdMapSessionId.remove(memberId);
        sessions.remove(session.getId());
        log.info(sessionId + "断开连接");
    }

    private void broadcastMessage(String message) {
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions.values()) {
            try {
                session.sendMessage(textMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pointSendMessage (String message,WebSocketSession session) {

        TextMessage textMessage = new TextMessage(message);
        try {
            session.sendMessage(textMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean getReceiveStatus (Integer receiveId) {

        String sessionId = memberIdMapSessionId.get(String.valueOf(receiveId));
        if (sessionId == null || sessionId.isEmpty()) {
            return false;
        }

        WebSocketSession webSocketSession = sessions.get(sessionId);
        if (webSocketSession != null) {
            return true;
        } else {
            return false;
        }
    }
}
