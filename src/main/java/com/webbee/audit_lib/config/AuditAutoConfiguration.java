package com.webbee.audit_lib.config;

import com.webbee.audit_lib.aspect.AuditLogAspect;
import com.webbee.audit_lib.filter.HttpLoggingFilter;
import com.webbee.audit_lib.interceptor.OutgoingHttpLoggingInterceptor;
import com.webbee.audit_lib.service.HttpAuditService;
import com.webbee.audit_lib.util.ApplicationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

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

    @Bean
    @ConditionalOnProperty(
            prefix = "audit.http",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public HttpLoggingFilter httpLoggingFilter() {
        return new HttpLoggingFilter();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "audit.http",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public OutgoingHttpLoggingInterceptor httpLoggingInterceptor() {
        return new OutgoingHttpLoggingInterceptor();
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "audit.http",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(5))
                .additionalInterceptors(httpLoggingInterceptor())
                .build();
    }

    @Bean
    public HttpAuditService httpAuditService() {
        return new HttpAuditService();
    }

    @Bean
    public ApplicationProperties applicationProperties() {
        return new ApplicationProperties();
    }

}
