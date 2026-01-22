package dk.kvalitetsit.itukt.auth.gateway;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "itukt.gateway")
public record GatewayConfiguration(
        @NotNull @Valid ApiConfiguration api,
        @NotNull List<String> allowedOrigins) {
    public record ApiConfiguration(@NotNull String url) {
    }
}
