package com.aseguratupata.policy_service.infrastructure.adapters.events;

import com.aseguratupata.policy_service.domain.model.events.PolicyIssuedEvent;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleEventPublisherTest {

    @Test
    void publishCompletesAndLogs() {
        ConsoleEventPublisher publisher = new ConsoleEventPublisher();

        PolicyIssuedEvent event = new PolicyIssuedEvent("policy-1", "owner@example.com", new BigDecimal("123.45"), LocalDateTime.now());

        Logger logger = (Logger) LoggerFactory.getLogger(ConsoleEventPublisher.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // execute
        publisher.publish(event).block();

        logger.detachAppender(listAppender);

        assertFalse(listAppender.list.isEmpty());
        String logged = listAppender.list.get(0).getFormattedMessage();
        assertTrue(logged.contains("EVENTO PUBLICADO A FACTURACIÓN"));
        assertTrue(logged.contains("policy-1"));
        assertTrue(logged.contains("owner@example.com"));
        assertTrue(logged.contains("123.45"));
    }
}

