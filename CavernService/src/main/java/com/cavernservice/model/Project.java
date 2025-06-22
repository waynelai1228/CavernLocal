package com.cavernservice.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @JsonProperty("project_name")
  private String projectName;

  Project() {}

  public Project(String projectName) {
    this.projectName = projectName;
  }

  public UUID getId() {
    return this.id;
  }

  public String getProjectName() {
    return this.projectName;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o)
      return true;
    if (!(o instanceof Project))
      return false;
    Project project = (Project) o;
    return Objects.equals(this.id, project.id) && Objects.equals(this.projectName, project.projectName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.projectName);
  }

  @Override
  public String toString() {
    return "project{" + "id=" + this.id + ", name='" + this.projectName + '\'' + '}';
  }
}