package dk.kvalitetsit.itukt.auth.gateway.userextraction;

import dk.gov.oio.saml.session.AssertionWrapper;
import dk.gov.oio.saml.session.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public class SAMLAssertionUserIDExtractor implements UserIDExtractor {
    private final Logger logger = LoggerFactory.getLogger(SAMLAssertionUserIDExtractor.class);
    private final SessionHandler sessionHandler;
    private final HttpSession httpSession;
    private final String userIdAttribute;

    public SAMLAssertionUserIDExtractor(SessionHandler sessionHandler, HttpSession httpSession, String userIdAttribute) {
        this.sessionHandler = sessionHandler;
        this.httpSession = httpSession;
        this.userIdAttribute = userIdAttribute;
    }

    @Override
    public String extractUserID() {
        return Optional.ofNullable(getAssertion().getAttributeValues().get(userIdAttribute))
                .orElseThrow(() -> createForbiddenException("Missing attribute in SAML assertion: " + userIdAttribute));
    }

    private AssertionWrapper getAssertion() {
        return Optional.ofNullable(sessionHandler.getAssertion(httpSession))
                .orElseThrow(() -> createForbiddenException("No SAML assertion found in session"));
    }

    private ResponseStatusException createForbiddenException(String errorMessage) {
        logger.warn("User denied access. " + errorMessage);
        return new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
    }
}
