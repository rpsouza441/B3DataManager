package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import org.springframework.stereotype.Component;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoRendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoRendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoRendaFixaEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoRendaVariavelEntity;

@Component
public class AtivoFinanceiroMapper {

    public AtivoFinanceiro toDomain(AtivoFinanceiroEntity entity) {
        if (entity instanceof AtivoRendaFixaEntity) {
            return mapRendaFixaToDomain((AtivoRendaFixaEntity) entity);
        }
        if (entity instanceof AtivoRendaVariavelEntity) {
            return mapRendaVariavelToDomain((AtivoRendaVariavelEntity) entity);
        }
        throw new IllegalArgumentException("Tipo de entidade de ativo financeiro desconhecido: " + entity.getClass().getName());
    }

    public AtivoFinanceiroEntity toEntity(AtivoFinanceiro domain) {
        if (domain instanceof AtivoRendaFixa) {
            return mapRendaFixaToEntity((AtivoRendaFixa) domain);
        }
        if (domain instanceof AtivoRendaVariavel) {
            return mapRendaVariavelToEntity((AtivoRendaVariavel) domain);
        }
        throw new IllegalArgumentException("Tipo de domínio de ativo financeiro desconhecido: " + domain.getClass().getName());
    }

    private void mapCommonFieldsToDomain(AtivoFinanceiro domain, AtivoFinanceiroEntity entity) {
        domain.setId(entity.getId());
        domain.setCodigo(entity.getCodigo());
        domain.setNome(entity.getNome());
        domain.setDeletado(entity.isDeletado());
    }

    private void mapCommonFieldsToEntity(AtivoFinanceiroEntity entity, AtivoFinanceiro domain) {
        entity.setId(domain.getId());
        entity.setCodigo(domain.getCodigo());
        entity.setNome(domain.getNome());
        entity.setDeletado(domain.getDeletado());
    }

    private AtivoRendaFixa mapRendaFixaToDomain(AtivoRendaFixaEntity entity) {
        AtivoRendaFixa ativo = new AtivoRendaFixa();
        mapCommonFieldsToDomain(ativo, entity);
        ativo.setDataVencimento(entity.getDataVencimento());
        ativo.setTaxaJuros(entity.getTaxaJuros());
        return ativo;
    }

    private AtivoRendaVariavel mapRendaVariavelToDomain(AtivoRendaVariavelEntity entity) {
        AtivoRendaVariavel ativo = new AtivoRendaVariavel();
        mapCommonFieldsToDomain(ativo, entity);
        ativo.setTicker(entity.getTicker());
        ativo.setDividendYield(entity.getDividendYield());
        return ativo;
    }

    private AtivoRendaFixaEntity mapRendaFixaToEntity(AtivoRendaFixa domain) {
        AtivoRendaFixaEntity entity = new AtivoRendaFixaEntity();
        mapCommonFieldsToEntity(entity, domain);
        entity.setDataVencimento(domain.getDataVencimento());
        entity.setTaxaJuros(domain.getTaxaJuros());
        return entity;
    }

    private AtivoRendaVariavelEntity mapRendaVariavelToEntity(AtivoRendaVariavel domain) {
        AtivoRendaVariavelEntity entity = new AtivoRendaVariavelEntity();
        mapCommonFieldsToEntity(entity, domain);
        entity.setTicker(domain.getTicker());
        entity.setDividendYield(domain.getDividendYield());
        return entity;
    }
}