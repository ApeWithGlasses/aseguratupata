package com.aseguratupata.policy_service.infrastructure.adapters.web;

import com.aseguratupata.policy_service.application.usecase.IssuePolicyUseCase;
import com.aseguratupata.policy_service.domain.model.Owner;
import com.aseguratupata.policy_service.domain.model.Policy;
import com.aseguratupata.policy_service.domain.model.PolicyStatus;
import com.aseguratupata.policy_service.infrastructure.adapters.web.dto.IssuePolicyRequest;
import com.aseguratupata.policy_service.infrastructure.adapters.web.dto.IssuePolicyResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = PolicyController.class)
class PolicyControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private IssuePolicyUseCase issuePolicyUseCase;

    @Test
    void issuePolicySuccess() {
        IssuePolicyRequest request = new IssuePolicyRequest("quote-123", "Carlos", "ID123", "carlos@example.com");

        Policy policy = new Policy("policy-1", "quote-123", new Owner("Carlos", "ID123", "carlos@example.com"), PolicyStatus.ACTIVE, LocalDateTime.now());

        BDDMockito.given(issuePolicyUseCase.execute(ArgumentMatchers.eq("quote-123"), ArgumentMatchers.any(Owner.class)))
                .willReturn(Mono.just(policy));

        webClient.post()
                .uri("/api/policies")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(IssuePolicyResponse.class)
                .value(response -> {
                    assertEquals(policy.id(), response.policyId());
                    assertEquals(policy.status().name(), response.status());
                    assertNotNull(response.issuedAt());
                    assertEquals("Póliza emitida exitosamente. Facturación iniciada.", response.message());

                    LocalDateTime parsed = LocalDateTime.parse(response.issuedAt(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    assertNotNull(parsed);
                });

        ArgumentCaptor<Owner> ownerCaptor = ArgumentCaptor.forClass(Owner.class);
        Mockito.verify(issuePolicyUseCase).execute(ArgumentMatchers.eq("quote-123"), ownerCaptor.capture());
        Owner captured = ownerCaptor.getValue();
        assertEquals("Carlos", captured.name());
        assertEquals("ID123", captured.idNumber());
        assertEquals("carlos@example.com", captured.email());
    }

    @Test
    void issuePolicyValidationError() {
        IssuePolicyRequest bad = new IssuePolicyRequest("", "", "", "not-an-email");

        webClient.post()
                .uri("/api/policies")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bad)
                .exchange()
                .expectStatus().isBadRequest();

        Mockito.verifyNoInteractions(issuePolicyUseCase);
    }

    @Test
    void issuePolicyUseCaseErrors() {
        IssuePolicyRequest request = new IssuePolicyRequest("quote-expired", "Ana", "ID456", "ana@example.com");

        BDDMockito.given(issuePolicyUseCase.execute(ArgumentMatchers.eq("quote-expired"), ArgumentMatchers.any(Owner.class)))
                .willReturn(Mono.error(new IllegalArgumentException("La cotización ha expirado")));

        webClient.post()
                .uri("/api/policies")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();

        Mockito.verify(issuePolicyUseCase).execute(ArgumentMatchers.eq("quote-expired"), ArgumentMatchers.any(Owner.class));
    }

    @Test
    void issuePolicyQuoteNotFound() {
        IssuePolicyRequest request = new IssuePolicyRequest("quote-404", "Ana", "ID456", "ana@example.com");

        BDDMockito.given(issuePolicyUseCase.execute(ArgumentMatchers.eq("quote-404"), ArgumentMatchers.any(Owner.class)))
                .willReturn(Mono.error(new IllegalArgumentException("La cotización no existe")));

        webClient.post()
                .uri("/api/policies")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();

        Mockito.verify(issuePolicyUseCase).execute(ArgumentMatchers.eq("quote-404"), ArgumentMatchers.any(Owner.class));
    }
}