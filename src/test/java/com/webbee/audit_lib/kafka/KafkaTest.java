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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("test")
public class KafkaTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestService testService;

    @Test
    void handleMessageTest() throws JsonProcessingException, ExecutionException, InterruptedException {
        List<String> list = new ArrayList<>();
        Message message = new Message("Privet Kafka");

        KafkaConsumer<String, String> kafkaConsumer = TestUtils.createConsumer("audit-log");

        ProducerRecord<String, String> producerRecord = TestUtils
                .createProducerRecord(objectMapper.writeValueAsString(message), "3", "audit-log");

        testService.sendToKafka(producerRecord);

        Awaitility.await()
                .pollDelay(Duration.ofMillis(300))
                .pollInterval(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(10))
                .until(() -> {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> consumerRecord : records) {
                        list.add(consumerRecord.value());
                        testService.handle(consumerRecord);
                    }
                    return !records.isEmpty();
                });

        System.out.println("ЛОГИ ИЗ КАФКИ: " + list);
        assertTrue(list.stream().anyMatch(msg -> msg.contains("Privet Kafka")));
    }

}
