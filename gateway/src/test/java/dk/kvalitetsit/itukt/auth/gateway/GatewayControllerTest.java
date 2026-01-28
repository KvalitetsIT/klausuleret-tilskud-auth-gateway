package dk.kvalitetsit.itukt.auth.gateway;

import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserIDExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GatewayControllerTest {
    private static final URL API_URL = createURL("http://test.dk");
    private static final String PATH = "/test";
    @Mock
    private UserIDExtractor userIDExtractor;
    @Mock
    private ProxyExchange<byte[]> proxyExchange;
    @Mock
    private HttpServletRequest httpRequest;
    private GatewayController gatewayController;

    @BeforeEach
    void setUp() {
        Mockito.when(proxyExchange.path("/api")).thenReturn(PATH);
        Mockito.when(proxyExchange.uri(Mockito.anyString())).thenReturn(proxyExchange);
        Mockito.when(proxyExchange.header(Mockito.any(), Mockito.any())).thenReturn(proxyExchange);
        var gatewayConf = new GatewayConfiguration(new GatewayConfiguration.ApiConfiguration(API_URL), List.of());
        gatewayController = new GatewayController(gatewayConf, userIDExtractor);
    }

    @Test
    void proxy_ForwardsToApiWithUriAndHeader() {
        Mockito.when(httpRequest.getMethod()).thenReturn("GET");
        String userId = "test-user";
        Mockito.when(userIDExtractor.extractUserID()).thenReturn(userId);
        var expectedResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(proxyExchange.get()).thenReturn(expectedResponse);

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(expectedResponse, response);
        Mockito.verify(proxyExchange).uri(API_URL + PATH);
        Mockito.verify(proxyExchange).header("User-ID", userId);
        Mockito.verify(proxyExchange).header("Host", API_URL.getHost());
    }

    @Test
    void proxy_WithQueryParameters_ForwardsQueryParametersToApi() {
        Mockito.when(proxyExchange.path("/api")).thenReturn(PATH);
        Mockito.when(httpRequest.getMethod()).thenReturn("GET");
        String queryparams = "queryparams";
        Mockito.when(httpRequest.getQueryString()).thenReturn(queryparams);

        gatewayController.proxy(proxyExchange, httpRequest);
        Mockito.verify(proxyExchange).uri(API_URL + PATH + "?" + queryparams);
    }

    @Test
    void proxy_WithTraceRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("TRACE");
        var expectedResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(proxyExchange.get()).thenReturn(expectedResponse);

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(expectedResponse, response);
    }

    @Test
    void proxy_WithHeadRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("HEAD");
        var expectedResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(proxyExchange.head()).thenReturn(expectedResponse);

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(expectedResponse, response);
    }

    @Test
    void proxy_WithPostRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("POST");
        var expectedResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(proxyExchange.post()).thenReturn(expectedResponse);

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(expectedResponse, response);
    }

    @Test
    void proxy_WithPutRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("PUT");
        var expectedResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(proxyExchange.put()).thenReturn(expectedResponse);

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(expectedResponse, response);
    }

    @Test
    void proxy_WithPatchRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("PATCH");
        var expectedResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(proxyExchange.patch()).thenReturn(expectedResponse);

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(expectedResponse, response);
    }

    @Test
    void proxy_WithDeleteRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("DELETE");
        var expectedResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(proxyExchange.delete()).thenReturn(expectedResponse);

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(expectedResponse, response);
    }

    @Test
    void proxy_WithOptionsRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("OPTIONS");
        var expectedResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(proxyExchange.options()).thenReturn(expectedResponse);

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(expectedResponse, response);
    }

    @Test
    void proxy_WithLowercaseDeleteRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("delete");
        var expectedResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(proxyExchange.delete()).thenReturn(expectedResponse);

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(expectedResponse, response);
    }

    @Test
    void proxy_WithUnknownRequestMethod_ForwardsToApiWithGetRequest() {
        Mockito.when(httpRequest.getMethod()).thenReturn("unknown");
        var expectedResponse = Mockito.mock(ResponseEntity.class);
        Mockito.when(proxyExchange.get()).thenReturn(expectedResponse);

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(expectedResponse, response);
    }

    private static URL createURL(String url) {
        try {
            return new URI(url).toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}