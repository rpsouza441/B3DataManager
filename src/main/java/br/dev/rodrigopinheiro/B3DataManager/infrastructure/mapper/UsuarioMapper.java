package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Usuario;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Usuario (domain) e UsuarioEntity (infrastructure).
 * 
 * Segue princípios SOLID:
 * - Single Responsibility: Apenas mapeamento de campos básicos
 * - Open/Closed: Extensível sem modificação
 * - Dependency Inversion: Não depende de outros mappers
 * 
 * Coleções são resolvidas pela camada de serviço para evitar:
 * - Problemas de performance com lazy loading
 * - Dependências circulares
 * - Violação do princípio de responsabilidade única
 */
@Component
public class UsuarioMapper {

    /**
     * Converte UsuarioEntity para Usuario (domain).
     * 
     * @param entity A entidade de infraestrutura
     * @return O modelo de domínio correspondente
     */
    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        
        Usuario usuario = new Usuario();
        
        // Campos básicos
        usuario.setId(entity.getId());
        usuario.setUsername(entity.getUsername());
        usuario.setPassword(entity.getPassword());
        usuario.setEmail(entity.getEmail());
        usuario.setRoles(entity.getRoles());
        usuario.setStatusConta(entity.getStatusConta());
        
        // Coleções (portfolios, instituicoes) são mapeadas sob demanda pela camada de serviço
        // para evitar problemas de performance, lazy loading e dependências circulares
        
        return usuario;
    }

    /**
     * Converte Usuario (domain) para UsuarioEntity (infrastructure).
     * 
     * @param domain O modelo de domínio
     * @return A entidade de infraestrutura correspondente
     */
    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        
        UsuarioEntity entity = new UsuarioEntity();
        
        // Campos básicos
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setEmail(domain.getEmail());
        entity.setRoles(domain.getRoles());
        entity.setStatusConta(domain.getStatusConta());
        
        // Note: Coleções (portfolios, instituicoes) devem ser resolvidas pela camada de serviço
        // que possui acesso aos respectivos repositories
        
        return entity;
    }
}