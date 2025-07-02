package com.cavernservice.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void testConstructorAndGetters() {
        Project project = new Project("Test Project");

        assertNull(project.getId()); // ID is null until set
        assertEquals("Test Project", project.getProjectName());
    }

    @Test
    void testSetters() {
        Project project = new Project();
        UUID id = UUID.randomUUID();

        project.setId(id);
        project.setProjectName("Updated Project");

        assertEquals(id, project.getId());
        assertEquals("Updated Project", project.getProjectName());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();

        Project project1 = new Project("Same Name");
        project1.setId(id);

        Project project2 = new Project("Same Name");
        project2.setId(id);

        assertEquals(project1, project2);
        assertEquals(project1.hashCode(), project2.hashCode());
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        Project project = new Project("ToString Test");
        project.setId(id);

        String output = project.toString();
        assertTrue(output.contains("ToString Test"));
        assertTrue(output.contains(id.toString()));
    }

    @Test
    void testNotEqualsDifferentIdOrName() {
        Project project1 = new Project("Project A");
        project1.setId(UUID.randomUUID());

        Project project2 = new Project("Project B");
        project2.setId(UUID.randomUUID());

        assertNotEquals(project1, project2);
    }
}