package dk.kvalitetsit.itukt.auth.gateway;

import dk.kvalitetsit.itukt.auth.gateway.userextraction.SAMLAssertionUserIDExtractor;
import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserIDExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class GatewayBeanRegistration {
    private final Logger logger = LoggerFactory.getLogger(GatewayBeanRegistration.class);
    private final GatewayConfiguration configuration;

    public GatewayBeanRegistration(GatewayConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    @Profile("without-oiosaml")
    public UserIDExtractor mockedUserIDExtractor() {
        logger.info("Registering mocked UserIDExtractor");
        return () -> "mocked-user";
    }

    @Bean
    @Profile("!without-oiosaml")
    public UserIDExtractor samlAssertionUserIDExtractor() {
        logger.info("Registering SAMLAssertionUserIDExtractor");
        return new SAMLAssertionUserIDExtractor();
    }

    @Bean
    public CorsFilter corsFilter() {
        var corsConfig = new CorsConfiguration();
        configuration.allowedOrigins().forEach(corsConfig::addAllowedOrigin);
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsFilter(source);
    }
}
