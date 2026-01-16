package dk.kvalitetsit.itukt.auth;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("dk.kvalitetsit.itukt")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
