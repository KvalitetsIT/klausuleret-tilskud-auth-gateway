package dk.kvalitetsit.itukt.auth.gateway;

import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserData;
import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserDataExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.List;

import static dk.kvalitetsit.itukt.auth.gateway.GatewayControllerTest.createURL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GatewayControllerErrorTest {

    private static final URL API_URL = createURL("http://test.dk");
    private static final URL LOGIN_REDIRECT_URL = createURL("http://login-test.dk");
    private static final String REQUIRED_USER_ROLE = "userRole";

    @Mock
    private UserDataExtractor userDataExtractor;
    @Mock
    private ProxyExchange<byte[]> proxyExchange;
    @Mock
    private HttpServletRequest httpRequest;

    private GatewayController gatewayController;

    @BeforeEach
    void setUp() {
        var gatewayConf = new GatewayConfiguration(new GatewayConfiguration.ApiConfiguration(API_URL), REQUIRED_USER_ROLE, LOGIN_REDIRECT_URL, List.of());
        gatewayController = new GatewayController(gatewayConf, userDataExtractor);
    }

    @Test
    void proxy_InvalidUserRole() {
        String userRole = "invalidRole_0_3"; // All user roles from SEB are postfixed with _[0-9]_[0-9]
        Mockito.when(userDataExtractor.extractUserData()).thenReturn(new UserData("", "", userRole));
        assertThrows(ResponseStatusException.class, () -> gatewayController.proxy(proxyExchange, httpRequest));
    }
}