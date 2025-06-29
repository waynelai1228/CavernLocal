package com.cavernservice.repository;

import java.util.UUID;

import com.cavernservice.model.Project;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
}