package br.dev.rodrigopinheiro.B3DataManager.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Infrastructure Configuration - JpaAuditingConfig
 * 
 * Configuração para habilitar auditoria automática do Spring Data JPA.
 * 
 * Funcionalidades:
 * - Preenchimento automático de @CreatedDate e @LastModifiedDate
 * - Integração com AuditableEntity
 * - Suporte a campos de auditoria em todas as entidades
 * 
 * Características:
 * - @EnableJpaAuditing: Ativa o sistema de auditoria
 * - Funciona em conjunto com @EntityListeners(AuditingEntityListener.class)
 * - Campos são preenchidos automaticamente na persistência/atualização
 * 
 * Uso:
 * - Entidades devem herdar de AuditableEntity
 * - Campos createdAt e updatedAt são gerenciados automaticamente
 * - Fallback manual via @PrePersist/@PreUpdate se necessário
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    
    // Configuração básica - Spring Data JPA gerencia automaticamente
    // os campos @CreatedDate e @LastModifiedDate
    
    // Caso seja necessário customizar o auditor (usuário que fez a alteração),
    // pode-se implementar AuditorAware<String> aqui no futuro
}