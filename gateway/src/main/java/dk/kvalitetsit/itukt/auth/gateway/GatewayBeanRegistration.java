package dk.kvalitetsit.itukt.auth.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class GatewayBeanRegistration {
    private List<String> allowedOrigins = List.of("*");

    @Bean
    public CorsFilter corsFilter() {
        var corsConfig = new CorsConfiguration();
//        corsConfig.setAllowCredentials(true);
        allowedOrigins.forEach(corsConfig::addAllowedOrigin);
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsFilter(source);
    }
}
