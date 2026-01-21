package com.aseguratupata.policy_service.infrastructure.adapters.db.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Table("quotes")
public class QuoteEntity implements Persistable<String> {
    @Id
    private String id;
    private String petName;
    private String petSpecies;
    private String petBreed;
    private Integer petAge;
    private String selectedPlan;
    private BigDecimal totalAmount;
    private LocalDateTime expirationDate;

    @Transient
    @Builder.Default
    private boolean isNewRecord = false;

    @Override
    @Transient
    public boolean isNew() {
        return isNewRecord || id == null;
    }
}
