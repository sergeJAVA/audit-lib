package com.webbee.audit_lib.util;

import org.springframework.beans.factory.annotation.Value;

public class ApplicationProperties {

    @Value("${audit.kafka.enabled:false}")
    private boolean kafkaEnabled;

    @Value("${audit.kafka.topic:audit-log}")
    private String kafkaTopic;

    public ApplicationProperties() {

    }

    public boolean isKafkaEnabled() {
        return kafkaEnabled;
    }

    public void setKafkaEnabled(boolean kafkaEnabled) {
        this.kafkaEnabled = kafkaEnabled;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

}
