package com.webbee.audit_lib.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webbee.audit_lib.model.HttpLog;
import com.webbee.audit_lib.util.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис для отправки HTTP-логов в Kafka.
 */
public class HttpAuditService {

    private static final String KAFKA_REQUEST_KEY = "2";
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpAuditService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final TransactionalProducer transactionalProducer;
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;

    public HttpAuditService(TransactionalProducer transactionalProducer, ApplicationProperties applicationProperties, ObjectMapper objectMapper) {
        this.transactionalProducer = transactionalProducer;
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Отправка залогированного Outgoing HTTP-запроса в Kafka.
     */
    public void logOutgoingRequestToKafka(String method, URI uri, int status, String requestBody, String responseBody) {
        try {
            HttpLog kafkaLog = new HttpLog();
            kafkaLog.setTimestamp(LocalDateTime.now());
            kafkaLog.setType("Outgoing");
            kafkaLog.setMethod(method);
            kafkaLog.setStatus(status);

            String path = uri.toString();
            kafkaLog.setPath(path);
            kafkaLog.setQueryParams(getQueryParamsMap(path));

            kafkaLog.setRequestBody(requestBody);
            kafkaLog.setResponseBody(responseBody);
            transactionalProducer.sendInTransaction(
                    applicationProperties.getKafkaRequestTopic(),
                    KAFKA_REQUEST_KEY,
                    objectMapper.writeValueAsString(kafkaLog)
            );
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing HttpLog to Kafka", e);
        }
    }

    /**
     * Отправка залогированного Incoming HTTP-запроса в Kafka.
     */
    public void logIncomingRequestToKafka(String method, String uri, int status, String requestBody, String responseBody) {
        try {
            HttpLog kafkaLog = new HttpLog();
            kafkaLog.setTimestamp(LocalDateTime.now());
            kafkaLog.setType("Incoming");
            kafkaLog.setMethod(method);
            kafkaLog.setStatus(status);

            String path = uri;
            kafkaLog.setPath(path);
            kafkaLog.setQueryParams(getQueryParamsMap(path));

            kafkaLog.setRequestBody(requestBody);
            kafkaLog.setResponseBody(responseBody);
            transactionalProducer.sendInTransaction(
                    applicationProperties.getKafkaRequestTopic(),
                    KAFKA_REQUEST_KEY,
                    objectMapper.writeValueAsString(kafkaLog)
            );
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing HttpLog to Kafka", e);
        }
    }

    /**
     * Метод для отправки логов об исходящих запросах в консоль.
     */
    public void logOutgoingRequestToConsole(LocalDateTime time,
                                            HttpMethod httpMethod,
                                            int status,
                                            URI uri,
                                            String requestBody,
                                            String responseBody) {
        String logMessage = String.format("%s Outgoing %s %d %s RequestBody = %s ResponseBody = %s",
                time.format(DATE_TIME_FORMATTER),
                httpMethod,
                status,
                uri,
                requestBody.isEmpty() ? "{}" : requestBody,
                responseBody.isEmpty() ? "{}" : responseBody
        );
        LOGGER.info(logMessage);
    }

    public void logOutgoingRequestToConsoleError(LocalDateTime time,
                                            HttpMethod httpMethod,
                                            URI uri,
                                            String requestBody,
                                            Exception exception) {
        LOGGER.error("{} Outgoing {} {} RequestBody = {} Error = {}",
                time.format(DATE_TIME_FORMATTER),
                httpMethod,
                uri,
                requestBody.isEmpty() ? "{}" : requestBody,
                exception.getMessage(),
                exception
        );
    }

    private Map<String, String> getQueryParamsMap(String fullPath) {
        return fullPath.contains("?") ?
                Stream.of(fullPath.split("\\?")[1].split("&"))
                        .map(param -> param.split("="))
                        .collect(Collectors.toMap(p -> p[0], p -> p[1]))
                : null;
    }

}
