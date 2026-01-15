package dk.kvalitetsit.itukt.auth.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ApiGatewayController {
    private final Logger logger = LoggerFactory.getLogger(ApiGatewayController.class);
    private String apiUrl = "http://localhost:8082";

    @RequestMapping("/api/**")
    public ResponseEntity<?> proxy(ProxyExchange<byte[]> proxy, HttpServletRequest request) {
        String apiUri = apiUrl + proxy.path("/api");
        apiUri = appendQueryParams(apiUri, request.getQueryString());
        logger.info("Forwarding request to: {}", apiUri);

        var method = getHttpMethod(request);

        ResponseEntity<?> response = switch (method) {
            case GET, TRACE -> proxy.uri(apiUri).get();
            case HEAD -> proxy.uri(apiUri).head();
            case POST -> proxy.uri(apiUri).post();
            case PUT -> proxy.uri(apiUri).put();
            case PATCH -> proxy.uri(apiUri).patch();
            case DELETE -> proxy.uri(apiUri).delete();
            case OPTIONS -> proxy.uri(apiUri).options();
        };
        return response;
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
