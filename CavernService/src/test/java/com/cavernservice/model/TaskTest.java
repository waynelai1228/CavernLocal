package com.cavernservice.model;

import static org.junit.jupiter.api.Assertions.*;

import com.cavernservice.type.TypeTask;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void testRunBashTask() {
      // Skip the test if not Linux
      String os = System.getProperty("os.name").toLowerCase();
      Assumptions.assumeTrue(os.contains("linux"), "Test runs only on Linux");


      Task task = new Task("Echo Test", "Runs echo command", "echo Hello, World!", TypeTask.BASH);
      task.run();

      assertNotNull(task.getTaskResult());
      assertEquals("Hello, World!", task.getTaskResult());
    }

    @Test
    void testRunBashTaskThrowsOnUnsupportedOS() {
        String os = System.getProperty("os.name").toLowerCase();

        if (!os.contains("linux")) {
            Task task = new Task("Invalid OS Test", "Should throw", "echo Test", TypeTask.BASH);
            assertThrows(UnsupportedOperationException.class, task::run);
        }
    }
}