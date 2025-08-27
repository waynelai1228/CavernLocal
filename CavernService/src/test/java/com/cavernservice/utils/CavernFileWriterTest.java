package com.cavernservice.utils;

import com.cavernservice.model.Task;
import com.cavernservice.type.TypeTask;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CavernFileWriterTest {

    private static final String TEST_FILE_PATH = "test_tasks.json";

    @AfterEach
    void cleanUp() {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testWriteTasksToFile_serializesTasksToJson() throws IOException {
        // Arrange: create Task objects
        Task task1 = new Task("Build", "Builds the project", "make build", TypeTask.BASH);
        task1.setTaskResult("Build successful");

        Task task2 = new Task("Test", "Runs unit tests", "./gradlew test", TypeTask.BASH);
        task2.setTaskResult("All tests passed");

        List<Task> tasks = List.of(task1, task2);

        // Act: write to file
        CavernFileWriter.writeTasksToFile(tasks, TEST_FILE_PATH);

        // Assert: check file content
        File file = new File(TEST_FILE_PATH);
        assertTrue(file.exists(), "The file should be created");

        String content = Files.readString(file.toPath());
        assertTrue(content.contains("Builds the project"));
        assertTrue(content.contains("Runs unit tests"));
        assertTrue(content.contains("Build successful"));
        assertTrue(content.contains("All tests passed"));
        assertTrue(content.contains("task_name"));
        assertTrue(content.contains("task_result"));
    }

    @Test
    void testWriteTasksToFile_withEmptyList_createsEmptyJsonArray() throws IOException {
        // Arrange
        List<Task> tasks = List.of();

        // Act
        CavernFileWriter.writeTasksToFile(tasks, TEST_FILE_PATH);

        // Assert
        File file = new File(TEST_FILE_PATH);
        assertTrue(file.exists(), "The file should be created");

        String content = Files.readString(file.toPath()).trim();
        assertEquals("[ ]", content);
    }
}
