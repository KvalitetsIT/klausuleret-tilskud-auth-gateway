package dk.kvalitetsit.itukt.auth.gateway;

import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserIDExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GatewayController {
    private final Logger logger = LoggerFactory.getLogger(GatewayController.class);
    private final GatewayConfiguration configuration;
    private final UserIDExtractor userIDExtractor;

    public GatewayController(GatewayConfiguration configuration, UserIDExtractor userIDExtractor) {
        this.configuration = configuration;
        this.userIDExtractor = userIDExtractor;
    }

    @RequestMapping("/api/**")
    public ResponseEntity<?> proxy(ProxyExchange<byte[]> proxy, HttpServletRequest request) {
        String apiUri = constructApiUrl(proxy, request);
        var api = proxy
                .uri(apiUri)
                .header("User-ID", userIDExtractor.extractUserID());

        var method = getHttpMethod(request);

        logger.info("Forwarding request to: {}", apiUri);
        return switch (method) {
            case GET, TRACE -> api.get();
            case HEAD -> api.head();
            case POST -> api.post();
            case PUT -> api.put();
            case PATCH -> api.patch();
            case DELETE -> api.delete();
            case OPTIONS -> api.options();
        };
    }

    private String constructApiUrl(ProxyExchange<byte[]> proxy, HttpServletRequest request) {
        String apiUri = configuration.api().url() + proxy.path("/api");
        apiUri = appendQueryParams(apiUri, request.getQueryString());
        return apiUri;
    }

    private static HttpMethod getHttpMethod(HttpServletRequest request) {
        var method = HttpMethod.resolve(request.getMethod().toUpperCase());
        method = method == null ? HttpMethod.GET : method;
        return method;
    }

    private static String appendQueryParams(String uri, String queryString) {
        return uri + (queryString != null ? "?" + queryString : "");
    }
}
