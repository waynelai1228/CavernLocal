package com.cavernservice.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TaskOutputWebSocketHandler taskHandler;

    public WebSocketConfig(TaskOutputWebSocketHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(taskHandler, "/ws/tasks/{taskId}/output")
                .setAllowedOrigins("*");
    }
}
