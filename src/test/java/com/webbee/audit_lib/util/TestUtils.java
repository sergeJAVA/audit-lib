package com.webbee.audit_lib.util;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class TestUtils {

    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka"))
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
            .withReuse(true);

    static {
        kafkaContainer.start();
        createTopic("audit-log");
    }

    @DynamicPropertySource
    static void setProp(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    public static KafkaProducer<String, String> createProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", StringSerializer.class);
        properties.put("acks", "all");
        properties.put("client.id", "audit-log");
        return new KafkaProducer<>(properties);
    }

    public static <T> KafkaConsumer<String, T> createConsumer(String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());

        KafkaConsumer<String, T> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of(topic));
        return consumer;

    }

    public static ProducerRecord<String, String> createProducerRecord(String message, String key, String topic) {
        return new ProducerRecord<>(topic, key, message);
    }

    static void createTopic(String topicName) {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, TestUtils.kafkaContainer.getBootstrapServers());
        try (AdminClient admin = AdminClient.create(config)) {
            NewTopic topic = new NewTopic(topicName, 1, (short) 1);
            admin.createTopics(List.of(topic)).all().get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Kafka topic: " + topicName, e);
        }
    }

}
