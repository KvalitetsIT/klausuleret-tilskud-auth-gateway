package dk.kvalitetsit.itukt.auth.oiosaml;

import dk.gov.oio.saml.filter.AuthenticatedFilter;
import dk.gov.oio.saml.servlet.DispatcherServlet;
import dk.gov.oio.saml.session.SessionDestroyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;

@Configuration
public class OIOSAMLBeanRegistration {
    private final Logger logger = LoggerFactory.getLogger(OIOSAMLBeanRegistration.class);
    private final OIOSAMLConfiguration.ServletConfiguration servletConf;

    public OIOSAMLBeanRegistration(OIOSAMLConfiguration configuration) {
        this.servletConf = configuration.servlet();
    }

    // Authenticates requests to /api/*
    @Bean
    @Profile("!without-oiosaml")
    public FilterRegistrationBean<AuthenticatedFilter> oioSamlFilter() {
        logger.info("Registering OIOSAML AuthenticatedFilter");

        var reg = new FilterRegistrationBean<AuthenticatedFilter>();
        reg.setFilter(new AuthenticatedFilter());
        reg.addUrlPatterns("/api/*");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }

    // Handles SAML requests for login, logout, metadata, etc.
    @Bean
    @Profile("!without-oiosaml")
    public ServletRegistrationBean<DispatcherServlet> oioSamlServlet() {
        logger.info("Registering OIOSAML DispatcherServlet");

        var reg = new ServletRegistrationBean<>(new DispatcherServlet(), "/saml/*");
        reg.setName("oioSamlServlet");
        reg.setLoadOnStartup(1);

        reg.addInitParameter("oiosaml.servlet.entityid", servletConf.entityId());
        reg.addInitParameter("oiosaml.servlet.baseurl", servletConf.baseurl());
        reg.addInitParameter("oiosaml.servlet.keystore.location", servletConf.keystore().location());
        reg.addInitParameter("oiosaml.servlet.keystore.password", servletConf.keystore().password());
        reg.addInitParameter("oiosaml.servlet.keystore.alias", servletConf.keystore().alias());
        reg.addInitParameter("oiosaml.servlet.idp.entityid", servletConf.idp().entityId());
        reg.addInitParameter("oiosaml.servlet.idp.metadata.file", servletConf.idp().metadataFile());

        return reg;
    }

    // So that sessions destroyed by the server are also removed from the OIOSAML Session handler
    @Bean
    @Profile("!without-oiosaml")
    public ServletListenerRegistrationBean<SessionDestroyListener> oioSamlSessionDestroyListener() {
        logger.info("Registering OIOSAML SessionDestroyListener");
        return new ServletListenerRegistrationBean<>(new SessionDestroyListener());
    }
}
