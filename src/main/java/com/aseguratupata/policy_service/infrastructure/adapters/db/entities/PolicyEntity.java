package com.aseguratupata.policy_service.infrastructure.adapters.db.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("policies")
public class PolicyEntity implements Persistable<String> {
    @Id
    private String id;
    private String quoteId;
    private String ownerName;
    private String ownerId;
    private String ownerEmail;
    private String status;
    private LocalDateTime issuedAt;

    @Transient
    @Builder.Default
    private boolean isNewRecord = false;

    @Override
    public boolean isNew() {
        return isNewRecord || id == null;
    }
}
