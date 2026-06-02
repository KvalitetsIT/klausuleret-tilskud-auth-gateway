package dk.kvalitetsit.itukt.auth.gateway.userextraction;

import dk.gov.oio.saml.session.AssertionWrapper;
import dk.gov.oio.saml.session.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public class SAMLAssertionDataExtractor implements UserDataExtractor {
    private static final String NAME_ATTRIBUTE = "https://data.gov.dk/model/core/eid/fullName";
    private static final String EMAIL_ATTRIBUTE = "https://data.gov.dk/model/core/eid/email";

    private final Logger logger = LoggerFactory.getLogger(SAMLAssertionDataExtractor.class);
    private final SessionHandler sessionHandler;
    private final HttpSession httpSession;
    private final String userIdAttribute;
    private final String userRoleAttribute;

    public SAMLAssertionDataExtractor(SessionHandler sessionHandler, HttpSession httpSession, String userIdAttribute, String userRoleAttribute) {
        this.sessionHandler = sessionHandler;
        this.httpSession = httpSession;
        this.userIdAttribute = userIdAttribute;
        this.userRoleAttribute = userRoleAttribute;
    }

    @Override
    public String extractUserID() {
        return Optional.ofNullable(getAssertion().getAttributeValues().get(userIdAttribute))
                .orElseThrow(() -> createForbiddenException("Missing attribute in SAML assertion: " + userIdAttribute));
    }

    @Override
    public String extractUserRole() {
        String roleClaim = Optional.ofNullable(getAssertion().getAttributeValues().get(userRoleAttribute))
                .orElseThrow(() -> createForbiddenException("Missing attribute in SAML assertion: " + userRoleAttribute));
        var roleNameIndex = roleClaim.indexOf("_");
        return roleNameIndex == -1 ? roleClaim : roleClaim.substring(0, roleNameIndex);
    }

    @Override
    public String extractUserName() {
        return Optional.ofNullable(getAssertion().getAttributeValues().get(NAME_ATTRIBUTE))
                .orElse("");
    }

    @Override
    public String extractUserEmail() {
        return Optional.ofNullable(getAssertion().getAttributeValues().get(EMAIL_ATTRIBUTE))
                .orElse("");
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
