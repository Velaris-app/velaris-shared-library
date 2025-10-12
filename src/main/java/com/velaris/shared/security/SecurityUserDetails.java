package com.velaris.shared.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class SecurityUserDetails extends User {
    private final Long id;
}