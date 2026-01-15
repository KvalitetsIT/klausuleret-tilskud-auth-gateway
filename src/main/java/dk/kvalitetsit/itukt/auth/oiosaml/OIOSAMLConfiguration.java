package dk.kvalitetsit.itukt.auth.oiosaml;

import dk.gov.oio.saml.filter.AuthenticatedFilter;
import dk.gov.oio.saml.servlet.DispatcherServlet;
import dk.gov.oio.saml.session.SessionDestroyListener;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class OIOSAMLConfiguration {

    // Authenticates requests to /api/*
    @Bean
    public FilterRegistrationBean<AuthenticatedFilter> oioSamlFilter() {
        var reg = new FilterRegistrationBean<AuthenticatedFilter>();
        reg.setFilter(new AuthenticatedFilter());
        reg.addUrlPatterns("/api/*");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }

    // Handles SAML requests for login, logout, metadata, etc.
    @Bean
    public ServletRegistrationBean<DispatcherServlet> oioSamlServlet() {
        var reg = new ServletRegistrationBean<>(new DispatcherServlet(), "/saml/*");
        reg.setName("oioSamlServlet");
        reg.setLoadOnStartup(1);
        reg.addInitParameter("oiosaml.servlet.entityid", "http://saml.bst.dev/nadm");
        // TODO: virker ikke med localhost da SEB kræver url https://dev.nadm.dk:8080 for entityid http://saml.bst.dev/nadm
        reg.addInitParameter("oiosaml.servlet.baseurl", "http://localhost:8080");
        reg.addInitParameter("oiosaml.servlet.keystore.location", "keystore/NADM_Test_new.p12");
        reg.addInitParameter("oiosaml.servlet.keystore.password", "Test1234");
        reg.addInitParameter("oiosaml.servlet.keystore.alias", "nadm test - 11/4/2026");
        reg.addInitParameter("oiosaml.servlet.idp.entityid", "https://t-seb.dkseb.dk/runtime/");
        reg.addInitParameter("oiosaml.servlet.idp.metadata.file", "metadata/IdP/seb.xml");

        return reg;
    }

    // So that sessions destroyed by the server are also removed from the OIOSAML Session handler
    @Bean
    public ServletListenerRegistrationBean<SessionDestroyListener> oioSamlSessionDestroyListener() {
        return new ServletListenerRegistrationBean<>(new SessionDestroyListener());
    }
}
