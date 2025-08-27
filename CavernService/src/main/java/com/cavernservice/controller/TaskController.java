package com.cavernservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.cavernservice.model.Project;
import com.cavernservice.model.Task;
import com.cavernservice.repository.ProjectRepository;
import com.cavernservice.repository.TaskRepository;
import com.cavernservice.service.TaskService;
import com.cavernservice.websocket.WebSocketOutputBroadcaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private TaskService taskService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Task> getTasksByProject(@PathVariable UUID projectId) {
    return taskRepository.findByProjectId(projectId);
  }

  @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getTaskById(@PathVariable UUID projectId, @PathVariable UUID taskId) {
    Optional<Task> optionalTask = taskRepository.findByIdAndProjectId(taskId, projectId);

    if (optionalTask.isEmpty()) {
      Map<String, String> error = new HashMap<>();
      error.put("error", "Task not found with ID " + projectId + " " + taskId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    Task task = optionalTask.get();
    return ResponseEntity.ok(task);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createTask(@PathVariable UUID projectId, @RequestBody Task task) {
    Optional<Project> optionalProject = projectRepository.findById(projectId);

    if (optionalProject.isEmpty()) {
      Map<String, String> error = new HashMap<>();
      error.put("error", "Project not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    Project project = optionalProject.get();

    task.setProject(project);
    return ResponseEntity.ok(taskRepository.save(task));
  }

  @PostMapping(value = "/{taskId}/run", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> runTask(@PathVariable UUID projectId, @PathVariable UUID taskId) {
    Optional<Task> optionalTask = taskRepository.findByIdAndProjectId(taskId, projectId);

    if (optionalTask.isEmpty()) {
      Map<String, String> error = new HashMap<>();
      error.put("error", "Task not found in project");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    Task task = optionalTask.get();

    taskService.runTask(task);
    return ResponseEntity.ok(taskRepository.save(task));
  }

  @PutMapping(value = "/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateTask(@PathVariable UUID projectId,
                                      @PathVariable UUID taskId,
                                      @RequestBody Task updatedTask) {
    Optional<Task> optionalTask = taskRepository.findByIdAndProjectId(taskId, projectId);

    if (optionalTask.isEmpty()) {
      Map<String, String> error = new HashMap<>();
      error.put("error", "Task not found in project");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    Task existingTask = optionalTask.get();
    existingTask.setTaskName(updatedTask.getTaskName());
    existingTask.setTaskDescription(updatedTask.getTaskDescription());
    existingTask.setTaskAction(updatedTask.getTaskAction());
    existingTask.setTaskType(updatedTask.getTaskType());
    existingTask.setTaskResult(updatedTask.getTaskResult());

    Task savedTask = taskRepository.save(existingTask);
    return ResponseEntity.ok(savedTask);
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<?> deleteTask(@PathVariable UUID projectId, @PathVariable UUID taskId) {
    Optional<Task> optionalTask = taskRepository.findByIdAndProjectId(taskId, projectId);

    if (optionalTask.isEmpty()) {
      Map<String, String> error = new HashMap<>();
      error.put("error", "Task not found in project");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    taskRepository.delete(optionalTask.get());
    return ResponseEntity.noContent().build();
  }
}
