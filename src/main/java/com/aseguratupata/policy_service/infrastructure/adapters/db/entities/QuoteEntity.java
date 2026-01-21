package com.aseguratupata.policy_service.infrastructure.adapters.db.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @PersistenceCreator
    public QuoteEntity(String id, String petName, String petSpecies, String petBreed,
                       Integer petAge, String selectedPlan, BigDecimal totalAmount,
                       LocalDateTime expirationDate) {
        this.id = id;
        this.petName = petName;
        this.petSpecies = petSpecies;
        this.petBreed = petBreed;
        this.petAge = petAge;
        this.selectedPlan = selectedPlan;
        this.totalAmount = totalAmount;
        this.expirationDate = expirationDate;
    }
}
