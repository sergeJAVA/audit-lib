package com.webbee.audit_lib.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webbee.audit_lib.model.HttpLog;
import com.webbee.audit_lib.util.ApplicationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class HttpAuditServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private TransactionalProducer transactionalProducer;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private HttpAuditService httpAuditService;

    private static final String KAFKA_TOPIC = "test-topic";
    private static final String REQUEST_BODY = "request body";
    private static final String RESPONSE_BODY = "response body";

    @BeforeEach
    void setUp() {
        when(applicationProperties.getKafkaRequestTopic()).thenReturn(KAFKA_TOPIC);
    }

    @Test
    void logOutgoingRequestToKafka_ShouldSendToKafka() throws URISyntaxException, JsonProcessingException {
        String method = "GET";
        URI uri = new URI("http://test.ru?param1=value1&param2=value2");
        int status = 200;

        when(objectMapper.writeValueAsString(any(HttpLog.class))).thenReturn("json-string");

        httpAuditService.logOutgoingRequestToKafka(method, uri, status, REQUEST_BODY, RESPONSE_BODY);

        verify(transactionalProducer, times(1)).sendInTransaction(KAFKA_TOPIC, "2", "json-string");
        verify(objectMapper, times(1)).writeValueAsString(any(HttpLog.class));
    }

    @Test
    void logOutgoingRequestToKafka_ShouldHandleUri_WithoutQueryParams() throws URISyntaxException, JsonProcessingException {
        String method = "POST";
        URI uri = new URI("http://test.com");
        int status = 201;

        when(objectMapper.writeValueAsString(any(HttpLog.class))).thenReturn("json-string");

        httpAuditService.logOutgoingRequestToKafka(method, uri, status, REQUEST_BODY, RESPONSE_BODY);

        verify(transactionalProducer, times(1)).sendInTransaction(KAFKA_TOPIC, "2", "json-string");
    }

    @Test
    void logOutgoingRequestToKafka_ShouldHandleJsonProcessingException() throws URISyntaxException, JsonProcessingException {
        String method = "PUT";
        URI uri = new URI("http://test.uk");
        int status = 400;

        when(objectMapper.writeValueAsString(any(HttpLog.class))).thenThrow(JsonProcessingException.class);

        httpAuditService.logOutgoingRequestToKafka(method, uri, status, REQUEST_BODY, RESPONSE_BODY);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }

}