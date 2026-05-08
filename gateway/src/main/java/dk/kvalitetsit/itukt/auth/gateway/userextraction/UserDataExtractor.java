package dk.kvalitetsit.itukt.auth.gateway.userextraction;

public interface UserDataExtractor {
    String extractUserID();
    String extractUserRole();
}
