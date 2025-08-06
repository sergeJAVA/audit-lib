package com.webbee.audit_lib.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Класс, который используют для отправки лога о методе в формате JSON в Kafka.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MethodLog {

    private String localDateTime;
    private String logLevel;
    private String correlationId;
    private String methodName;
    private String args;
    private String logType;
    private Object result;
    private String exceptionMessage;

    public MethodLog() {

    }

    /**
     * Метод для создания START лога.
     */
    public void createStartLog(String localDateTime,
                               String logLevel,
                               String logType,
                               String correlationId ,
                               String methodName,
                               String args) {
        this.localDateTime = localDateTime;
        this.logLevel = logLevel;
        this.correlationId = correlationId ;
        this.methodName = methodName;
        this.args = args;
        this.logType = logType;
    }

    /**
     * Метод для создания END лога.
     */
    public void createEndLog(String localDateTime,
                             String logLevel,
                             String logType,
                             String correlationId ,
                             String methodName,
                             Object result) {
        this.localDateTime = localDateTime;
        this.logLevel = logLevel;
        this.correlationId = correlationId ;
        this.methodName = methodName;
        this.logType = logType;
        this.result = result;
    }

    /**
     * Метод для создания ERROR лога.
     */
    public void createErrorLog(String localDateTime,
                             String logLevel,
                             String logType,
                             String correlationId ,
                             String methodName,
                             String exceptionMessage) {
        this.localDateTime = localDateTime;
        this.logLevel = logLevel;
        this.correlationId = correlationId ;
        this.methodName = methodName;
        this.logType = logType;
        this.exceptionMessage = exceptionMessage;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getArgs() {
        return args;
    }

    public String getLogType() {
        return logType;
    }

    public Object getResult() {
        return result;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

}
