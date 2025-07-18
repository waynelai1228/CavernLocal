package com.cavernservice.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.time.Duration;

public class DockerContainerManager {
    private static DockerClient dockerClient;
    private static String containerId;

    static {
        // Initialize DockerClientConfig using a builder
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("unix:///var/run/docker.sock")  // Unix socket is default for local Docker
            .withDockerTlsVerify(false)  // Set to true if you need TLS verification
            .build();

        // Create ApacheDockerHttpClient with custom config
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())  // Optional: Configure SSL if needed
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build();

        // Instantiate the Docker client using the config and HTTP client
        dockerClient = DockerClientImpl.getInstance(config, httpClient);
        
        // Initialize the container after client setup
        initializeContainer();
    }

    private static void initializeContainer() {
        try {
            // Check if image already exists locally
            boolean imageExists = dockerClient.listImagesCmd()
                .exec()
                .stream()
                .anyMatch(image -> {
                    String[] tags = image.getRepoTags();
                    if (tags == null) return false;
                    for (String tag : tags) {
                        if (tag.equals("ubuntu:latest")) {
                            return true;
                        }
                    }
                    return false;
                });

            if (!imageExists) {
                dockerClient.pullImageCmd("ubuntu")
                    .withTag("latest")
                    .start()
                    .awaitCompletion();
            }
        
            // Expose and bind ports correctly
            ExposedPort exposedPort = ExposedPort.tcp(80);  // Expose port 80 inside the container

            // Creating the container with updated configuration
            CreateContainerResponse container = dockerClient.createContainerCmd("ubuntu")
                .withHostConfig(HostConfig.newHostConfig()
                    .withNetworkMode("host"))
                .withName("task-runner")
                .withTty(true) // Keep bash open
                .withCmd("bash", "-c", "while true; do sleep 60; done")
                .withExposedPorts(exposedPort)
                .exec();

            containerId = container.getId();
            
            // Start the container after creation
            dockerClient.startContainerCmd(containerId).exec();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getContainerId() {
        return containerId;
    }

    public static DockerClient getClient() {
        return dockerClient;
    }
}
