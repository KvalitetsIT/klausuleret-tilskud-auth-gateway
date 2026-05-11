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
    @Mock
    private SessionHandler sessionHandler;

    @Mock
    private HttpSession httpSession;

    @Test
    void extractUserID_WithNoAssertion_ThrowsForbiddenException() {
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession, "test-id", "test-role");
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(null);

        var e = assertThrows(ResponseStatusException.class, samlAssertionUserIDExtractor::extractUserID);
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void extractUserID_WithoutRequiredAttributeOnAssertion_ThrowsForbiddenException() {
        String userIdAttribute = "test-attribute";
        String userRoleAttribute = "role-test-attribute";
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession, userIdAttribute, userRoleAttribute);
        var assertion = Mockito.mock(AssertionWrapper.class);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(assertion);
        Mockito.when(assertion.getAttributeValues()).thenReturn(Map.of("another-attribute", "value"));

        var e = assertThrows(ResponseStatusException.class, samlAssertionUserIDExtractor::extractUserID);
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void extractUserID_WithRequiredAttributeOnAssertion_ReturnsAttributeValue() {
        String userIdAttribute = "test-attribute";
        String userRoleAttribute = "role-test-attribute";
        String userIdValue = "test-user";
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession, userIdAttribute, userRoleAttribute);
        var assertion = Mockito.mock(AssertionWrapper.class);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(assertion);
        Mockito.when(assertion.getAttributeValues()).thenReturn(Map.of(userIdAttribute, userIdValue, "another-attribute", "value"));

        var result = samlAssertionUserIDExtractor.extractUserID();

        assertEquals(userIdValue, result);
    }

    @Test
    void extractUserRole_WithoutRequiredAttributeOnAssertion_ThrowsForbiddenException() {
        String userIdAttribute = "test-attribute";
        String userRoleAttribute = "role-test-attribute";
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession, userIdAttribute, userRoleAttribute);
        var assertion = Mockito.mock(AssertionWrapper.class);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(assertion);
        Mockito.when(assertion.getAttributeValues()).thenReturn(Map.of("another-attribute", "value"));

        var e = assertThrows(ResponseStatusException.class, samlAssertionUserIDExtractor::extractUserRole);
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void extractUserRole_WithRequiredAttributeOnAssertion_ReturnsAttributeValue() {
        String userIdAttribute = "test-attribute";
        String userRoleAttribute = "role-test-attribute";
        String userRoleValue = "test-role";
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession, userIdAttribute, userRoleAttribute);
        var assertion = Mockito.mock(AssertionWrapper.class);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(assertion);
        Mockito.when(assertion.getAttributeValues()).thenReturn(Map.of(userRoleAttribute, userRoleValue + "_0_3", "another-attribute", "value"));

        var result = samlAssertionUserIDExtractor.extractUserRole();

        assertEquals(userRoleValue, result);
    }

    @Test
    void extractUserRole_WithRequiredAttributeOnAssertionWithoutRoleSuffix_ReturnsAttributeValue() {
        String userIdAttribute = "test-attribute";
        String userRoleAttribute = "role-test-attribute";
        String userRoleValue = "test-role";
        var samlAssertionUserIDExtractor = new SAMLAssertionDataExtractor(sessionHandler, httpSession, userIdAttribute, userRoleAttribute);
        var assertion = Mockito.mock(AssertionWrapper.class);
        Mockito.when(sessionHandler.getAssertion(httpSession)).thenReturn(assertion);
        Mockito.when(assertion.getAttributeValues()).thenReturn(Map.of(userRoleAttribute, userRoleValue, "another-attribute", "value"));

        var result = samlAssertionUserIDExtractor.extractUserRole();

        assertEquals(userRoleValue, result);
    }
}