package com.cavernservice.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import com.cavernservice.CavernServiceApplication;
import com.cavernservice.controller.TaskController;
import com.cavernservice.model.Project;
import com.cavernservice.model.Task;
import com.cavernservice.repository.ProjectRepository;
import com.cavernservice.repository.TaskRepository;
import com.cavernservice.type.TypeTask;

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

@WebMvcTest(TaskController.class)
@ContextConfiguration(classes = CavernServiceApplication.class)
@Import(TaskControllerTest.MockConfig.class)
public class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

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

    clearInvocations(taskRepository, projectRepository);
  }

  @Test
  void testGetTasksByProject() throws Exception {
    when(taskRepository.findByProjectId(projectId)).thenReturn(Arrays.asList(sampleTask));

    mockMvc.perform(get("/projects/" + projectId + "/tasks"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].task_name").value("Sample Task"))
      .andExpect(jsonPath("$[0].task_description").value("Do something"))
      .andExpect(jsonPath("$[0].task_action").value("echo test"))
      .andExpect(jsonPath("$[0].task_type").value("BASH"));
  }

  @Test
  void testGetTaskById() throws Exception {
    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(sampleTask));

    mockMvc.perform(get("/projects/" + projectId + "/tasks/" + taskId))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.task_name").value("Sample Task"));
  }

  @Test
  void testCreateTask() throws Exception {
    String json = """
    {
      "task_name": "New Task",
      "task_description": "Something new",
      "task_action": "ls -la",
      "task_type": "BASH"
    }
    """;

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));
    when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

    mockMvc.perform(post("/projects/" + projectId + "/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isOk());

    verify(taskRepository, times(1)).save(any(Task.class));
  }

  @Test
  void testUpdateTask() throws Exception {
    String json = """
    {
      "task_name": "Updated Task",
      "task_description": "Updated desc",
      "task_action": "whoami",
      "task_type": "BASH"
    }
    """;

    Task updatedTask = new Task("Updated Task", "Updated desc", "whoami", TypeTask.BASH);
    updatedTask.setId(taskId);
    updatedTask.setProject(sampleProject);

    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(sampleTask));
    when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

    mockMvc.perform(put("/projects/" + projectId + "/tasks/" + taskId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.task_name").value("Updated Task"));

    verify(taskRepository, times(1)).save(any(Task.class));
  }

  @Test
  void testDeleteTask() throws Exception {
    when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(sampleTask));

    mockMvc.perform(delete("/projects/" + projectId + "/tasks/" + taskId))
      .andExpect(status().isOk());

    verify(taskRepository, times(1)).delete(sampleTask);
  }
}
