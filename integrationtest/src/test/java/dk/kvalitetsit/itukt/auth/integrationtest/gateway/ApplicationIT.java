package dk.kvalitetsit.itukt.auth.integrationtest.gateway;

import dk.kvalitetsit.itukt.auth.integrationtest.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ApplicationIT extends BaseTest {

    @Test
    void testApplicationStartup() {
        assertDoesNotThrow(() -> component.start());
    }
}