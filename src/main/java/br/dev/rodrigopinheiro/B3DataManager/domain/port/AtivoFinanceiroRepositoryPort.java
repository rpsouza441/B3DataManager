package br.dev.rodrigopinheiro.B3DataManager.domain.port;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;

import java.util.List;
import java.util.Optional;

/**
 * Port para repositório de ativos financeiros (Arquitetura Corrigida)
 * Define operações usando domain models ao invés de IDs
 * Usa enums tipados e considera soft delete
 */
public interface AtivoFinanceiroRepositoryPort {
    
    // ========== OPERAÇÕES BÁSICAS ==========
    AtivoFinanceiro save(AtivoFinanceiro ativo);
    Optional<AtivoFinanceiro> findById(Long id);
    List<AtivoFinanceiro> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    
    // ========== BUSCA POR CÓDIGO ==========
    Optional<AtivoFinanceiro> findByCodigo(String codigo);
    Optional<AtivoFinanceiro> findByCodigoAndPortfolioId(String codigo, Long portfolioId);
    boolean existsByCodigoAndPortfolioId(String codigo, Long portfolioId);
    
    // ========== BUSCA POR PORTFOLIO ==========
    List<AtivoFinanceiro> findByPortfolio(Portfolio portfolio);
    List<AtivoFinanceiro> findByPortfolioId(Long portfolioId);
    
    // ========== BUSCA POR NOME ==========
    List<AtivoFinanceiro> findByNome(String nome);
    boolean existsByNomeAndPortfolioId(String nome, Long portfolioId);
    
    // ========== BUSCA POR TIPO (ENUM TIPADO) ==========
    List<AtivoFinanceiro> findByTipoAtivo(TipoAtivo tipoAtivo);
    List<AtivoFinanceiro> findByTipoAtivoAndPortfolioId(TipoAtivo tipoAtivo, Long portfolioId);
    List<AtivoFinanceiro> findAtivosRendaVariavel(Long portfolioId);
    List<AtivoFinanceiro> findAtivosRendaFixa(Long portfolioId);
    
    // ========== VERIFICAÇÕES DE EXISTÊNCIA ==========
    boolean existsByCodigoAndPortfolio(String codigo, Portfolio portfolio);
    boolean existsByCodigoAndPortfolioIdAndNotDeleted(String codigo, Long portfolioId);
}