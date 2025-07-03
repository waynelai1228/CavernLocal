import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import com.cavernservice.CavernServiceApplication;
import com.cavernservice.controller.ProjectController;
import com.cavernservice.model.Project;
import com.cavernservice.repository.ProjectRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
@ContextConfiguration(classes = CavernServiceApplication.class)
@Import(ProjectControllerTest.MockConfig.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ProjectRepository projectRepository() {
            return mock(ProjectRepository.class);
        }
    }

    private Project sampleProject;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleProject = new Project("Test Project");
        sampleProject.setId(sampleId);
    }

    @Test
    void testGetProjects() throws Exception {
        when(projectRepository.findAll()).thenReturn(Arrays.asList(sampleProject));

        mockMvc.perform(get("/projects"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].project_name").value("Test Project"));
    }

    @Test
    void testGetProjectById() throws Exception {
        when(projectRepository.findById(sampleId)).thenReturn(Optional.of(sampleProject));

        mockMvc.perform(get("/projects/" + sampleId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.project_name").value("Test Project"));
    }

    @Test
    void testCreateNewProject() throws Exception {
        String json = "{\"projectName\":\"Test Project\"}";

        mockMvc.perform(post("/new_project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk());

        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testDeleteProject() throws Exception {
        mockMvc.perform(delete("/delete_project/" + sampleId))
            .andExpect(status().isOk());

        verify(projectRepository, times(1)).deleteById(sampleId);
    }
}