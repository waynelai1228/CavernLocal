package com.cavernservice.controller;

import java.util.List;
import java.util.UUID;

import com.cavernservice.model.Project;
import com.cavernservice.model.Task;
import com.cavernservice.repository.ProjectRepository;
import com.cavernservice.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Task> getTasksByProject(@PathVariable UUID projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Task getTaskById(@PathVariable UUID projectId, @PathVariable UUID taskId) {
        return taskRepository.findByIdAndProjectId(taskId, projectId)
            .orElseThrow(() -> new RuntimeException("Task not found in project"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Task createTask(@PathVariable UUID projectId, @RequestBody Task task) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));

        task.setProject(project);
        return taskRepository.save(task);
    }

    @PutMapping(value = "/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Task updateTask(@PathVariable UUID projectId,
                           @PathVariable UUID taskId,
                           @RequestBody Task updatedTask) {
        return taskRepository.findByIdAndProjectId(taskId, projectId)
            .map(existingTask -> {
                existingTask.setTaskName(updatedTask.getTaskName());
                existingTask.setTaskDescription(updatedTask.getTaskDescription());
                existingTask.setTaskAction(updatedTask.getTaskAction());
                existingTask.setTaskType(updatedTask.getTaskType());
                existingTask.setTaskResult(updatedTask.getTaskResult());
                return taskRepository.save(existingTask);
            })
            .orElseThrow(() -> new RuntimeException("Task not found in project"));
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable UUID projectId, @PathVariable UUID taskId) {
        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
            .orElseThrow(() -> new RuntimeException("Task not found in project"));
        taskRepository.delete(task);
    }
}
