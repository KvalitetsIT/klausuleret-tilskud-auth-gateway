package dk.kvalitetsit.itukt.auth.gateway;

import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserDataExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GatewayControllerTest {
    private static final URL API_URL = createURL("http://test.dk");
    private static final URL LOGIN_REDIRECT_URL = createURL("http://login-test.dk");
    private static final String PATH = "/test";
    private static final String REQUIRED_USER_ROLE = "userRole";
    @Mock
    private UserDataExtractor userDataExtractor;
    @Mock
    private ProxyExchange<byte[]> proxyExchange;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private ResponseEntity<byte[]> mockedResponse;
    private GatewayController gatewayController;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(proxyExchange.path(GatewayConstants.API_PATH)).thenReturn(PATH);
        Mockito.lenient().when(proxyExchange.uri(Mockito.anyString())).thenReturn(proxyExchange);
        Mockito.lenient().when(proxyExchange.header(Mockito.any(), Mockito.any())).thenReturn(proxyExchange);
        String userRole = REQUIRED_USER_ROLE + "_0_3"; // All user roles from SEB are postfixed with _[0-9]_[0-9]
        Mockito.when(userDataExtractor.extractUserRole()).thenReturn(userRole);
        var gatewayConf = new GatewayConfiguration(new GatewayConfiguration.ApiConfiguration(API_URL), "", "", REQUIRED_USER_ROLE, LOGIN_REDIRECT_URL, List.of());
        gatewayController = new GatewayController(gatewayConf, userDataExtractor);
    }

    @Test
    void proxy_ForwardsToApiWithUriAndHeader() {
        Mockito.when(httpRequest.getMethod()).thenReturn("GET");
        String userId = "test-user";
        Mockito.when(userDataExtractor.extractUserID()).thenReturn(userId);
        Mockito.when(proxyExchange.get()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        var responseHeaders = new HttpHeaders();
        responseHeaders.put("Content-Type", List.of("test"));
        responseHeaders.put("Another-Header", List.of("jens"));
        Mockito.when(mockedResponse.getHeaders()).thenReturn(responseHeaders);
        Mockito.when(mockedResponse.getBody()).thenReturn(new byte[]{42});

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        var expectedResponseHeaders = new HttpHeaders();
        expectedResponseHeaders.put("Content-Type", List.of("test"));
        assertEquals(mockedResponse.getStatusCode(), response.getStatusCode());
        assertEquals(mockedResponse.getBody(), response.getBody());
        assertEquals(expectedResponseHeaders, response.getHeaders());
        Mockito.verify(proxyExchange).uri(API_URL + PATH);
        Mockito.verify(proxyExchange).header("User-ID", userId);
        Mockito.verify(proxyExchange).header("Host", API_URL.getHost());
    }

    @Test
    void proxy_InvalidUserRole() {
        String userRole = "invalidRole_0_3";
        Mockito.when(userDataExtractor.extractUserRole()).thenReturn(userRole);
        assertThrows(ResponseStatusException.class, () -> gatewayController.proxy(proxyExchange, httpRequest));
    }

    @Test
    void proxy_WithQueryParameters_ForwardsQueryParametersToApi() {
        Mockito.when(proxyExchange.path(GatewayConstants.API_PATH)).thenReturn(PATH);
        Mockito.when(httpRequest.getMethod()).thenReturn("GET");
        String queryparams = "queryparams";
        Mockito.when(httpRequest.getQueryString()).thenReturn(queryparams);
        Mockito.when(proxyExchange.get()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(mockedResponse.getHeaders()).thenReturn(Mockito.mock(HttpHeaders.class));

        gatewayController.proxy(proxyExchange, httpRequest);
        Mockito.verify(proxyExchange).uri(API_URL + PATH + "?" + queryparams);
        Mockito.verify(proxyExchange).get();
    }

    @Test
    void proxy_WithTraceRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("TRACE");
        Mockito.when(proxyExchange.get()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        Mockito.when(mockedResponse.getHeaders()).thenReturn(Mockito.mock(HttpHeaders.class));

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(mockedResponse.getStatusCode(), response.getStatusCode());
        Mockito.verify(proxyExchange).get();
    }

    @Test
    void proxy_WithHeadRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("HEAD");
        Mockito.when(proxyExchange.head()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        Mockito.when(mockedResponse.getHeaders()).thenReturn(Mockito.mock(HttpHeaders.class));

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(mockedResponse.getStatusCode(), response.getStatusCode());
        Mockito.verify(proxyExchange).head();
    }

    @Test
    void proxy_WithPostRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("POST");
        Mockito.when(proxyExchange.post()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.CREATED);
        Mockito.when(mockedResponse.getHeaders()).thenReturn(Mockito.mock(HttpHeaders.class));

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(mockedResponse.getStatusCode(), response.getStatusCode());
        Mockito.verify(proxyExchange).post();
    }

    @Test
    void proxy_WithPutRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("PUT");
        Mockito.when(proxyExchange.put()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.CONFLICT);
        Mockito.when(mockedResponse.getHeaders()).thenReturn(Mockito.mock(HttpHeaders.class));

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(mockedResponse.getStatusCode(), response.getStatusCode());
        Mockito.verify(proxyExchange).put();
    }

    @Test
    void proxy_WithPatchRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("PATCH");
        Mockito.when(proxyExchange.patch()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.I_AM_A_TEAPOT);
        Mockito.when(mockedResponse.getHeaders()).thenReturn(Mockito.mock(HttpHeaders.class));

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(mockedResponse.getStatusCode(), response.getStatusCode());
        Mockito.verify(proxyExchange).patch();
    }

    @Test
    void proxy_WithDeleteRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("DELETE");
        Mockito.when(proxyExchange.delete()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
        Mockito.when(mockedResponse.getHeaders()).thenReturn(Mockito.mock(HttpHeaders.class));

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(mockedResponse.getStatusCode(), response.getStatusCode());
        Mockito.verify(proxyExchange).delete();
    }

    @Test
    void proxy_WithOptionsRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("OPTIONS");
        Mockito.when(proxyExchange.options()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.I_AM_A_TEAPOT);
        Mockito.when(mockedResponse.getHeaders()).thenReturn(Mockito.mock(HttpHeaders.class));

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(mockedResponse.getStatusCode(), response.getStatusCode());
        Mockito.verify(proxyExchange).options();
    }

    @Test
    void proxy_WithLowercaseDeleteRequest_ForwardsToApi() {
        Mockito.when(httpRequest.getMethod()).thenReturn("delete");
        Mockito.when(proxyExchange.delete()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.I_AM_A_TEAPOT);
        Mockito.when(mockedResponse.getHeaders()).thenReturn(Mockito.mock(HttpHeaders.class));

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(mockedResponse.getStatusCode(), response.getStatusCode());
        Mockito.verify(proxyExchange).delete();
    }

    @Test
    void proxy_WithUnknownRequestMethod_ForwardsToApiWithGetRequest() {
        Mockito.when(httpRequest.getMethod()).thenReturn("unknown");
        Mockito.when(proxyExchange.get()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.I_AM_A_TEAPOT);
        Mockito.when(mockedResponse.getHeaders()).thenReturn(Mockito.mock(HttpHeaders.class));

        var response = gatewayController.proxy(proxyExchange, httpRequest);

        assertEquals(mockedResponse.getStatusCode(), response.getStatusCode());
        Mockito.verify(proxyExchange).get();
    }

    private static URL createURL(String url) {
        try {
            return new URI(url).toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}