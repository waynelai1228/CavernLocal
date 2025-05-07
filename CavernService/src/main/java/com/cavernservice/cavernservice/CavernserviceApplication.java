package com.cavernservice.cavernservice;
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


    // Inner class for the Project model (move this to its own file)
    static class Project {
        private int id;
        private String name;

        public Project(int id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters are required for JSON serialization
        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

	@RequestMapping(value="/api/projects", produces = MediaType.APPLICATION_JSON_VALUE)
	List<Project> getProjects() {
        return Arrays.asList(
            new Project(1, "Project Alpha"),
            new Project(2, "Project Beta"),
            new Project(3, "Project Gamma")
        );

	}

	public static void main(String[] args) {
		SpringApplication.run(CavernserviceApplication.class, args);
	}

}
