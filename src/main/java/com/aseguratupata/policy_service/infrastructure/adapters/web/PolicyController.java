package com.aseguratupata.policy_service.infrastructure.adapters.web;

import com.aseguratupata.policy_service.application.usecase.IssuePolicyUseCase;
import com.aseguratupata.policy_service.domain.model.Owner;
import com.aseguratupata.policy_service.infrastructure.adapters.web.dto.IssuePolicyRequest;
import com.aseguratupata.policy_service.infrastructure.adapters.web.dto.IssuePolicyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {
    private final IssuePolicyUseCase issuePolicyUseCase;

    @PostMapping
    public Mono<IssuePolicyResponse> issuePolicy(@RequestBody @Valid IssuePolicyRequest request) {
        return Mono.fromCallable(() -> new Owner(request.ownerName(), request.ownerId(), request.ownerEmail())
        ).flatMap(owner -> issuePolicyUseCase.execute(request.quoteId(), owner))
                .map(policy -> new IssuePolicyResponse(
                        policy.id(),
                        policy.status().name(),
                        policy.issuedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        "Póliza emitida exitosamente. Facturación iniciada."
                ));
    }
}
