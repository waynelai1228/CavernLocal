package com.cavernservice.websocket;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskOutputWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, Set<WebSocketSession>> taskSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String taskId = getTaskIdFromUri(session);
        if (taskId != null) {
            taskSessions.computeIfAbsent(taskId, k -> ConcurrentHashMap.newKeySet()).add(session);
            System.out.println("WebSocket opened for task: " + taskId);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Optional: Handle messages from the client
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String taskId = getTaskIdFromUri(session);
        if (taskId != null) {
            Set<WebSocketSession> sessions = taskSessions.get(taskId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    taskSessions.remove(taskId);
                }
            }
            System.out.println("WebSocket closed for task: " + taskId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String taskId = getTaskIdFromUri(session);
        System.err.println("WebSocket error for task " + taskId + ": " + exception.getMessage());
        afterConnectionClosed(session, CloseStatus.SERVER_ERROR);
    }

    public static void sendTaskOutput(String taskId, String output, String streamType) {
        Set<WebSocketSession> sessions = taskSessions.get(taskId);
        if (sessions != null) {
            String json = String.format("{\"stream\":\"%s\", \"data\":\"%s\"}", streamType, escapeJson(output));
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(json));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static String getTaskIdFromUri(WebSocketSession session) {
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String[] segments = path.split("/");
        if (segments.length >= 4) {
            return segments[3]; // e.g., /ws/tasks/{taskId}/output
        }
        return null;
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}
