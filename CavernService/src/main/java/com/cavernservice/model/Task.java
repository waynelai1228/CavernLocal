package com.cavernservice.model;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.UUID;

import com.cavernservice.type.TypeTask;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import org.apache.commons.exec.CommandLine;

// Docker SDK:

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;

// DockerContainerManager
import com.cavernservice.service.DockerContainerManager;
import com.github.dockerjava.api.model.StreamType;


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
            // Ensure the task is only run on Linux
            String os = System.getProperty("os.name").toLowerCase();
            if (!os.contains("linux")) {
                throw new UnsupportedOperationException("BASH tasks are only supported on Linux systems.");
            }

            //// ** Defensive approach: Verify that taskAction is a valid bash command **
            //if (!isValidBashCommand(taskAction)) {
            //    throw new IllegalArgumentException("Invalid BASH command: potential security risk.");
            //}

            // Get Docker client and container ID
            DockerClient dockerClient = DockerContainerManager.getClient();
            String containerId = DockerContainerManager.getContainerId();

            // Execute the task action in the container
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withCmd("bash", "-c", taskAction)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();

            // Declare output streams for capturing stdout and stderr
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();

            // Use the ResultCallback Adapter to capture output
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        try {
                            if (frame.getStreamType() == StreamType.STDOUT) {
                              byte[] framePayload = frame.getPayload();
                              stdout.write(framePayload);
                               // Dynamically update task result for stdout
                              synchronized (Task.this) {
                                taskResult = (taskResult == null ? "" : taskResult) + new String(framePayload);
                              }
                            } else if (frame.getStreamType() == StreamType.STDERR) {
                              byte[] framePayload = frame.getPayload();
                              stderr.write(framePayload);
                              synchronized (Task.this) {
                                taskResult = (taskResult == null ? "" : taskResult) + new String(framePayload);
                              }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Error while writing frame payload to output stream", e);
                        }
                    }
                })
                .awaitCompletion();  // Wait for the command to complete

            // Set task result
            if (stderr.size() > 0) {
              // Capture stderr if any error occurred
              synchronized (Task.this) {
                taskResult = "Error: " + stderr.toString().trim();
              }
            } else {
              // Otherwise, capture stdout
              synchronized (Task.this) {
                taskResult = stdout.toString().trim();
              }
            }

        } catch (InterruptedException e) {
            this.taskResult = "Execution failed: " + e.getMessage();
            e.printStackTrace();
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            this.taskResult = "Validation failed: " + e.getMessage();
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