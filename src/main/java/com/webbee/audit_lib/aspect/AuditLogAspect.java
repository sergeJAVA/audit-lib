package com.webbee.audit_lib.aspect;

import com.webbee.audit_lib.annotation.AuditLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.boot.logging.LogLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

@Aspect
public class AuditLogAspect {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuditLogAspect.class);
    private static final ThreadLocal<String> ID = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Before("@annotation(auditLog)")
    public void logBefore(JoinPoint joinPoint, AuditLog auditLog) {
        String id = ID.get();
        String methodName = joinPoint.getSignature().getName();
        Level logLevel = toSlf4jLevel(auditLog.logLevel());
        String time = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String args = Arrays.toString(joinPoint.getArgs());

        LOGGER.atLevel(logLevel).log("{} {} START {} {} args = {}",
                time,
                logLevel.toString(),
                id,
                methodName,
                args
        );
    }

    @AfterReturning(pointcut = "@annotation(auditLog)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, AuditLog auditLog, Object result) {
        String id = ID.get();
        String methodName = joinPoint.getSignature().getName();
        Level logLevel = toSlf4jLevel(auditLog.logLevel());
        String time = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        LOGGER.atLevel(logLevel).log("{} {} END {} {} result = {}",
                time,
                logLevel.toString(),
                id,
                methodName,
                result
        );
    }

    @AfterThrowing(pointcut = "@annotation(auditLog)", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, AuditLog auditLog, Throwable exception) {
        String id = ID.get();
        String methodName = joinPoint.getSignature().getName();
        Level logLevel = toSlf4jLevel(auditLog.logLevel());
        String time = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        LOGGER.atLevel(logLevel).log("{} {} ERROR {} {} error = {}",
                time,
                logLevel.toString(),
                id,
                methodName,
                exception.getMessage(),
                exception
        );
    }

    private Level toSlf4jLevel(LogLevel springLevel) {
        return Level.valueOf(springLevel.name());
    }

}
