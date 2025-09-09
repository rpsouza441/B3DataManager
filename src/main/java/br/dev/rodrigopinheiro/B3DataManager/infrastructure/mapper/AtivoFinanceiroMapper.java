package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import org.springframework.stereotype.Component;

@Component
public class AtivoFinanceiroMapper {

    public AtivoFinanceiro toDomain(AtivoFinanceiroEntity entity) {
        if (entity == null) return null;
        
        AtivoFinanceiro ativo = new AtivoFinanceiro();
        ativo.setId(entity.getId());
        ativo.setCodigo(entity.getCodigo());
        ativo.setNome(entity.getNome());
        ativo.setTipoAtivo(entity.getTipoAtivo());
        ativo.setDeletado(entity.getDeletado());
        
        // Relacionamentos são mapeados sob demanda pelos respectivos mappers
        // para evitar lazy loading issues
        
        return ativo;
    }

    public AtivoFinanceiroEntity toEntity(AtivoFinanceiro domain) {
        if (domain == null) return null;
        
        AtivoFinanceiroEntity entity = new AtivoFinanceiroEntity();
        entity.setId(domain.getId());
        entity.setCodigo(domain.getCodigo());
        entity.setNome(domain.getNome());
        entity.setTipoAtivo(domain.getTipoAtivo());
        entity.setDeletado(domain.getDeletado());
        
        return entity;
    }
}