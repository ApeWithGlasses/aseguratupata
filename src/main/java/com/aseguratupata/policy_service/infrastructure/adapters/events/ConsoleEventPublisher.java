package com.aseguratupata.policy_service.infrastructure.adapters.events;

import com.aseguratupata.policy_service.domain.model.events.PolicyIssuedEvent;
import com.aseguratupata.policy_service.domain.ports.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ConsoleEventPublisher implements EventPublisher {
    @Override
    public Mono<Void> publish(PolicyIssuedEvent event) {
        return Mono.fromRunnable(() -> log.info("EVENTO PUBLICADO A FACTURACIÓN: Póliza={} Email={} Monto={}",
                event.policyId(), event.ownerEmail(), event.amount())
        );
    }
}
