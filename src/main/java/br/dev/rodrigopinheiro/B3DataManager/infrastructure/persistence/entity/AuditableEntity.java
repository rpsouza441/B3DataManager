package br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Infrastructure Entity - AuditableEntity
 * 
 * Classe base abstrata que fornece campos de auditoria padronizados
 * para todas as entidades do sistema.
 * 
 * Características:
 * - Campos de auditoria automáticos (createdAt, updatedAt)
 * - Utiliza Spring Data JPA Auditing
 * - Configuração centralizada para todas as entidades
 * - Suporte a @PrePersist e @PreUpdate automáticos
 * 
 * Uso:
 * - Todas as entidades devem herdar desta classe
 * - Ativar @EnableJpaAuditing na configuração
 * - Campos são preenchidos automaticamente pelo Spring
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    /**
     * Data e hora de criação do registro
     * Preenchido automaticamente na primeira persistência
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora da última atualização do registro
     * Atualizado automaticamente a cada modificação
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Método executado antes da persistência inicial
     * Garante que createdAt seja definido mesmo sem Spring Auditing
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Método executado antes de cada atualização
     * Garante que updatedAt seja atualizado mesmo sem Spring Auditing
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica se a entidade é nova (ainda não foi persistida)
     * 
     * @return true se a entidade é nova, false caso contrário
     */
    public boolean isNew() {
        return createdAt == null;
    }

    /**
     * Verifica se a entidade foi modificada recentemente
     * 
     * @return true se updatedAt é diferente de createdAt
     */
    public boolean wasModified() {
        return updatedAt != null && createdAt != null && !updatedAt.equals(createdAt);
    }
}