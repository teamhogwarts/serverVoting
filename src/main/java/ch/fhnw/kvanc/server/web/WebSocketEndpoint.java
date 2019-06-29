package ch.fhnw.kvanc.server.web;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketEndpoint extends TextWebSocketHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<String, WebSocketSession>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.debug("Established new session " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        super.afterConnectionClosed(session, status);
        log.debug("Closed session " + session.getId());
    }

    public void pushMessage(String message) throws IOException {
        if (!sessions.isEmpty()) {
            for (WebSocketSession session : sessions.values()) {
                TextMessage text = new TextMessage(message);
                session.sendMessage(text);
                log.debug("Sent textmessage to session " + session.getId());
            }
        } else {
            log.debug("No session available to push message");
        }
    }

    public void reset() throws IOException {
        if (!sessions.isEmpty()) {
            for (WebSocketSession session : sessions.values()) {
                session.close(CloseStatus.SERVICE_RESTARTED);
                log.debug("Session '{}' closed", session.getId());
            }
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.debug("Pong received from session " + session.getId());
    }

    // 30 sec following IETF recommendation: see http://tools.ietf.org/html/rfc6202
    @Scheduled(fixedRate = 30000)
    public void sendPing() throws IOException {
        if (!sessions.isEmpty()) {
            PingMessage pingMessage = new PingMessage();
            for (WebSocketSession session : sessions.values()) {
                session.sendMessage(pingMessage);
                log.debug("Sent PING to session " + session.getId());
            }
        } else {
            log.debug("No session available");
        }
    }

}
