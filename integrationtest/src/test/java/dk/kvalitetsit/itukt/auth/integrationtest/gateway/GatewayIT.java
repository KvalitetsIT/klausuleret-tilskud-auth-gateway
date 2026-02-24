package dk.kvalitetsit.itukt.auth.integrationtest.gateway;

import dk.kvalitetsit.itukt.auth.gateway.GatewayConstants;
import dk.kvalitetsit.itukt.auth.integrationtest.BaseTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GatewayIT extends BaseTest {

    private HttpClient client;

    @BeforeAll
    void setUp() {
        client = HttpClient.newHttpClient();
    }

    @Test
    void gateway_WithGetRequest_ForwardsRequestToMockApi() throws IOException, InterruptedException {
        var mockApiExpectedPath = "/test?test=test";
        var request = HttpRequest.newBuilder()
                .uri(URI.create(getGatewayUrl() + GatewayConstants.API_PATH + mockApiExpectedPath))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Success!", response.body(), "Response should match body from mock API");
    }

    @Test
    void gateway_WithPostRequest_ForwardsRequestToMockApi() throws IOException, InterruptedException {
        var mockApiExpectedPath = "/test";
        var mockApiExpectedBody = "test";
        var mockApiExpectedContentType = "text/plain";
        var request = HttpRequest.newBuilder()
                .uri(URI.create(getGatewayUrl() + GatewayConstants.API_PATH + mockApiExpectedPath))
                .setHeader("Content-Type", mockApiExpectedContentType)
                .POST(HttpRequest.BodyPublishers.ofString(mockApiExpectedBody))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Success!", response.body(), "Response should match body from mock API");
    }

    @Test
    void login_RedirectsToLoginUrl() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(getGatewayUrl() + GatewayConstants.LOGIN_PATH))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.FOUND.value(), response.statusCode());
        Optional<String> locationHeader = response.headers().firstValue("Location");
        assertTrue(locationHeader.isPresent());
        assertEquals("http://localhost:4200", locationHeader.get(), "Location header should match login redirect url");
    }

    @Test
    void authCheck_Returns200() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(getGatewayUrl() + GatewayConstants.GATEWAY_PATH + "/auth-check"))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.OK.value(), response.statusCode());
    }

    private String getGatewayUrl() {
        return String.format("http://%s:%s", component.getHost(), component.getPort());
    }

    @Override
    protected boolean withOioSaml() {
        return false;
    }
}