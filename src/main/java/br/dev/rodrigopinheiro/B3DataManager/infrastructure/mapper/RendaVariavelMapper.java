package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.RendaVariavelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre RendaVariavel (domain) e RendaVariavelEntity (infrastructure)
 * Segue arquitetura hexagonal com mapeamento de objetos completos
 */
@Component
public class RendaVariavelMapper {

    @Autowired
    private AtivoFinanceiroMapper ativoFinanceiroMapper;

    /**
     * Converte RendaVariavelEntity para RendaVariavel (domain)
     */
    public RendaVariavel toDomain(RendaVariavelEntity entity) {
        if (entity == null) return null;
        
        RendaVariavel rendaVariavel = new RendaVariavel();
        rendaVariavel.setId(entity.getId());
        rendaVariavel.setDataCompra(entity.getDataCompra());
        rendaVariavel.setPrecoUnitario(entity.getPrecoUnitario());
        rendaVariavel.setQuantidade(entity.getQuantidade());
        rendaVariavel.setTotal(entity.getTotal());
        rendaVariavel.setTicker(entity.getTicker());
        rendaVariavel.setTipoAtivoFinanceiroVariavel(entity.getTipoAtivoFinanceiroVariavel());
        rendaVariavel.setDeletado(entity.getDeletado());
        
        // Mapear objeto AtivoFinanceiro completo (DDD compliance)
        if (entity.getAtivoFinanceiro() != null) {
            rendaVariavel.setAtivoFinanceiro(ativoFinanceiroMapper.toDomain(entity.getAtivoFinanceiro()));
        }
        
        return rendaVariavel;
    }

    /**
     * Converte RendaVariavel (domain) para RendaVariavelEntity (infrastructure)
     */
    public RendaVariavelEntity toEntity(RendaVariavel domain) {
        if (domain == null) return null;
        
        RendaVariavelEntity entity = new RendaVariavelEntity();
        entity.setId(domain.getId());
        entity.setDataCompra(domain.getDataCompra());
        entity.setPrecoUnitario(domain.getPrecoUnitario());
        entity.setQuantidade(domain.getQuantidade());
        entity.setTotal(domain.getTotal());
        entity.setTicker(domain.getTicker());
        entity.setTipoAtivoFinanceiroVariavel(domain.getTipoAtivoFinanceiroVariavel());
        entity.setDeletado(domain.getDeletado());
        
        // Mapear objeto AtivoFinanceiro completo (DDD compliance)
        if (domain.getAtivoFinanceiro() != null) {
            entity.setAtivoFinanceiro(ativoFinanceiroMapper.toEntity(domain.getAtivoFinanceiro()));
        }
        
        return entity;
    }
}