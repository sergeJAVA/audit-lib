package com.webbee.audit_lib.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webbee.audit_lib.util.Message;
import com.webbee.audit_lib.util.TestApp;
import com.webbee.audit_lib.util.TestService;
import com.webbee.audit_lib.util.TestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;

import static com.webbee.audit_lib.util.TestUtils.createConsumer;

@SpringBootTest(classes = TestApp.class)
public class KafkaTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestService testService;

    @Test
    void handleMessageTest() throws JsonProcessingException {
        Message message = new Message("Privet Kafka");
        KafkaConsumer<String, String> kafkaConsumer = createConsumer("audit-log");
        ProducerRecord<String, String> producerRecord = TestUtils
                .createProducerRecord(objectMapper.writeValueAsString(message), "1", "audit-log");
        testService.sendToKafka(producerRecord);
        Awaitility.await().pollDelay(Duration.ofMillis(500)).pollInterval(Duration.ofMillis(200)).atMost(Duration.ofSeconds(100)).until(() -> {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(10));
            for (ConsumerRecord<String, String> consumerRecord : records) {
                testService.handle(consumerRecord);
                Assertions.assertNotNull(consumerRecord);
            }
            return !records.isEmpty();
        });
    }



}
