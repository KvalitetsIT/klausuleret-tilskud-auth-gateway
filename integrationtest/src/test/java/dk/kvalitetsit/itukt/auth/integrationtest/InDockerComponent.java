package dk.kvalitetsit.itukt.auth.integrationtest;

import org.slf4j.Logger;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

import static dk.kvalitetsit.itukt.auth.integrationtest.BaseTest.getComposeFile;

final class InDockerComponent implements Component {
    private final ComposeContainer component;
    private final String serviceName;
    private final int port;

    public InDockerComponent(Logger logger, String serviceName, int port) {
        this.port = port;
        this.component = new ComposeContainer(getComposeFile("docker-compose.yaml"))
                .withExposedService(serviceName, port, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer(serviceName, new Slf4jLogConsumer(logger).withPrefix(serviceName));
        this.serviceName = serviceName;
    }

    @Override
    public void start() {
        component.start();
    }

    @Override
    public void stop() {
        component.stop();
    }

    @Override
    public String getHost() {
        return component.getServiceHost(serviceName, port);
    }

    @Override
    public Integer getPort() {
        return component.getServicePort(serviceName, port);
    }

    @Override
    public void withSpringProfile(String profile) {
        component.withEnv("SPRING_PROFILES_ACTIVE", profile);
    }
}