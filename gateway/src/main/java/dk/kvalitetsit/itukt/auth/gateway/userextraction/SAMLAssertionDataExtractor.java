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
    private static final String ROLE_ATTRIBUTE = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role";

    private final Logger logger = LoggerFactory.getLogger(SAMLAssertionDataExtractor.class);
    private final SessionHandler sessionHandler;
    private final HttpSession httpSession;

    public SAMLAssertionDataExtractor(SessionHandler sessionHandler, HttpSession httpSession) {
        this.sessionHandler = sessionHandler;
        this.httpSession = httpSession;
    }

    @Override
    public UserData extractUserData() {
        return new UserData(extractAtribute(NAME_ATTRIBUTE), extractAtribute(EMAIL_ATTRIBUTE), extractRole());
    }

    private String extractRole() {
        String roleClaim = extractAtribute(ROLE_ATTRIBUTE);
        var roleNameIndex = roleClaim.indexOf("_");
        return roleNameIndex == -1 ? roleClaim : roleClaim.substring(0, roleNameIndex);
    }

    private String extractAtribute(String attributeName) {
        return Optional.ofNullable(getAssertion().getAttributeValues().get(attributeName))
                .orElseThrow(() -> createForbiddenException("Missing attribute in SAML assertion: " + attributeName));
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
