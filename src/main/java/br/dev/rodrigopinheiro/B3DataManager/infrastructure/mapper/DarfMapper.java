package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Darf;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.DarfEntity;
import org.springframework.stereotype.Component;

@Component
public class DarfMapper {

    public Darf toDomain(DarfEntity entity) {
        if (entity == null) return null;
        
        Darf darf = new Darf();
        darf.setId(entity.getId());
        darf.setEstaPago(entity.isEstaPago());
        darf.setDataPagamento(entity.getDataPagamento());
        darf.setValor(entity.getValor());
        darf.setUsuarioId(entity.getUsuarioId());
        darf.setMesReferencia(entity.getMesReferencia());
        darf.setAnoReferencia(entity.getAnoReferencia());
        darf.setStatus(entity.getStatus());
        darf.setDataVencimento(entity.getDataVencimento());
        
        // Campos de auditoria herdados de AuditableEntity
        darf.setCreatedAt(entity.getCreatedAt());
        darf.setUpdatedAt(entity.getUpdatedAt());
        
        // Relacionamentos são mapeados sob demanda pelos respectivos mappers
        // para evitar lazy loading issues e referências circulares
        
        return darf;
    }

    public DarfEntity toEntity(Darf domain) {
        if (domain == null) return null;
        
        DarfEntity entity = new DarfEntity();
        entity.setId(domain.getId());
        entity.setEstaPago(domain.isEstaPago());
        entity.setDataPagamento(domain.getDataPagamento());
        entity.setValor(domain.getValor());
        entity.setUsuarioId(domain.getUsuarioId());
        entity.setMesReferencia(domain.getMesReferencia());
        entity.setAnoReferencia(domain.getAnoReferencia());
        entity.setStatus(domain.getStatus());
        entity.setDataVencimento(domain.getDataVencimento());
        
        // Relacionamentos são mapeados sob demanda pelos respectivos mappers
        // para evitar lazy loading issues e referências circulares
        
        return entity;
    }
}