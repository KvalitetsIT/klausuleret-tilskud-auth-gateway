package dk.kvalitetsit.itukt.auth.integrationtest;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.*;
import org.openapitools.client.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    protected Component component;
    protected ApiClient client;
    protected String url;

    @BeforeAll
    void setupApp() {
        boolean runInDocker = Boolean.getBoolean("runInDocker");
        component = runInDocker ? new InDockerComponent(logger, "gateway", 8080) : new OutsideDockerComponent(logger);
        if (!withOioSaml()) {
            component.withSpringProfile("without-oiosaml");
        }

        logger.info("Starting component");
        component.start();

        // Configure API client
        var httpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .build();
        url = String.format("http://%s:%s", component.getHost(), component.getPort());
        client = new ApiClient(httpClient).setBasePath(url);
    }

    @AfterAll
    void shutdown() {
        if (component != null) {
            component.stop();
        }
    }

    public static File getComposeFile(String fileName) {
        var testWorkingDir = System.getProperty("user.dir");
        var projectRoot = Paths.get(testWorkingDir).toAbsolutePath().normalize().getParent().toFile();
        return new File(projectRoot, "compose/" + fileName);
    }

    protected abstract boolean withOioSaml();
}
