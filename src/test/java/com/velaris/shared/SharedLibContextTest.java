package com.velaris.shared;

import com.velaris.shared.common.LibraryIntegrationTest;
import com.velaris.shared.config.SharedLibraryConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@LibraryIntegrationTest
class SharedLibContextTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
        assertThat(context.getBean(SharedLibraryConfig.class)).isNotNull();
        assertThat(context.getBean("securityFilterChain")).isNotNull();
    }
}