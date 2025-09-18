package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Transacao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.TransacaoEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Transacao (domain) e TransacaoEntity (infrastructure).
 * 
 * Segue princípios SOLID:
 * - Single Responsibility: Apenas mapeamento de campos básicos
 * - Open/Closed: Extensível sem modificação
 * - Dependency Inversion: Não depende de outros mappers
 * 
 * Relacionamentos são resolvidos pela camada de serviço para evitar:
 * - Dependências circulares
 * - Violação do princípio de responsabilidade única
 * - Problemas de lazy loading
 */
@Component
public class TransacaoMapper {

    /**
     * Converte TransacaoEntity para Transacao (domain).
     * 
     * @param entity A entidade de infraestrutura
     * @return O modelo de domínio correspondente
     */
    public Transacao toDomain(TransacaoEntity entity) {
        if (entity == null) return null;
        
        Transacao transacao = new Transacao();
        
        // Campos básicos
        transacao.setId(entity.getId());
        transacao.setDataOperacao(entity.getDataOperacao());
        transacao.setTipoTransacao(entity.getTipoTransacao());
        transacao.setTipoMovimentacao(entity.getTipoMovimentacao());
        transacao.setQuantidade(entity.getQuantidade());
        transacao.setPrecoUnitario(entity.getPrecoUnitario());
        transacao.setValorTotal(entity.getValorTotal());
        transacao.setTaxas(entity.getTaxas());
        transacao.setValorLiquido(entity.getValorLiquido());
        transacao.setObservacoes(entity.getObservacoes());
        transacao.setDeletado(!entity.getAtivo()); // Entity usa 'ativo', domain usa 'deletado' (inverso)
        
        // Relacionamentos são mapeados sob demanda pela camada de serviço
        // para evitar lazy loading issues e dependências circulares
        
        return transacao;
    }

    /**
     * Converte Transacao (domain) para TransacaoEntity (infrastructure).
     * 
     * @param domain O modelo de domínio
     * @return A entidade de infraestrutura correspondente
     */
    public TransacaoEntity toEntity(Transacao domain) {
        if (domain == null) return null;
        
        TransacaoEntity entity = new TransacaoEntity();
        
        // Campos básicos
        entity.setId(domain.getId());
        entity.setDataOperacao(domain.getDataOperacao());
        entity.setTipoTransacao(domain.getTipoTransacao());
        entity.setTipoMovimentacao(domain.getTipoMovimentacao());
        entity.setQuantidade(domain.getQuantidade());
        entity.setPrecoUnitario(domain.getPrecoUnitario());
        entity.setValorTotal(domain.getValorTotal());
        entity.setTaxas(domain.getTaxas());
        entity.setValorLiquido(domain.getValorLiquido());
        entity.setObservacoes(domain.getObservacoes());
        entity.setAtivo(!domain.getDeletado()); // Domain usa 'deletado', entity usa 'ativo' (inverso)
        
        // Inicializa DARF como null (será configurado posteriormente se necessário)
        entity.setDarf(null);
        
        // Note: Relacionamentos (ativoFinanceiro, portfolio, instituicao, operacao, darf)
        // devem ser resolvidos pela camada de serviço que possui acesso aos respectivos repositories
        
        return entity;
    }
}