package com.webbee.audit_lib.config;

import com.webbee.audit_lib.aspect.AuditLogAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "audit.console",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public AuditLogAspect auditLogAspect() {
        return new AuditLogAspect();
    }

}
