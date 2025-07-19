package com.cavernservice.model;

import static org.junit.jupiter.api.Assertions.*;

import com.cavernservice.type.TypeTask;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void testRunBashTask() {
      //// Skip the test if not Linux
      //String os = System.getProperty("os.name").toLowerCase();
      //Assumptions.assumeTrue(os.contains("linux"), "Test runs only on Linux");


      Task task = new Task("Echo Test", "Runs echo command", "echo Hello, World!", TypeTask.BASH);
      task.run();

      assertNotNull(task.getTaskResult());
      assertEquals("Hello, World!", task.getTaskResult());
    }
}