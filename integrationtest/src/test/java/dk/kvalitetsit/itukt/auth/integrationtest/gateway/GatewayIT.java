package dk.kvalitetsit.itukt.auth.integrationtest.gateway;

import dk.kvalitetsit.itukt.auth.gateway.GatewayBeanRegistration;
import dk.kvalitetsit.itukt.auth.gateway.GatewayConstants;
import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserDataExtractor;
import dk.kvalitetsit.itukt.auth.integrationtest.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.GatewayApi;
import org.openapitools.client.model.User;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GatewayIT extends BaseTest {

    private HttpClient httpClient;
    private GatewayApi gatewayApi;

    @BeforeAll
    void setUp() {
        httpClient = HttpClient.newHttpClient();
        gatewayApi = new GatewayApi(client);
    }

    @Test
    void gateway_WithGetRequest_ForwardsRequestToMockApi() throws IOException, InterruptedException {
        var mockApiExpectedPath = "/test?test=test";
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + GatewayConstants.API_PATH + mockApiExpectedPath))
                .GET()
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Success!", response.body(), "Response should match body from mock API");
    }

    @Test
    void gateway_WithPostRequest_ForwardsRequestToMockApi() throws IOException, InterruptedException {
        var mockApiExpectedPath = "/test";
        var mockApiExpectedBody = "test";
        var mockApiExpectedContentType = "text/plain";
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + GatewayConstants.API_PATH + mockApiExpectedPath))
                .setHeader("Content-Type", mockApiExpectedContentType)
                .POST(HttpRequest.BodyPublishers.ofString(mockApiExpectedBody))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Success!", response.body(), "Response should match body from mock API");
    }

    @Test
    void login_RedirectsToLoginUrl() {
        ApiException e = assertThrows(ApiException.class, () -> gatewayApi.login());

        assertEquals(HttpStatus.FOUND.value(), e.getCode());
        List<String> locationHeaders = e.getResponseHeaders().get("Location");
        assertNotNull(locationHeaders);
        assertEquals(1, locationHeaders.size());
        assertEquals("http://localhost:4200", locationHeaders.getFirst(), "Location header should match login redirect url");
    }

    @Test
    void getUser_ReturnsMockedAuthUser() throws ApiException {
        User user = gatewayApi.getUser();

        var userData = new GatewayBeanRegistration(null).mockedUserIDExtractor().extractUserData();
        User expectedUser = new User()
                .name(userData.name())
                .email(userData.email());
        assertEquals(expectedUser, user);
    }

    @Override
    protected boolean withOioSaml() {
        return false;
    }
}