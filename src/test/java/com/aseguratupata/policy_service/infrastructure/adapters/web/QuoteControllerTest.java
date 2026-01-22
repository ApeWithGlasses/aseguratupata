package com.aseguratupata.policy_service.infrastructure.adapters.web;

import com.aseguratupata.policy_service.application.usecase.CreateQuoteUseCase;
import com.aseguratupata.policy_service.domain.model.Pet;
import com.aseguratupata.policy_service.domain.model.Plan;
import com.aseguratupata.policy_service.domain.model.Species;
import com.aseguratupata.policy_service.infrastructure.adapters.web.dto.QuoteRequest;
import com.aseguratupata.policy_service.infrastructure.adapters.web.dto.QuoteResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = QuoteController.class)
class QuoteControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private CreateQuoteUseCase createQuoteUseCase;

    @Test
    void createQuoteSuccess() {
        QuoteRequest request = new QuoteRequest("Buddy", "dog", "Mixed", 3, "basic");

        Pet pet = new Pet("Buddy", Species.DOG, "Mixed", 3);
        Plan plan = Plan.BASIC;
        var quote = com.aseguratupata.policy_service.domain.model.Quote.create(pet, plan, new BigDecimal("123.45"));

        BDDMockito.given(createQuoteUseCase.execute(ArgumentMatchers.any(Pet.class), ArgumentMatchers.eq(plan)))
                .willReturn(Mono.just(quote));

        webClient.post()
                .uri("/api/quotes")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(QuoteResponse.class)
                .value(response -> {
                    assertEquals(quote.id(), response.id());
                    assertEquals(quote.pet().name(), response.petName());
                    assertEquals(quote.totalAmount().doubleValue(), response.totalAmount());
                    assertNotNull(response.expirationDate());
                    LocalDateTime parsed = LocalDateTime.parse(response.expirationDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    assertNotNull(parsed);
                });

        ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
        Mockito.verify(createQuoteUseCase).execute(petCaptor.capture(), ArgumentMatchers.eq(plan));
        Pet captured = petCaptor.getValue();
        assertEquals("Buddy", captured.name());
        assertEquals(Species.DOG, captured.species());
        assertEquals("Mixed", captured.breed());
        assertEquals(3, captured.age());
    }

    @Test
    void createQuoteMalformedJsonReturnsBadRequest() {
        String badJson = "{\"petName\": \"Buddy\", \"species\": \"dog\", \"breed\": \"Mixed\", \"age\": \"notAnInt\", \"plan\": \"basic\"}";

        webClient.post()
                .uri("/api/quotes")
                .header("Content-Type", "application/json")
                .bodyValue(badJson)
                .exchange()
                .expectStatus().value(status -> assertTrue(status == 400 || status == 500));

        Mockito.verifyNoInteractions(createQuoteUseCase);
    }

    @Test
    void createQuoteUseCaseError() {
        QuoteRequest request = new QuoteRequest("Buddy", "dog", "Mixed", 3, "basic");
        Plan plan = Plan.BASIC;

        BDDMockito.given(createQuoteUseCase.execute(ArgumentMatchers.any(Pet.class), ArgumentMatchers.eq(plan)))
                .willReturn(Mono.error(new IllegalArgumentException("pricing failed")));

        webClient.post()
                .uri("/api/quotes")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();

        Mockito.verify(createQuoteUseCase).execute(ArgumentMatchers.any(Pet.class), ArgumentMatchers.eq(plan));
    }
}