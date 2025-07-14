package com.cavernservice.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.UUID;

import com.cavernservice.type.TypeTask;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import org.apache.commons.exec.CommandLine;

@Entity
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @JsonProperty("task_name")
  private String taskName;

  @JsonProperty("task_description")
  private String taskDescription;

  @JsonProperty("task_action")
  private String taskAction;

  @Column(name = "task_result", columnDefinition = "TEXT")
  @JsonProperty("task_result")
  private String taskResult;

  @JsonProperty("task_type")
  @Enumerated(EnumType.STRING)
  private TypeTask taskType;

  Task() {}

  public Task(String taskName, String taskDescription, String taskAction, TypeTask taskType) {
    this.taskName = taskName;
    this.taskDescription = taskDescription;
    this.taskAction = taskAction;
    this.taskType = taskType;
  }

  public UUID getId() {
    return this.id;
  }

  public Project getProject() {
    return project;
  }

  public String getTaskName() {
    return this.taskName;
  }

  public String getTaskDescription() {
    return this.taskDescription;
  }

  public String getTaskAction() {
    return this.taskAction;
  }

  public String getTaskResult() {
    return this.taskResult;
  }

  public TypeTask getTaskType() {
    return this.taskType;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public void setTaskDescription(String taskDescription) {
    this.taskDescription = taskDescription;
  }

  public void setTaskAction(String taskAction) {
    this.taskAction = taskAction;
  }

  public void setTaskResult(String taskResult) {
    this.taskResult = taskResult;
  }

  public void setTaskType(TypeTask taskType) {
    this.taskType = taskType;
  }

  public void run() {
    if (taskType == TypeTask.BASH) {
      try {
        // Detect OS
        String os = System.getProperty("os.name").toLowerCase();

        if (!os.contains("linux")) {
          throw new UnsupportedOperationException("BASH tasks are only supported on Linux systems.");
        }

        // ******* DANGEROUS FUNCTIONALITY *********** //
        // ** This contains OS Command Injection ** //
        // ** Do not ever deploy to production system **//
        // ******************************************* //

        // take taskAction as bash command and run it
        CommandLine cmdLine = CommandLine.parse(this.taskAction);
        String[] arguments = cmdLine.toStrings();

        ProcessBuilder builder = new ProcessBuilder(arguments);

        Process process = builder.start();

        // Read output
        BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream())
        );

        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
          output.append(line).append("\n");
        }
        // Store the result
        this.taskResult = output.toString().trim();

        int exitCode = process.waitFor();
        System.out.println("Exited with code: " + exitCode);
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public boolean equals(Object o) {

    if (this == o)
      return true;
    if (!(o instanceof Task))
      return false;
    Task task = (Task) o;
    return Objects.equals(this.id, task.id) && Objects.equals(this.taskName, task.taskName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.taskName);
  }

  @Override
  public String toString() {
    return "task{" + "id=" + this.id + ", name='" + this.taskName + '\'' + '}';
  }
}