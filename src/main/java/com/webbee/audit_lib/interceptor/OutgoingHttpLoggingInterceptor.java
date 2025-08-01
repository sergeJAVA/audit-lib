package com.webbee.audit_lib.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OutgoingHttpLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutgoingHttpLoggingInterceptor.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            ClientHttpResponse response = execution.execute(request, body);

            ClientHttpResponse bufferedResponse = new BufferingClientHttpResponseWrapper(response);

            String requestBody = new String(body, StandardCharsets.UTF_8);
            String responseBody = StreamUtils.copyToString(bufferedResponse.getBody(), StandardCharsets.UTF_8);

            String logMessage = String.format("%s Outgoing %s %d %s RequestBody = %s ResponseBody = %s",
                    LocalDateTime.now().format(DATE_TIME_FORMATTER),
                    request.getMethod(),
                    bufferedResponse.getStatusCode().value(),
                    request.getURI(),
                    requestBody.isEmpty() ? "{}" : requestBody,
                    responseBody.isEmpty() ? "{}" : responseBody
            );
            LOGGER.info(logMessage);

            return bufferedResponse;

        } catch (IOException e) {
            String requestBody = new String(body, StandardCharsets.UTF_8);
            LOGGER.error("{} Outgoing {} {} RequestBody = {} Error = {}",
                    LocalDateTime.now().format(DATE_TIME_FORMATTER),
                    request.getMethod(),
                    request.getURI(),
                    requestBody.isEmpty() ? "{}" : requestBody,
                    e.getMessage(),
                    e
            );
            throw e;
        }
    }

}

