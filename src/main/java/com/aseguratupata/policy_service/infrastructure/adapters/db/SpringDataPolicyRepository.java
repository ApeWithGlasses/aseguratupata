package com.aseguratupata.policy_service.infrastructure.adapters.db;

import com.aseguratupata.policy_service.infrastructure.adapters.db.entities.PolicyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataPolicyRepository extends ReactiveCrudRepository<PolicyEntity, String> {
}
