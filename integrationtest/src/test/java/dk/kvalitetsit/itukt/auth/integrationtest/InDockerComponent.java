package dk.kvalitetsit.itukt.auth.integrationtest;

import org.slf4j.Logger;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

import static dk.kvalitetsit.itukt.auth.integrationtest.BaseTest.getComposeFile;

final class InDockerComponent implements Component {
    private static final String SERVICE_NAME = "gateway";
    private final ComposeContainer component;

    public InDockerComponent(Logger logger) {
        this.component = new ComposeContainer(getComposeFile("docker-compose.yaml"))
                .withServices(SERVICE_NAME)
                .withExposedService(SERVICE_NAME, 8080, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)))
                .withLogConsumer(SERVICE_NAME, new Slf4jLogConsumer(logger).withPrefix(SERVICE_NAME));
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
        return component.getServiceHost(SERVICE_NAME, 8080);
    }

    @Override
    public Integer getPort() {
        return component.getServicePort(SERVICE_NAME, 8080);
    }
}