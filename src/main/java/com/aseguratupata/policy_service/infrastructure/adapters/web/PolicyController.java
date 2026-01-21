package com.aseguratupata.policy_service.infrastructure.adapters.web;

import com.aseguratupata.policy_service.application.usecase.IssuePolicyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {
    private final IssuePolicyUseCase issuePolicyUseCase;

}
