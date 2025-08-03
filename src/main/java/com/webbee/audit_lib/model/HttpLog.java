package com.webbee.audit_lib.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpLog {

    private LocalDateTime timestamp;
    private String type;
    private String method;
    private int status;
    private String path;
    private Map<String, String> queryParams;
    private String requestBody;
    private String responseBody;

    public HttpLog() {

    }

    public HttpLog(LocalDateTime timestamp,
                   String type, String method,
                   int status, String path,
                   Map<String, String> queryParams,
                   String requestBody,
                   String responseBody) {
        this.timestamp = timestamp;
        this.type = type;
        this.method = method;
        this.status = status;
        this.path = path;
        this.queryParams = queryParams;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

}
