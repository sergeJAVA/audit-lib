package com.webbee.audit_lib.interceptor;

import com.webbee.audit_lib.service.HttpAuditService;
import com.webbee.audit_lib.util.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Перехватчик исходящих RestTemplate запросов.
 */
public class OutgoingHttpLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutgoingHttpLoggingInterceptor.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private HttpAuditService httpAuditService;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            ClientHttpResponse response = execution.execute(request, body);

            ClientHttpResponse bufferedResponse = new BufferingClientHttpResponseWrapper(response);

            String requestBody = new String(body, StandardCharsets.UTF_8);
            String responseBody = StreamUtils.copyToString(bufferedResponse.getBody(), StandardCharsets.UTF_8);

            httpAuditService.logOutgoingRequestToConsole(LocalDateTime.now(),
                    request.getMethod(),
                    bufferedResponse.getStatusCode().value(),
                    request.getURI(),
                    requestBody,
                    responseBody);

            if (applicationProperties.isKafkaEnabled()) {
                httpAuditService.logOutgoingRequestToKafka(
                        request.getMethod().toString(),
                        request.getURI(),
                        bufferedResponse.getStatusCode().value(),
                        requestBody,
                        responseBody
                );

            }

            return bufferedResponse;

        } catch (IOException e) {
            String requestBody = new String(body, StandardCharsets.UTF_8);
            httpAuditService.logOutgoingRequestToConsoleError
            (
                    LocalDateTime.now(),
                    request.getMethod(),
                    request.getURI(),
                    requestBody,
                    e
            );
            throw e;
        }
    }

}

