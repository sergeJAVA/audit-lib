package com.webbee.audit_lib.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webbee.audit_lib.service.HttpAuditService;
import com.webbee.audit_lib.util.ApplicationProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Фильтр для логирования входящих HTTP-запросов
 */
@Order(1)
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(HttpLoggingFilter.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private HttpAuditService httpAuditService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CustomContentCachingRequestWrapper wrappedRequest = new CustomContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String requestBody = getRequestBody(wrappedRequest);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        String responseBody = getResponseBody(wrappedResponse);
        logRequest(wrappedRequest, wrappedResponse, requestBody, responseBody);

        wrappedResponse.copyBodyToResponse();
    }

    private void logRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
                            String requestBody, String responseBody) throws JsonProcessingException {
        String queryString = getQueryString(request);
        String url = request.getRequestURI() + (queryString != null && !queryString.isEmpty() ? "?" + queryString : "");
        String logMessage = String.format("%s Incoming %s %d %s RequestBody=%s ResponseBody=%s",
                LocalDateTime.now().format(DATE_TIME_FORMATTER),
                request.getMethod(),
                response.getStatus(),
                url,
                requestBody.isEmpty() ? "{}" : requestBody,
                responseBody.isEmpty() ? "{}" : responseBody);
        LOGGER.info(logMessage);

        if (applicationProperties.isKafkaEnabled()) {
            httpAuditService.logIncomingRequestToKafka(request.getMethod(),
                    url,
                    response.getStatus(),
                    requestBody.isEmpty() ? "{}" : requestBody,
                    responseBody.isEmpty() ? "{}" : responseBody
            );
        }
    }

    private String getRequestBody(CustomContentCachingRequestWrapper request) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            String body = reader.lines().collect(Collectors.joining("\n"));
            return body.isEmpty() ? "{}" : body;
        } catch (IOException e) {
            return "{}";
        }
    }

    private String getQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null && !request.getParameterMap().isEmpty()) {
            queryString = request.getParameterMap().entrySet()
                    .stream()
                    .flatMap(entry -> {
                        String key = entry.getKey();
                        String[] values = entry.getValue();
                        return Arrays.stream(values).map(value -> key + "=" + value);
                    })
                    .collect(Collectors.joining("&"));
        }
        return queryString;
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        try {
            byte[] content = response.getContentAsByteArray();
            return content.length > 0 ? new String(content, StandardCharsets.UTF_8) : "{}";
        } catch (Exception e) {
            LOGGER.error("Failed to read response body", e);
            return "{}";
        }
    }

    private Map<String, String> getQueryParamsMap(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return null;
        }
        return Arrays.stream(queryString.split("&"))
                .map(param -> param.split("=", 2))
                .collect(Collectors.toMap(p -> p[0], p -> p.length > 1 ? p[1] : ""));
    }

}

