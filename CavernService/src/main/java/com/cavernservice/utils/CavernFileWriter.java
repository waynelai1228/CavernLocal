package com.cavernservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.cavernservice.model.Task;

public class CavernFileWriter {

    private static final ObjectMapper mapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT); // Pretty print

    public static void writeTasksToFile(List<Task> tasks, String filePath) throws IOException {
        mapper.writeValue(new File(filePath), tasks);
    }
}