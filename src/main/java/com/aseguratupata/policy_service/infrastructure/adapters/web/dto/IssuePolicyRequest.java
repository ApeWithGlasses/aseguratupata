package com.aseguratupata.policy_service.infrastructure.adapters.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record IssuePolicyRequest(
        @NotBlank(message = "El ID de la cotización es obligatorio")
        String quoteId,

        @NotBlank(message = "El nombre del dueño es obligatorio")
        String ownerName,

        @NotBlank(message = "El documento del dueño es obligatorio")
        String ownerId,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String ownerEmail
) {
}
