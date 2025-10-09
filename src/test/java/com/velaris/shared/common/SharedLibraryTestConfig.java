package com.velaris.shared.common;

import com.velaris.shared.config.SharedLibraryConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication(scanBasePackageClasses = {
        SharedLibraryConfig.class
})
@ImportAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class SharedLibraryTestConfig {

    @Bean
    @Profile("test")
    public UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                User.withUsername("test")
                    .password("{noop}test")
                    .roles("USER")
                    .build()
        );
    }
}
