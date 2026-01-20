package dk.kvalitetsit.itukt.auth.integrationtest.gateway;

import dk.kvalitetsit.itukt.auth.integrationtest.BaseTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayIT extends BaseTest {

    private static final String GATEWAY_PATH = "/api";
    private HttpClient client;

    @BeforeAll
    void setUp() {
        client = HttpClient.newHttpClient();
    }

    @Test
    void gateway_WithGetRequest_ForwardsRequestToMockApi() throws IOException, InterruptedException {
        var mockApiExpectedPath = "/test?test=test";
        var request = HttpRequest.newBuilder()
                .uri(URI.create(getGatewayUrl() + GATEWAY_PATH + mockApiExpectedPath))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Success!", response.body());
    }

    @Test
    void gateway_WithPostRequest_ForwardsRequestToMockApi() throws IOException, InterruptedException {
        var mockApiExpectedPath = "/test";
        var mockApiExpectedBody = "test";
        var mockApiExpectedContentType = "text/plain";
        var request = HttpRequest.newBuilder()
                .uri(URI.create(getGatewayUrl() + GATEWAY_PATH + mockApiExpectedPath))
                .setHeader("Content-Type", mockApiExpectedContentType)
                .POST(HttpRequest.BodyPublishers.ofString(mockApiExpectedBody))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Success!", response.body());
    }

    private String getGatewayUrl() {
        return String.format("http://%s:%s", component.getHost(), component.getPort());
    }

    @Override
    protected boolean withOioSaml() {
        return false;
    }
}