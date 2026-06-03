package dk.kvalitetsit.itukt.auth.gateway;

import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserData;
import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserDataExtractor;
import org.openapitools.api.GatewayApi;
import org.openapitools.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;

@RestController
public class GatewayController implements GatewayApi {
    private final Logger logger = LoggerFactory.getLogger(GatewayController.class);
    private final URL apiUrl;
    private final String loginRedirectUrl;
    private final UserDataExtractor userDataExtractor;
    private final String requiredUserRoleFromSeb;

    public GatewayController(GatewayConfiguration configuration, UserDataExtractor userDataExtractor) {
        this.apiUrl = configuration.api().url();
        this.loginRedirectUrl = configuration.loginRedirectUrl().toString();
        this.requiredUserRoleFromSeb = configuration.requiredUserRoleFromSeb();
        this.userDataExtractor = userDataExtractor;
    }

    @Override
    public ResponseEntity<Void> login() {
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", loginRedirectUrl).build();
    }

    @Override
    public ResponseEntity<User> getUser() {
        UserData userData = userDataExtractor.extractUserData();
        var user = new User(userData.name(), userData.email());
        return ResponseEntity.ok(user);
    }

    @RequestMapping(GatewayConstants.API_PATH + "/**")
    public ResponseEntity<?> proxy(ProxyExchange<byte[]> proxy, HttpServletRequest request) {
        var userData = userDataExtractor.extractUserData();
        if(!userData.role().equals(requiredUserRoleFromSeb))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have the required SEB role claim '" + requiredUserRoleFromSeb + "'");

        String apiUri = constructApiUrl(proxy, request);
        var api = proxy
                .uri(apiUri)
                .header("User-ID", userData.email())
                .header("Host", apiUrl.getHost());

        var method = getHttpMethod(request);

        logger.info("Forwarding {} request to: {}", method, apiUri);
        ResponseEntity<?> response = switch (method) {
            case GET, TRACE -> api.get();
            case HEAD -> api.head();
            case POST -> api.post();
            case PUT -> api.put();
            case PATCH -> api.patch();
            case DELETE -> api.delete();
            case OPTIONS -> api.options();
        };
        logger.debug("Received response with status: {}", response.getStatusCode().value());
        return ResponseEntity
                .status(response.getStatusCode())
                .header("Content-Type", response.getHeaders().getFirst("Content-Type"))
                .body(response.getBody());
    }

    private String constructApiUrl(ProxyExchange<byte[]> proxy, HttpServletRequest request) {
        String apiUri = apiUrl + proxy.path(GatewayConstants.API_PATH);
        return appendQueryParams(apiUri, request.getQueryString());
    }

    private static HttpMethod getHttpMethod(HttpServletRequest request) {
        var method = HttpMethod.resolve(request.getMethod().toUpperCase());
        return method == null ? HttpMethod.GET : method;
    }

    private static String appendQueryParams(String uri, String queryString) {
        return uri + (queryString != null ? "?" + queryString : "");
    }
}
