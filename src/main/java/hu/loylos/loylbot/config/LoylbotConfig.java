package hu.loylos.loylbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "loylbot")
@Data
public class LoylbotConfig {
    private String clientId;
    private String clientSecret;
}
