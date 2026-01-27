package dk.kvalitetsit.itukt.auth.integrationtest;

public sealed interface Component permits OutsideDockerComponent, InDockerComponent {
    void start();

    void stop();

    String getHost();

    Integer getPort();

    void withSpringProfile(String profile);

}