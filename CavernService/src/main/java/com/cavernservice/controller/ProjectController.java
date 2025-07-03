package com.cavernservice.controller;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.cavernservice.model.Project;
import com.cavernservice.repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

	@GetMapping(value="/projects", produces = MediaType.APPLICATION_JSON_VALUE)
	List<Project> getProjects() {
        return projectRepository.findAll();
	}

    @GetMapping(value="/projects/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    Project getProjectById(@PathVariable UUID projectId) {
        return projectRepository.findById(projectId).get();
    }

    @PostMapping(value="/new_project")
    Project createNewProject(@RequestBody Project project) {
        System.out.println("project name: " + project.getProjectName());
        return projectRepository.save(project);
    }

    @DeleteMapping(value="/delete_project/{projectId}")
    void deleteProject(@PathVariable UUID projectId) {
        System.out.println("attempt to delete project with id: " + projectId);
        projectRepository.deleteById(projectId);
    }
}