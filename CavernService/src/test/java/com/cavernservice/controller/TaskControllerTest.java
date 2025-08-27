package com.cavernservice.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.cavernservice.CavernServiceApplication;
import com.cavernservice.controller.TaskController;
import com.cavernservice.model.Project;
import com.cavernservice.model.Task;
import com.cavernservice.repository.ProjectRepository;
import com.cavernservice.repository.TaskRepository;
import com.cavernservice.service.TaskService;
import com.cavernservice.type.TypeTask;

@WebMvcTest(TaskController.class)
@ContextConfiguration(classes = CavernServiceApplication.class)
@Import(TaskControllerTest.MockConfig.class)
public class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @TestConfiguration
  static class MockConfig {
    @Bean
    public TaskRepository taskRepository() {
      return mock(TaskRepository.class);
    }

    @Bean
    public ProjectRepository projectRepository() {
      return mock(ProjectRepository.class);
    }

    @Bean
    public TaskService taskService() {
      return mock(TaskService.class);
    }
  }

  private UUID projectId;
  private UUID taskId;
  private Project sampleProject;
  private Task sampleTask;

  @BeforeEach
  void setUp() {
    projectId = UUID.randomUUID();
    taskId = UUID.randomUUID();

    sampleProject = new Project("Sample Project");
    sampleProject.setId(projectId);

    sampleTask = new Task("Sample Task", "Do something", "echo test", TypeTask.BASH);
    sampleTask.setId(taskId);
    sampleTask.setProject(sampleProject);

    clearInvocations(taskRepository, projectRepository, taskService);
  }

  @Test
  void testGetTasksByProject() throws Exception {
    when(taskRepository.findByProjectId(projectId)).thenReturn(List.of(sampleTask));

    mockMvc.perform(get("/projects/" + projectId + "/tasks"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].task_name").value("Sample Task"))
      .andExpect(jsonPath("$[0].task_description").value("Do something"))
      .andExpect(jsonPath("$[0].task_action").value("echo test"))
      .andExpect(jsonPath("$[0].task_type").value("BASH"));
  }

  @Test
  void testGetTaskById_Success() throws Exception {
    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(sampleTask));

    mockMvc.perform(get("/projects/" + projectId + "/tasks/" + taskId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.task_name").value("Sample Task"))
      .andExpect(jsonPath("$.task_description").value("Do something"));
  }

  @Test
  void testGetTaskById_NotFound() throws Exception {
    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/projects/" + projectId + "/tasks/" + taskId))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value("Task not found with ID " + projectId + " " + taskId));
  }

  @Test
  void testCreateTask_Success() throws Exception {
    String json = """
      {
        "task_name": "New Task",
        "task_description": "Something new",
        "task_action": "ls -la",
        "task_type": "BASH"
      }
      """;

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));
    // Mock save to return a Task matching the input (simulate saved entity)
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
      Task t = invocation.getArgument(0);
      t.setId(taskId);
      t.setProject(sampleProject);
      return t;
    });

    mockMvc.perform(post("/projects/" + projectId + "/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.task_name").value("New Task"))
      .andExpect(jsonPath("$.task_description").value("Something new"))
      .andExpect(jsonPath("$.task_action").value("ls -la"))
      .andExpect(jsonPath("$.task_type").value("BASH"));

    verify(taskRepository).save(any(Task.class));
  }

  @Test
  void testCreateTask_ProjectNotFound() throws Exception {
    String json = """
      {
        "task_name": "New Task",
        "task_description": "Something new",
        "task_action": "ls -la",
        "task_type": "BASH"
      }
      """;

    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

    mockMvc.perform(post("/projects/" + projectId + "/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value("Project not found"));

    verify(taskRepository, never()).save(any());
  }

  @Test
  void testUpdateTask_Success() throws Exception {
    String json = """
      {
        "task_name": "Updated Task",
        "task_description": "Updated desc",
        "task_action": "whoami",
        "task_type": "BASH"
      }
      """;

    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(sampleTask));
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(put("/projects/" + projectId + "/tasks/" + taskId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.task_name").value("Updated Task"))
      .andExpect(jsonPath("$.task_description").value("Updated desc"))
      .andExpect(jsonPath("$.task_action").value("whoami"))
      .andExpect(jsonPath("$.task_type").value("BASH"));

    verify(taskRepository).save(any(Task.class));
  }

  @Test
  void testUpdateTask_NotFound() throws Exception {
    String json = """
      {
        "task_name": "Updated Task",
        "task_description": "Updated desc",
        "task_action": "whoami",
        "task_type": "BASH"
      }
      """;

    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.empty());

    mockMvc.perform(put("/projects/" + projectId + "/tasks/" + taskId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value("Task not found in project"));

    verify(taskRepository, never()).save(any());
  }

  @Test
  void testDeleteTask_Success() throws Exception {
    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(sampleTask));

    mockMvc.perform(delete("/projects/" + projectId + "/tasks/" + taskId))
      .andExpect(status().isNoContent());

    verify(taskRepository).delete(sampleTask);
  }

  @Test
  void testDeleteTask_NotFound() throws Exception {
    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.empty());

    mockMvc.perform(delete("/projects/" + projectId + "/tasks/" + taskId))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value("Task not found in project"));

    verify(taskRepository, never()).delete(any());
  }

  @Test
  void testRunTask_Success() throws Exception {
    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(sampleTask));
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(post("/projects/" + projectId + "/tasks/" + taskId + "/run"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.task_name").value("Sample Task"));

    verify(taskService).runTask(sampleTask);
    verify(taskRepository).save(sampleTask);
  }

  @Test
  void testRunTask_NotFound() throws Exception {
    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.empty());

    mockMvc.perform(post("/projects/" + projectId + "/tasks/" + taskId + "/run"))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value("Task not found in project"));

    verify(taskService, never()).runTask(any());
    verify(taskRepository, never()).save(any());
  }
}
