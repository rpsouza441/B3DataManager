package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Transacao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.TransacaoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Transacao (domain) e TransacaoEntity (infrastructure)
 * Segue arquitetura hexagonal com mapeamento de objetos completos (DDD compliance)
 */
@Component
public class TransacaoMapper {

    @Autowired
    private AtivoFinanceiroMapper ativoFinanceiroMapper;

    @Autowired
    private PortfolioMapper portfolioMapper;

    @Autowired
    private InstituicaoMapper instituicaoMapper;

    /**
     * Converte TransacaoEntity para Transacao (domain)
     * Mapeia objetos completos seguindo princípios DDD
     */
    public Transacao toDomain(TransacaoEntity entity) {
        if (entity == null) return null;
        
        Transacao transacao = new Transacao();
        transacao.setId(entity.getId());
        transacao.setDataOperacao(entity.getDataOperacao());
        transacao.setEntradaSaida(entity.getEntradaSaida());
        transacao.setQuantidade(entity.getQuantidade());
        transacao.setPrecoUnitario(entity.getPrecoUnitario());
        transacao.setValorTotal(entity.getValorTotal());
        transacao.setPrecoMedio(entity.getPrecoMedio());
        transacao.setTipoTransacao(entity.getTipoTransacao());
        transacao.setTipoMovimentacao(entity.getTipoMovimentacao());
        transacao.setDeletado(entity.getDeletado());
        
        // Mapear objetos completos ao invés de IDs (DDD compliance)
        if (entity.getAtivoFinanceiro() != null) {
            transacao.setAtivoFinanceiro(ativoFinanceiroMapper.toDomain(entity.getAtivoFinanceiro()));
        }
        if (entity.getPortfolio() != null) {
            transacao.setPortfolio(portfolioMapper.toDomain(entity.getPortfolio()));
        }
        if (entity.getInstituicao() != null) {
            transacao.setInstituicao(instituicaoMapper.toDomain(entity.getInstituicao()));
        }
        
        return transacao;
    }

    /**
     * Converte Transacao (domain) para TransacaoEntity (infrastructure)
     * Mapeia objetos completos seguindo princípios DDD
     */
    public TransacaoEntity toEntity(Transacao domain) {
        if (domain == null) return null;
        
        TransacaoEntity entity = new TransacaoEntity();
        entity.setId(domain.getId());
        entity.setDataOperacao(domain.getDataOperacao());
        entity.setEntradaSaida(domain.getEntradaSaida());
        entity.setQuantidade(domain.getQuantidade());
        entity.setPrecoUnitario(domain.getPrecoUnitario());
        entity.setValorTotal(domain.getValorTotal());
        entity.setPrecoMedio(domain.getPrecoMedio());
        entity.setTipoTransacao(domain.getTipoTransacao());
        entity.setTipoMovimentacao(domain.getTipoMovimentacao());
        entity.setDeletado(domain.getDeletado());
        
        // Mapear objetos completos ao invés de IDs (DDD compliance)
        if (domain.getAtivoFinanceiro() != null) {
            entity.setAtivoFinanceiro(ativoFinanceiroMapper.toEntity(domain.getAtivoFinanceiro()));
        }
        if (domain.getPortfolio() != null) {
            entity.setPortfolio(portfolioMapper.toEntity(domain.getPortfolio()));
        }
        if (domain.getInstituicao() != null) {
            entity.setInstituicao(instituicaoMapper.toEntity(domain.getInstituicao()));
        }
        
        return entity;
    }
}