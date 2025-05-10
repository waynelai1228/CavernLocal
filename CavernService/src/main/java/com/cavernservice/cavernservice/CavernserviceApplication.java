package com.cavernservice.cavernservice;

import com.cavernservice.model.Project;

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

import java.util.Arrays;
import java.util.List;

@RestController
@SpringBootApplication
public class CavernserviceApplication {
	@GetMapping(value="/projects", produces = MediaType.APPLICATION_JSON_VALUE)
	List<Project> getProjects() {
        return Arrays.asList(
            new Project("Project Alpha"),
            new Project("Project Beta"),
            new Project("Project Gamma")
        );
	}

    @PostMapping(value="/new_project")
    void createNewProject(@RequestBody Project project) {
        System.out.println("project name: " + project.getProjectName());
    }

	public static void main(String[] args) {
		SpringApplication.run(CavernserviceApplication.class, args);
	}

}
