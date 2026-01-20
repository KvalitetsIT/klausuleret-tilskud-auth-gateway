package dk.kvalitetsit.itukt.auth.integrationtest;

import dk.kvalitetsit.itukt.auth.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Properties;

final class OutsideDockerComponent implements Component {
    private ConfigurableApplicationContext app;

    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("itukt.gateway.api.url", "test");
        properties.setProperty("itukt.gateway.oiosaml.servlet.entityid", "test");
        properties.setProperty("itukt.gateway.oiosaml.servlet.baseurl", "test");
        properties.setProperty("itukt.gateway.oiosaml.servlet.keystore.location", "NADM_Test_new.p12");
        properties.setProperty("itukt.gateway.oiosaml.servlet.keystore.password", "Test1234");
        properties.setProperty("itukt.gateway.oiosaml.servlet.keystore.alias", "nadm test - 11/4/2026");
        properties.setProperty("itukt.gateway.oiosaml.servlet.idp.entityid", "test");
        properties.setProperty("itukt.gateway.oiosaml.servlet.idp.metadatafile", "seb.xml");
        return properties;
    }

    @Override
    public void start() {
        System.getProperties().putAll(getProperties());
        app = SpringApplication.run(Application.class);
    }

    @Override
    public void stop() {
        if (app != null) {
            app.close();
        }
    }

    @Override
    public String getHost() {
        return "localhost";
    }

    @Override
    public Integer getPort() {
        return 8080;
    }
}

