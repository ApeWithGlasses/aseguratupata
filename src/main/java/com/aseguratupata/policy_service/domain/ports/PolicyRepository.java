package com.aseguratupata.policy_service.domain.ports;

import com.aseguratupata.policy_service.domain.model.Policy;
import reactor.core.publisher.Mono;

public interface PolicyRepository {
    Mono<Policy> save(Policy policy);
}
