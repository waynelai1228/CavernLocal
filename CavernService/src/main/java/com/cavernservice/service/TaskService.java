package com.cavernservice.service;

import com.cavernservice.model.Task;
import com.cavernservice.websocket.WebSocketOutputBroadcaster;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private final Map<UUID, WebSocketOutputBroadcaster> broadcasterCache = new ConcurrentHashMap<>();

    public void runTask(Task task) {
        UUID taskId = task.getId();
        WebSocketOutputBroadcaster broadcaster = broadcasterCache.computeIfAbsent(
            taskId, id -> new WebSocketOutputBroadcaster(id.toString())
        );

        task.addOutputListener(broadcaster);
        task.run();
    }
}
