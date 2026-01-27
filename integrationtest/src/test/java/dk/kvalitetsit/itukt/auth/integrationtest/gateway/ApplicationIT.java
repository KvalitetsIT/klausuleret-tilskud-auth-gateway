package dk.kvalitetsit.itukt.auth.integrationtest.gateway;

import dk.kvalitetsit.itukt.auth.integrationtest.BaseTest;
import org.junit.jupiter.api.Test;

public class ApplicationIT extends BaseTest {

    @Test
    void testApplicationStartup() {
        // Starts the application with oiosaml
    }

    @Override
    protected boolean withOioSaml() {
        return true;
    }
}