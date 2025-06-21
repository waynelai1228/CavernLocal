package com.cavernservice.cavernservice;

import com.cavernservice.model.Project;
import com.cavernservice.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;
import java.util.List;

@RestController
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.cavernservice.repository")
@EntityScan(basePackages = "com.cavernservice.model")
public class CavernserviceApplication {

    @Autowired
    private ProjectRepository projectRepository;

	@GetMapping(value="/projects", produces = MediaType.APPLICATION_JSON_VALUE)
	List<Project> getProjects() {
        return projectRepository.findAll();
	}

    @PostMapping(value="/new_project")
    void createNewProject(@RequestBody Project project) {
        System.out.println("project name: " + project.getProjectName());
        projectRepository.save(project);
    }

    @DeleteMapping(value="/delete_project")
    void deleteProject(@RequestBody Project project) {
        System.out.println("attempt to delete: " + project.getProjectName());
    }

	public static void main(String[] args) {
		SpringApplication.run(CavernserviceApplication.class, args);
	}

}
