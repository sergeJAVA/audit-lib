package com.webbee.audit_lib.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webbee.audit_lib.model.HttpLog;
import com.webbee.audit_lib.util.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpAuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpAuditService.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private ObjectMapper objectMapper;

    public void logOutgoingRequest(String method, URI uri, int status, String requestBody, String responseBody) {
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
            kafkaTemplate.send(applicationProperties.getKafkaTopic(),"2", objectMapper.writeValueAsString(kafkaLog));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing HttpLog to Kafka", e);
        }
    }

    private Map<String, String> getQueryParamsMap(String fullPath) {
        return fullPath.contains("?") ?
                Stream.of(fullPath.split("\\?")[1].split("&"))
                        .map(param -> param.split("="))
                        .collect(Collectors.toMap(p -> p[0], p -> p[1]))
                : null;
    }

}
