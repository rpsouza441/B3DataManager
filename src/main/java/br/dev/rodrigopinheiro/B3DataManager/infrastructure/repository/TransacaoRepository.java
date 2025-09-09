package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoTransacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoMovimentacao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.TransacaoEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository - TransacaoRepository (Opção 1)
 * 
 * Repository para gerenciar o histórico completo de transações.
 * Separado das posições atuais para otimizar consultas.
 * 
 * Características da Opção 1:
 * - Queries para histórico de operações
 * - Filtros por tipo de transação e movimentação
 * - Suporte a auditoria e relatórios
 */
@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoEntity, Long> {

    // ========== QUERIES DE COMPATIBILIDADE (adaptadas) ==========
    
    /**
     * Busca todas as transações ativas associadas a um ativo financeiro.
     * COMPATIBILIDADE: Adaptado para campo 'ativo' e 'dataOperacao'
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.ativoFinanceiro.id = :ativoFinanceiroId AND t.ativo = true ORDER BY t.dataOperacao DESC")
    List<TransacaoEntity> findByAtivoFinanceiroIdAndDeletadoFalse(@Param("ativoFinanceiroId") Long ativoFinanceiroId);

    /**
     * Busca todas as transações associadas a um ativo financeiro.
     * COMPATIBILIDADE: Adaptado para 'dataOperacao'
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.ativoFinanceiro.id = :ativoFinanceiroId ORDER BY t.dataOperacao DESC")
    List<TransacaoEntity> findByAtivoFinanceiroId(@Param("ativoFinanceiroId") Long ativoFinanceiroId);

    /**
     * Busca todas as transações por data.
     * COMPATIBILIDADE: Adaptado para 'dataOperacao'
     */
    List<TransacaoEntity> findByDataOperacao(LocalDate dataOperacao);
    
    /**
     * Método de compatibilidade para dataTransacao
     */
    List<TransacaoEntity> findByDataTransacao(LocalDate dataTransacao);

    /**
     * Busca todas as transações entre duas datas.
     * COMPATIBILIDADE: Adaptado para 'dataOperacao'
     */
    List<TransacaoEntity> findByDataOperacaoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Método de compatibilidade para dataTransacao
     */
    List<TransacaoEntity> findByDataTransacaoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca transações por ativo e entrada/saída
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.ativoFinanceiro.id = :ativoFinanceiroId AND t.entradaSaida = :entradaSaida AND t.ativo = true ORDER BY t.dataOperacao DESC")
    List<TransacaoEntity> findByAtivoFinanceiroIdAndEntradaSaida(@Param("ativoFinanceiroId") Long ativoFinanceiroId, @Param("entradaSaida") String entradaSaida);
    
    // ========== NOVAS QUERIES DA OPÇÃO 1 ==========
    
    /**
     * Busca transações por portfolio (histórico completo)
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.portfolio.id = :portfolioId AND t.ativo = true ORDER BY t.dataOperacao DESC")
    List<TransacaoEntity> findByPortfolioId(@Param("portfolioId") Long portfolioId);
    
    /**
     * Busca transações por tipo de transação
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.portfolio.id = :portfolioId AND t.tipoTransacao = :tipoTransacao AND t.ativo = true ORDER BY t.dataOperacao DESC")
    List<TransacaoEntity> findByTipoTransacao(@Param("portfolioId") Long portfolioId, @Param("tipoTransacao") TipoTransacao tipoTransacao);
    
    /**
     * Busca transações por tipo de movimentação
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.portfolio.id = :portfolioId AND t.tipoMovimentacao = :tipoMovimentacao AND t.ativo = true ORDER BY t.dataOperacao DESC")
    List<TransacaoEntity> findByTipoMovimentacao(@Param("portfolioId") Long portfolioId, @Param("tipoMovimentacao") TipoMovimentacao tipoMovimentacao);
    
    /**
     * Busca transações de compra (para cálculo de posição)
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.ativoFinanceiro.id = :ativoFinanceiroId AND t.tipoTransacao = 'ENTRADA' AND t.ativo = true ORDER BY t.dataOperacao ASC")
    List<TransacaoEntity> findComprasByAtivoFinanceiro(@Param("ativoFinanceiroId") Long ativoFinanceiroId);
    
    /**
     * Busca transações de venda (para cálculo de posição)
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.ativoFinanceiro.id = :ativoFinanceiroId AND t.tipoTransacao = 'VENDA' AND t.ativo = true ORDER BY t.dataOperacao ASC")
    List<TransacaoEntity> findVendasByAtivoFinanceiro(@Param("ativoFinanceiroId") Long ativoFinanceiroId);
    
    /**
     * Busca transações de rendimento
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.portfolio.id = :portfolioId AND t.tipoTransacao IN ('LUCRO_DIVIDENDO', 'LUCRO_JUROS', 'LUCRO_RENDIMENTO') AND t.ativo = true ORDER BY t.dataOperacao DESC")
    List<TransacaoEntity> findRendimentos(@Param("portfolioId") Long portfolioId);
    
    /**
     * Calcula valor total transacionado por ativo
     */
    @Query("SELECT COALESCE(SUM(t.valorTotal), 0) FROM TransacaoEntity t WHERE t.ativoFinanceiro.id = :ativoFinanceiroId AND t.tipoTransacao = 'ENTRADA' AND t.ativo = true")
    BigDecimal calcularValorTotalCompras(@Param("ativoFinanceiroId") Long ativoFinanceiroId);
    
    /**
     * Calcula quantidade total comprada por ativo
     */
    @Query("SELECT COALESCE(SUM(t.quantidade), 0) FROM TransacaoEntity t WHERE t.ativoFinanceiro.id = :ativoFinanceiroId AND t.tipoTransacao = 'ENTRADA' AND t.ativo = true")
    BigDecimal calcularQuantidadeTotalCompras(@Param("ativoFinanceiroId") Long ativoFinanceiroId);
    
    /**
     * Busca transações por período e ativo
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.ativoFinanceiro.id = :ativoFinanceiroId AND t.dataOperacao BETWEEN :dataInicio AND :dataFim AND t.ativo = true ORDER BY t.dataOperacao DESC")
    List<TransacaoEntity> findByAtivoAndPeriodo(@Param("ativoFinanceiroId") Long ativoFinanceiroId, @Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    /**
     * Busca transações por período e portfolio
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.portfolio.id = :portfolioId AND t.dataOperacao BETWEEN :dataInicio AND :dataFim AND t.ativo = true ORDER BY t.dataOperacao DESC")
    List<TransacaoEntity> findByPortfolioAndPeriodo(@Param("portfolioId") Long portfolioId, @Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    /**
     * Busca transações por instituição
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.instituicao.id = :instituicaoId AND t.portfolio.id = :portfolioId AND t.ativo = true ORDER BY t.dataOperacao DESC")
    List<TransacaoEntity> findByInstituicaoAndPortfolio(@Param("instituicaoId") Long instituicaoId, @Param("portfolioId") Long portfolioId);
    
    /**
     * Conta transações por tipo
     */
    @Query("SELECT t.tipoTransacao, COUNT(t) FROM TransacaoEntity t WHERE t.portfolio.id = :portfolioId AND t.ativo = true GROUP BY t.tipoTransacao")
    List<Object[]> contarTransacoesPorTipo(@Param("portfolioId") Long portfolioId);
    
    /**
     * Busca últimas transações do portfolio
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.portfolio.id = :portfolioId AND t.ativo = true ORDER BY t.dataOperacao DESC, t.id DESC")
    List<TransacaoEntity> findUltimasTransacoes(@Param("portfolioId") Long portfolioId, org.springframework.data.domain.Pageable pageable);
    
    /**
     * Busca transações para recálculo de posição (ordenadas cronologicamente)
     */
    @Query("SELECT t FROM TransacaoEntity t WHERE t.ativoFinanceiro.id = :ativoFinanceiroId AND t.ativo = true ORDER BY t.dataOperacao ASC, t.id ASC")
    List<TransacaoEntity> findForRecalculoPosicao(@Param("ativoFinanceiroId") Long ativoFinanceiroId);


}
