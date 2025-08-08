package com.webbee.audit_lib.util;

import com.webbee.audit_lib.annotation.AuditLog;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class TestService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @AuditLog
    public String method(String word, int num) {
        return word + " " + num;
    }

    @AuditLog
    public String testException(String text) {
        throw new RuntimeException("Test exception");
    }

    @AuditLog
    public void handle(ConsumerRecord<String, String> consumerRecord) {

    }

    @AuditLog
    public void sendToKafka(ProducerRecord<String, String> producerRecord) throws ExecutionException, InterruptedException {
        kafkaTemplate.send(producerRecord).get();
    }

}
