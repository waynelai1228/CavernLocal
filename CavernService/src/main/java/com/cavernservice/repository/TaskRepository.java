package com.cavernservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.cavernservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID> {
  List<Task> findByProjectId(UUID projectId);
  Optional<Task> findByIdAndProjectId(UUID taskId, UUID projectId);
}