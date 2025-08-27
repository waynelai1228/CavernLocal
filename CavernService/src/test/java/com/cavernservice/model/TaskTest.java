package com.cavernservice.model;

import com.cavernservice.type.TypeTask;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @BeforeAll
    static void ensureDockerIsRunning() {
        try {
            var client = com.cavernservice.service.DockerContainerManager.getClient();
            assertNotNull(client);
            var containerId = com.cavernservice.service.DockerContainerManager.getContainerId();
            assertNotNull(containerId);
        } catch (Exception e) {
            fail("Docker is not running or not accessible: " + e.getMessage());
        }
    }

    @Test
    void testRunBashTask_success() {
        // Arrange
        Task task = new Task("Echo Test", "Runs echo command", "echo Hello, World!", TypeTask.BASH);

        // Act
        task.run();

        // Assert
        String result = task.getTaskResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains("Hello, World!"), "Result should contain 'Hello, World!'");
    }

    @Test
    void testRunBashTask_errorOutput() {
        // Arrange
        Task task = new Task("Invalid Command", "Runs an invalid command", "invalid_command_xyz", TypeTask.BASH);

        // Act
        task.run();

        // Assert
        String result = task.getTaskResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.startsWith("Error:"), "Result should indicate error");
        assertTrue(result.toLowerCase().contains("invalid"), "Error message should mention invalid command");
    }

    @Test
    void testRunBashTask_withMultipleCommands() {
        // Arrange
        Task task = new Task("Multiple Commands", "Runs multiple shell commands", "echo Line1 && echo Line2", TypeTask.BASH);

        // Act
        task.run();

        // Assert
        String result = task.getTaskResult();
        assertNotNull(result);
        assertTrue(result.contains("Line1"));
        assertTrue(result.contains("Line2"));
    }

    @AfterAll
    static void cleanup() {
        // No cleanup needed since container is set to auto-remove in DockerContainerManager
        // You could add logic to stop container here if needed
    }
}
