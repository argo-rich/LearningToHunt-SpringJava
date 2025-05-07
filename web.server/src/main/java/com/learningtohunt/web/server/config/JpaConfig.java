package com.learningtohunt.web.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Moved @Enable* annotations from Application to here to avoid unit test errors.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class JpaConfig {
}
