package com.aseguratupata.policy_service.infrastructure.config;

import com.aseguratupata.policy_service.application.usecase.CreateQuoteUseCase;
import com.aseguratupata.policy_service.application.usecase.IssuePolicyUseCase;
import com.aseguratupata.policy_service.domain.ports.EventPublisher;
import com.aseguratupata.policy_service.domain.ports.PolicyRepository;
import com.aseguratupata.policy_service.domain.ports.QuoteRepository;
import com.aseguratupata.policy_service.domain.service.PricingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PolicyConfig {
    @Bean
    public PricingService pricingService() {
        return new PricingService();
    }

    @Bean
    public CreateQuoteUseCase createQuoteUseCase(QuoteRepository quoteRepository, PricingService pricingService) {
        return new CreateQuoteUseCase(quoteRepository, pricingService);
    }

    @Bean
    public IssuePolicyUseCase issuePolicyUseCase(
            QuoteRepository quoteRepository,
            PolicyRepository policyRepository,
            EventPublisher eventPublisher
    ) {
        return new IssuePolicyUseCase(quoteRepository, policyRepository, eventPublisher);
    }
}
