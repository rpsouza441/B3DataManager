package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PosicaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository - PosicaoRepository (Opção 1)
 * 
 * Repository para gerenciar posições atuais dos ativos.
 * Separado do histórico de transações para otimizar consultas.
 * 
 * Características da Opção 1:
 * - Queries otimizadas para estado atual
 * - Filtros por tipo de ativo
 * - Percentuais pré-calculados
 */
@Repository
public interface PosicaoRepository extends JpaRepository<PosicaoEntity, Long> {
    
    /**
     * Busca todas as posições ativas de um portfolio
     */
    @Query("SELECT p FROM PosicaoEntity p WHERE p.portfolio.id = :portfolioId AND p.ativo = true AND p.quantidadeAtual > 0")
    List<PosicaoEntity> findPosicoesAtivasByPortfolioId(@Param("portfolioId") Long portfolioId);
    
    /**
     * Busca posição específica por ativo e portfolio
     */
    @Query("SELECT p FROM PosicaoEntity p WHERE p.ativoFinanceiro.id = :ativoId AND p.portfolio.id = :portfolioId AND p.ativo = true")
    Optional<PosicaoEntity> findByAtivoAndPortfolio(@Param("ativoId") Long ativoId, @Param("portfolioId") Long portfolioId);
    
    /**
     * Busca posições por tipo de ativo (OPÇÃO 1: Flexibilidade)
     */
    @Query("SELECT p FROM PosicaoEntity p JOIN p.ativoFinanceiro af WHERE af.tipoAtivo = :tipoAtivo AND p.portfolio.id = :portfolioId AND p.ativo = true AND p.quantidadeAtual > 0")
    List<PosicaoEntity> findPosicoesAtivasByTipoAtivo(@Param("portfolioId") Long portfolioId, @Param("tipoAtivo") String tipoAtivo);
    
    /**
     * Busca posições de renda variável (ações + FIIs)
     */
    @Query("SELECT p FROM PosicaoEntity p JOIN p.ativoFinanceiro af WHERE af.tipoAtivo IN ('ACAO', 'FII') AND p.portfolio.id = :portfolioId AND p.ativo = true AND p.quantidadeAtual > 0")
    List<PosicaoEntity> findPosicoesRendaVariavel(@Param("portfolioId") Long portfolioId);
    
    /**
     * Busca posições de renda fixa (CDB + LCI + Tesouro)
     */
    @Query("SELECT p FROM PosicaoEntity p JOIN p.ativoFinanceiro af WHERE af.tipoAtivo IN ('CDB', 'LCI', 'TESOURO') AND p.portfolio.id = :portfolioId AND p.ativo = true AND p.quantidadeAtual > 0")
    List<PosicaoEntity> findPosicoesRendaFixa(@Param("portfolioId") Long portfolioId);
    
    /**
     * Calcula valor total do portfolio (soma de todas as posições ativas)
     */
    @Query("SELECT COALESCE(SUM(p.valorAtual), 0) FROM PosicaoEntity p WHERE p.portfolio.id = :portfolioId AND p.ativo = true AND p.quantidadeAtual > 0")
    BigDecimal calcularValorTotalPortfolio(@Param("portfolioId") Long portfolioId);
    
    /**
     * Calcula valor investido total do portfolio
     */
    @Query("SELECT COALESCE(SUM(p.valorInvestido), 0) FROM PosicaoEntity p WHERE p.portfolio.id = :portfolioId AND p.ativo = true AND p.quantidadeAtual > 0")
    BigDecimal calcularValorInvestidoPortfolio(@Param("portfolioId") Long portfolioId);
    
    /**
     * Busca posições com percentual acima de um limite
     */
    @Query("SELECT p FROM PosicaoEntity p WHERE p.portfolio.id = :portfolioId AND p.percentualPortfolio >= :percentualMinimo AND p.ativo = true AND p.quantidadeAtual > 0")
    List<PosicaoEntity> findPosicoesComPercentualAcimaDe(@Param("portfolioId") Long portfolioId, @Param("percentualMinimo") BigDecimal percentualMinimo);
    
    /**
     * Busca posições ordenadas por valor (maiores primeiro)
     */
    @Query("SELECT p FROM PosicaoEntity p WHERE p.portfolio.id = :portfolioId AND p.ativo = true AND p.quantidadeAtual > 0 ORDER BY p.valorAtual DESC")
    List<PosicaoEntity> findPosicoesOrderByValorDesc(@Param("portfolioId") Long portfolioId);
    
    /**
     * Busca posições ordenadas por percentual (maiores primeiro)
     */
    @Query("SELECT p FROM PosicaoEntity p WHERE p.portfolio.id = :portfolioId AND p.ativo = true AND p.quantidadeAtual > 0 ORDER BY p.percentualPortfolio DESC")
    List<PosicaoEntity> findPosicoesOrderByPercentualDesc(@Param("portfolioId") Long portfolioId);
    
    /**
     * Conta número de posições ativas por tipo de ativo
     */
    @Query("SELECT af.tipoAtivo, COUNT(p) FROM PosicaoEntity p JOIN p.ativoFinanceiro af WHERE p.portfolio.id = :portfolioId AND p.ativo = true AND p.quantidadeAtual > 0 GROUP BY af.tipoAtivo")
    List<Object[]> contarPosicoesAtivasPorTipo(@Param("portfolioId") Long portfolioId);
    
    /**
     * Busca posições com lucro não realizado positivo
     */
    @Query("SELECT p FROM PosicaoEntity p WHERE p.portfolio.id = :portfolioId AND p.lucroNaoRealizado > 0 AND p.ativo = true AND p.quantidadeAtual > 0")
    List<PosicaoEntity> findPosicoesComLucro(@Param("portfolioId") Long portfolioId);
    
    /**
     * Busca posições com prejuízo não realizado
     */
    @Query("SELECT p FROM PosicaoEntity p WHERE p.portfolio.id = :portfolioId AND p.lucroNaoRealizado < 0 AND p.ativo = true AND p.quantidadeAtual > 0")
    List<PosicaoEntity> findPosicoesComPrejuizo(@Param("portfolioId") Long portfolioId);
    
    /**
     * Atualiza percentuais de todas as posições de um portfolio
     * (Usado pelo job assíncrono da Opção 1)
     */
    @Query("UPDATE PosicaoEntity p SET p.percentualPortfolio = (p.valorAtual * 100.0 / :valorTotalPortfolio) WHERE p.portfolio.id = :portfolioId AND p.ativo = true AND p.quantidadeAtual > 0")
    void atualizarPercentuaisPortfolio(@Param("portfolioId") Long portfolioId, @Param("valorTotalPortfolio") BigDecimal valorTotalPortfolio);
    
    /**
     * Busca posições que precisam de atualização de preço
     * (Baseado na data da última atualização)
     */
    @Query("SELECT p FROM PosicaoEntity p WHERE p.portfolio.id = :portfolioId AND p.dataUltimaAtualizacao < CURRENT_DATE AND p.ativo = true AND p.quantidadeAtual > 0")
    List<PosicaoEntity> findPosicoesParaAtualizarPreco(@Param("portfolioId") Long portfolioId);
    
    /**
     * Busca posições por propriedade específica do ativo
     * (Flexibilidade da Opção 1 com JSON)
     */
    @Query(value = "SELECT p.* FROM posicao p JOIN ativo_financeiro af ON p.ativo_financeiro_id = af.id WHERE p.portfolio_id = :portfolioId AND JSON_EXTRACT(af.propriedades_especificas, :jsonPath) = :valor AND p.ativo = true AND p.quantidade_atual > 0", nativeQuery = true)
    List<PosicaoEntity> findByPropriedadeEspecifica(@Param("portfolioId") Long portfolioId, @Param("jsonPath") String jsonPath, @Param("valor") String valor);
}