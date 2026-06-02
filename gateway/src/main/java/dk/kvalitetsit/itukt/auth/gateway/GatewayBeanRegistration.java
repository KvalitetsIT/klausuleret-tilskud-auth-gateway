package dk.kvalitetsit.itukt.auth.gateway;

import dk.gov.oio.saml.service.OIOSAML3Service;
import dk.gov.oio.saml.util.InternalException;
import dk.kvalitetsit.itukt.auth.gateway.userextraction.SAMLAssertionDataExtractor;
import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserData;
import dk.kvalitetsit.itukt.auth.gateway.userextraction.UserDataExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpSession;

@Configuration
public class GatewayBeanRegistration {
    private final Logger logger = LoggerFactory.getLogger(GatewayBeanRegistration.class);
    private final GatewayConfiguration configuration;

    public GatewayBeanRegistration(GatewayConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    @Profile("without-oiosaml")
    public UserDataExtractor mockedUserIDExtractor() {
        logger.info("Registering mocked UserIDExtractor");
        return () -> new UserData("Test", "test@test.dk", "testRole");
    }

    @Bean
    @Profile("!without-oiosaml")
    @RequestScope
    public UserDataExtractor samlAssertionDataExtractor(HttpSession httpSession) throws InternalException {
        logger.info("Registering SAMLAssertionDataExtractor");
        var sessionHandler = OIOSAML3Service.getSessionHandlerFactory().getHandler();
        return new SAMLAssertionDataExtractor(sessionHandler, httpSession);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        var corsConfig = new CorsConfiguration();
        configuration.allowedOrigins().forEach(corsConfig::addAllowedOrigin);
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(GatewayConstants.GATEWAY_PATH + "/**", corsConfig);

        var corsFilter = new FilterRegistrationBean<>(new CorsFilter(source));
        corsFilter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsFilter;
    }
}
