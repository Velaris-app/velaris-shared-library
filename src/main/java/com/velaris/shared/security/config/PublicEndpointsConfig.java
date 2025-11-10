package com.velaris.shared.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class PublicEndpointsConfig {
    private List<String> publicEndpoints;
}
