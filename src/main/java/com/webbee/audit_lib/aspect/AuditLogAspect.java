package com.webbee.audit_lib.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webbee.audit_lib.annotation.AuditLog;
import com.webbee.audit_lib.model.MethodLog;
import com.webbee.audit_lib.service.TransactionalProducer;
import com.webbee.audit_lib.util.ApplicationProperties;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

@Aspect
public class AuditLogAspect {

    private final static String METHOD_LOG_KEY = "1";
    private final static Logger LOGGER = LoggerFactory.getLogger(AuditLogAspect.class);
    private static final ThreadLocal<String> ID = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final TransactionalProducer transactionalProducer;
    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public AuditLogAspect(TransactionalProducer transactionalProducer) {
        this.transactionalProducer = transactionalProducer;
    }

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
        if (applicationProperties.isKafkaEnabled()) {
            MethodLog methodLog = new MethodLog();
            methodLog.createStartLog(time, logLevel.toString(), "START", id, methodName, args);
            try {
                transactionalProducer.sendInTransaction(
                        applicationProperties.getKafkaTopic(),
                        METHOD_LOG_KEY,
                        objectMapper.writeValueAsString(methodLog)
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
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
        if (applicationProperties.isKafkaEnabled()) {
            MethodLog methodLog = new MethodLog();
            methodLog.createEndLog(time, logLevel.toString(), "END", id, methodName, result);
            try {
                transactionalProducer.sendInTransaction(
                        applicationProperties.getKafkaTopic(),
                        METHOD_LOG_KEY,
                        objectMapper.writeValueAsString(methodLog)
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
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

        if (applicationProperties.isKafkaEnabled()) {
            MethodLog methodLog = new MethodLog();
            methodLog.createErrorLog(time, logLevel.toString(), "ERROR", id, methodName, exception.getMessage());
            try {
                transactionalProducer.sendInTransaction(
                        applicationProperties.getKafkaTopic(),
                        METHOD_LOG_KEY,
                        objectMapper.writeValueAsString(methodLog)
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Level toSlf4jLevel(LogLevel springLevel) {
        return Level.valueOf(springLevel.name());
    }

}
