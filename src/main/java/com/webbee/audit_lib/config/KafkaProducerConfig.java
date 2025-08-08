package com.webbee.audit_lib.config;

import com.webbee.audit_lib.util.ApplicationProperties;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean(name = "auditKafkaTemplate")
    @ConditionalOnMissingBean(name = "auditKafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean(value = "auditProducerFactory")
    @ConditionalOnMissingBean(name = "auditProducerFactory")
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperties.getKafkaAuditBootstrapServer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "audit-log-prod");
        return new DefaultKafkaProducerFactory<>(props);
    }

}
