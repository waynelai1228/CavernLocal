package com.cavernservice.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cavernservice.model.Project;
import com.cavernservice.repository.ProjectRepository;
import com.cavernservice.repository.TaskRepository;
import com.cavernservice.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectController {

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  List<Project> getProjects() {
    return projectRepository.findAll();
  }

  @GetMapping(value = "/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Project getProjectById(@PathVariable UUID projectId) {
    return projectRepository.findById(projectId)
      .orElseThrow(() -> new RuntimeException("Project not found"));
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  Project createProject(@RequestBody Project project) {
    System.out.println("project name: " + project.getProjectName());
    return projectRepository.save(project);
  }

  @PutMapping(value = "/{projectId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Project updateProject(@PathVariable UUID projectId, @RequestBody Project updatedProject) {
    return projectRepository.findById(projectId)
      .map(existingProject -> {
        existingProject.setProjectName(updatedProject.getProjectName());
        // update other fields if needed
        return projectRepository.save(existingProject);
      })
      .orElseThrow(() -> new RuntimeException("Project not found"));
  }

  @DeleteMapping(value = "/{projectId}")
  void deleteProject(@PathVariable UUID projectId) {
    System.out.println("attempt to delete project with id: " + projectId);
    projectRepository.deleteById(projectId);
  }

  @GetMapping("/{projectId}/export")
  public ResponseEntity<?> exportTasks(@PathVariable UUID projectId) {
    try {
      String filePath = taskService.exportTasksToJson(projectId);
      return ResponseEntity.ok(Map.of("message", "Tasks exported", "filePath", filePath));
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Failed to export tasks", "details", e.getMessage()));
    }
  }
}
