package com.aseguratupata.policy_service.domain.ports;

import com.aseguratupata.policy_service.domain.model.events.PolicyIssuedEvent;
import reactor.core.publisher.Mono;

public interface EventPublisher {
    Mono<Void> publish(PolicyIssuedEvent event);
}
