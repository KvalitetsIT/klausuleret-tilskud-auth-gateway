package dk.kvalitetsit.itukt.auth.oiosaml;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "itukt.gateway.oiosaml")
public record OIOSAMLConfiguration(@NotNull @Valid ServletConfiguration servlet) {

    public record ServletConfiguration(
            @NotNull String entityId,
            @NotNull String baseurl,
            @NotNull @Valid KeystoreConfiguration keystore,
            @NotNull @Valid IdPConfiguration idp
    ) {

        public record KeystoreConfiguration(
                @NotNull String location,
                @NotNull String password,
                @NotNull String alias
        ) {
        }

        public record IdPConfiguration(
                @NotNull String entityId,
                @NotNull String metadataFile
        ) {
        }
    }
}
