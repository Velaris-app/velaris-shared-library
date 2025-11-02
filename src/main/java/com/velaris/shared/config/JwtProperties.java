package com.velaris.shared.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    @NotNull private String secret;
    @NotNull private long accessExpirationMs;
    @NotNull private long refreshExpirationMs;
}
