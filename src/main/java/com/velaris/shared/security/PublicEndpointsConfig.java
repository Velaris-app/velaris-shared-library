package com.velaris.shared.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class PublicEndpointsConfig {

    private final List<String> publicEndpoints;

    public PublicEndpointsConfig(@Value("${security.public-endpoints}") String endpoints) {
        this.publicEndpoints = Arrays.stream(endpoints.split(","))
                                     .map(String::trim)
                                     .toList();
    }
}
