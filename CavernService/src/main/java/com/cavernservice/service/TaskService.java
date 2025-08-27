package com.cavernservice.service;

import com.cavernservice.model.Task;
import com.cavernservice.websocket.WebSocketOutputBroadcaster;
import com.cavernservice.utils.CavernFileWriter;
import com.cavernservice.model.Task;
import com.cavernservice.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    private final Map<UUID, WebSocketOutputBroadcaster> broadcasterCache = new ConcurrentHashMap<>();

    public void runTask(Task task) {
        UUID taskId = task.getId();
        WebSocketOutputBroadcaster broadcaster = broadcasterCache.computeIfAbsent(
            taskId, id -> new WebSocketOutputBroadcaster(id.toString())
        );

        task.addOutputListener(broadcaster);
        task.run();
    }

    
    public String exportTasksToJson(UUID projectId) throws IOException {
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        // Define output path (e.g., temporary file)
        Path outputPath = Files.createTempFile("tasks-" + projectId, ".json");

        CavernFileWriter.writeTasksToFile(tasks, outputPath.toString());

        return outputPath.toString();
    }
}
