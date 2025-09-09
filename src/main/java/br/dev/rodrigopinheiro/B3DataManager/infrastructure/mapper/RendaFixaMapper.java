package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.RendaFixaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre RendaFixa (domain) e RendaFixaEntity (infrastructure)
 * Segue arquitetura hexagonal com mapeamento de objetos completos
 */
@Component
public class RendaFixaMapper {

    @Autowired
    private AtivoFinanceiroMapper ativoFinanceiroMapper;

    /**
     * Converte RendaFixaEntity para RendaFixa (domain)
     */
    public RendaFixa toDomain(RendaFixaEntity entity) {
        if (entity == null) return null;
        
        RendaFixa rendaFixa = new RendaFixa();
        rendaFixa.setId(entity.getId());
        rendaFixa.setDataCompra(entity.getDataCompra());
        rendaFixa.setPrecoUnitario(entity.getPrecoUnitario());
        rendaFixa.setQuantidade(entity.getQuantidade());
        rendaFixa.setTotal(entity.getTotal());
        rendaFixa.setTipoAtivoFinanceiroFixa(entity.getTipoAtivoFinanceiroFixa());
        rendaFixa.setDataVencimento(entity.getDataVencimento());
        rendaFixa.setTaxaJuros(entity.getTaxaJuros());
        rendaFixa.setDeletado(entity.getDeletado());
        
        // Mapear objeto AtivoFinanceiro completo (DDD compliance)
        if (entity.getAtivoFinanceiro() != null) {
            rendaFixa.setAtivoFinanceiro(ativoFinanceiroMapper.toDomain(entity.getAtivoFinanceiro()));
        }
        
        return rendaFixa;
    }

    /**
     * Converte RendaFixa (domain) para RendaFixaEntity (infrastructure)
     */
    public RendaFixaEntity toEntity(RendaFixa domain) {
        if (domain == null) return null;
        
        RendaFixaEntity entity = new RendaFixaEntity();
        entity.setId(domain.getId());
        entity.setDataCompra(domain.getDataCompra());
        entity.setPrecoUnitario(domain.getPrecoUnitario());
        entity.setQuantidade(domain.getQuantidade());
        entity.setTotal(domain.getTotal());
        entity.setTipoAtivoFinanceiroFixa(domain.getTipoAtivoFinanceiroFixa());
        entity.setDataVencimento(domain.getDataVencimento());
        entity.setTaxaJuros(domain.getTaxaJuros());
        entity.setDeletado(domain.getDeletado());
        
        // Mapear objeto AtivoFinanceiro completo (DDD compliance)
        if (domain.getAtivoFinanceiro() != null) {
            entity.setAtivoFinanceiro(ativoFinanceiroMapper.toEntity(domain.getAtivoFinanceiro()));
        }
        
        return entity;
    }
}