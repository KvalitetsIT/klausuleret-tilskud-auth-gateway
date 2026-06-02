package dk.kvalitetsit.itukt.auth.gateway.userextraction;

import dk.gov.oio.saml.session.AssertionWrapper;
import dk.gov.oio.saml.session.SessionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SAMLAssertionDataExtractorTest {
    private static final String NAME_ATTRIBUTE = "https://data.gov.dk/model/core/eid/fullName";
    private static final String EMAIL_ATTRIBUTE = "https://data.gov.dk/model/core/eid/email";
    private static final String ROLE_ATTRIBUTE = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role";

    @Mock
    private SessionHandler sessionHandler;

    @Mock
    private HttpSession httpSession;

    @Test
    void extractUserData_WithNoAssertion_ThrowsForbiddenException() {
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(null);

        var e = assertThrows(ResponseStatusException.class, samlAssertionUserIDExtractor::extractUserData);
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void extractUserData_WithoutNameAttributeOnAssertion_ThrowsForbiddenException() {
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession);
        var assertion = Mockito.mock(AssertionWrapper.class);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(assertion);
        Mockito.when(assertion.getAttributeValues())
                .thenReturn(Map.of(
                        EMAIL_ATTRIBUTE, "email",
                        ROLE_ATTRIBUTE, "role"));

        var e = assertThrows(ResponseStatusException.class, samlAssertionUserIDExtractor::extractUserData);
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void extractUserData_WithoutEmailAttributeOnAssertion_ThrowsForbiddenException() {
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession);
        var assertion = Mockito.mock(AssertionWrapper.class);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(assertion);
        Mockito.when(assertion.getAttributeValues())
                .thenReturn(Map.of(
                        NAME_ATTRIBUTE, "name",
                        ROLE_ATTRIBUTE, "role"));

        var e = assertThrows(ResponseStatusException.class, samlAssertionUserIDExtractor::extractUserData);
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void extractUserData_WithoutRoleAttributeOnAssertion_ThrowsForbiddenException() {
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession);
        var assertion = Mockito.mock(AssertionWrapper.class);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(assertion);
        Mockito.when(assertion.getAttributeValues())
                .thenReturn(Map.of(
                        NAME_ATTRIBUTE, "name",
                        ROLE_ATTRIBUTE, "role"));

        var e = assertThrows(ResponseStatusException.class, samlAssertionUserIDExtractor::extractUserData);
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void extractUserID_WithAllRequiredAttributesOnAssertion_ReturnsUserData() {
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession);
        var assertion = Mockito.mock(AssertionWrapper.class);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(assertion);
        var userData = new UserData("name", "email", "readonly");
        Mockito.when(assertion.getAttributeValues())
                .thenReturn(Map.of(
                        NAME_ATTRIBUTE, userData.name(),
                        EMAIL_ATTRIBUTE, userData.email(),
                        ROLE_ATTRIBUTE, userData.role() + "_0_3"));

        var result = samlAssertionUserIDExtractor.extractUserData();

        assertEquals(userData, result);
    }

    @Test
    void extractUserData_WithoutRoleSuffix_ReturnsUserData() {
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession);
        var assertion = Mockito.mock(AssertionWrapper.class);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(assertion);
        var userData = new UserData("jens", "jens@test.dk", "admin");
        Mockito.when(assertion.getAttributeValues())
                .thenReturn(Map.of(
                        NAME_ATTRIBUTE, userData.name(),
                        EMAIL_ATTRIBUTE, userData.email(),
                        ROLE_ATTRIBUTE, userData.role()));

        var result = samlAssertionUserIDExtractor.extractUserData();

        assertEquals(userData, result);
    }
}