package dk.kvalitetsit.itukt.auth.integrationtest;

import dk.kvalitetsit.itukt.auth.Application;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Properties;

final class OutsideDockerComponent implements Component {
    private final InDockerComponent apiMock;
    private ConfigurableApplicationContext app;

    OutsideDockerComponent(Logger logger) {
        apiMock = new InDockerComponent(logger, "api-mock", 1080);
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("itukt.gateway.api.url", String.format("http://%s:%s", apiMock.getHost(), apiMock.getPort()));
        properties.setProperty("itukt.gateway.oiosaml.servlet.entityid", "test");
        properties.setProperty("itukt.gateway.oiosaml.servlet.baseurl", "test");
        properties.setProperty("itukt.gateway.oiosaml.servlet.keystore.location", "keystore.p12");
        properties.setProperty("itukt.gateway.oiosaml.servlet.keystore.password", "Test1234");
        properties.setProperty("itukt.gateway.oiosaml.servlet.keystore.alias", "java.referenceimplementering");
        properties.setProperty("itukt.gateway.oiosaml.servlet.idp.entityid", "test");
        properties.setProperty("itukt.gateway.oiosaml.servlet.idp.metadatafile", "seb.xml");
        return properties;
    }

    @Override
    public void start() {
        apiMock.start();
        System.getProperties().putAll(getProperties());
        app = SpringApplication.run(Application.class);
    }

    @Override
    public void stop() {
        if (app != null) {
            app.close();
        }
        apiMock.stop();
    }

    @Override
    public String getHost() {
        return "localhost";
    }

    @Override
    public Integer getPort() {
        return 8080;
    }

    @Override
    public void withSpringProfile(String profile) {
        System.setProperty("spring.profiles.active", profile);
    }
}

