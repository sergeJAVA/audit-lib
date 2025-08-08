package com.webbee.audit_lib.util;

import com.webbee.audit_lib.config.KafkaProducerConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = KafkaProducerConfig.class)
public class TestApp {

}
