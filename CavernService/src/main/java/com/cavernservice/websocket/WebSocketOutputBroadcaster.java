package com.cavernservice.websocket;

import com.cavernservice.listener.TaskOutputListener;
import com.github.dockerjava.api.model.StreamType;

public class WebSocketOutputBroadcaster implements TaskOutputListener {

    private final String taskId;

    public WebSocketOutputBroadcaster(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public void onOutput(String output, StreamType streamType) {
        TaskOutputWebSocketHandler.sendTaskOutput(taskId, output, streamType.name());
    }
}

