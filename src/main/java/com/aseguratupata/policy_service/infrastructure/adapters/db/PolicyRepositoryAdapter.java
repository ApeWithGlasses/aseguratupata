package com.aseguratupata.policy_service.infrastructure.adapters.db;

import com.aseguratupata.policy_service.domain.model.Policy;
import com.aseguratupata.policy_service.domain.ports.PolicyRepository;
import com.aseguratupata.policy_service.infrastructure.adapters.db.entities.PolicyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PolicyRepositoryAdapter implements PolicyRepository {
    private final SpringDataPolicyRepository repository;

    @Override
    public Mono<Policy> save(Policy policy) {
        PolicyEntity entity = PolicyEntity.builder()
                .id(policy.id())
                .quoteId(policy.quoteId())
                .ownerName(policy.owner().name())
                .ownerId(policy.owner().idNumber())
                .ownerEmail(policy.owner().email())
                .status(policy.status().name())
                .issuedAt(policy.issuedAt())
                .isNewRecord(true)
                .build();
        return repository.save(entity)
                .map(e -> policy);
    }
}
