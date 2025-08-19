package com.webbee.audit_lib.util;

import org.springframework.beans.factory.annotation.Value;

public class ApplicationProperties {

    @Value("${audit.kafka.enabled:false}")
    private boolean kafkaEnabled;

    @Value("${audit.kafka.audit-method.topic:audit-methods}")
    private String kafkaMethodTopic;

    @Value("${audit.kafka.audit-request.topic:audit-requests}")
    private String kafkaRequestTopic;

    @Value("${audit.kafka.bootstrap.server:localhost:9092}")
    private String kafkaAuditBootstrapServer;

    public ApplicationProperties() {

    }

    public boolean isKafkaEnabled() {
        return kafkaEnabled;
    }

    public void setKafkaEnabled(boolean kafkaEnabled) {
        this.kafkaEnabled = kafkaEnabled;
    }

    public String getKafkaMethodTopic() {
        return kafkaMethodTopic;
    }

    public void setKafkaMethodTopic(String kafkaMethodTopic) {
        this.kafkaMethodTopic = kafkaMethodTopic;
    }

    public String getKafkaAuditBootstrapServer() {
        return kafkaAuditBootstrapServer;
    }

    public void setKafkaAuditBootstrapServer(String kafkaAuditBootstrapServer) {
        this.kafkaAuditBootstrapServer = kafkaAuditBootstrapServer;
    }

    public String getKafkaRequestTopic() {
        return kafkaRequestTopic;
    }

    public void setKafkaRequestTopic(String kafkaRequestTopic) {
        this.kafkaRequestTopic = kafkaRequestTopic;
    }

}
