package com.webbee.audit_lib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public void sendInTransaction(String topic, String key, String value) {
        kafkaTemplate.send(topic, key, value);
    }

}
