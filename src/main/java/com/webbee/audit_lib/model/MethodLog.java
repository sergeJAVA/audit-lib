package com.webbee.audit_lib.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MethodLog {

    private String localDateTime;
    private String logLevel;
    private String id;
    private String methodName;
    private String args;
    private String logType;
    private Object result;
    private String exceptionMessage;

    public MethodLog() {

    }

    public void createStartLog(String localDateTime,
                               String logLevel,
                               String logType,
                               String id,
                               String methodName,
                               String args) {
        this.localDateTime = localDateTime;
        this.logLevel = logLevel;
        this.id = id;
        this.methodName = methodName;
        this.args = args;
        this.logType = logType;
    }

    public void createEndLog(String localDateTime,
                             String logLevel,
                             String logType,
                             String id,
                             String methodName,
                             Object result) {
        this.localDateTime = localDateTime;
        this.logLevel = logLevel;
        this.id = id;
        this.methodName = methodName;
        this.logType = logType;
        this.result = result;
    }

    public void createErrorLog(String localDateTime,
                             String logLevel,
                             String logType,
                             String id,
                             String methodName,
                             String exceptionMessage) {
        this.localDateTime = localDateTime;
        this.logLevel = logLevel;
        this.id = id;
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

    public String getId() {
        return id;
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
