package com.example.payment.Asych;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaRetryConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        // 1. Configure Recoverer: Routes failed records to "<original-topic>.DLT"
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
            (cr, e) -> new TopicPartition(cr.topic() + ".DLT", cr.partition())
        );
        
        // 2. Retry policy: Attempt up to 2 retries (3 total attempts) with 1-second interval
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2));

        // 3. Non-retryable exceptions: Send directly to DLT without retrying
        handler.addNotRetryableExceptions(
            NullPointerException.class, 
            IllegalArgumentException.class,
            DeserializationException.class, // Prevents retry loops on broken JSON payloads
            ClassCastException.class
        );

        return handler;
    }
}