package br.dev.rodrigopinheiro.B3DataManager.domain.port;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;

import java.util.List;
import java.util.Optional;

/**
 * Port de repositório para ativos financeiros na arquitetura hexagonal.
 * 
 * <p>Este port define o contrato para operações de persistência de ativos financeiros,
 * seguindo os princípios da arquitetura hexagonal onde o domínio define as interfaces
 * e a infraestrutura implementa os adapters.</p>
 * 
 * <p><strong>Características principais:</strong></p>
 * <ul>
 *   <li>Utiliza objetos de domínio (AtivoFinanceiro, Portfolio) ao invés de IDs primitivos</li>
 *   <li>Suporte a enums tipados (TipoAtivo) para type safety</li>
 *   <li>Considera soft delete através de flags de deleção</li>
 *   <li>Operações otimizadas por portfolio e tipo de ativo</li>
 *   <li>Métodos de verificação de existência para validações</li>
 * </ul>
 * 
 * <p><strong>Operações disponíveis:</strong></p>
 * <ul>
 *   <li><strong>CRUD básico:</strong> save, findById, findAll, deleteById, existsById</li>
 *   <li><strong>Busca por código:</strong> findByCodigo, findByCodigoAndPortfolioId</li>
 *   <li><strong>Busca por portfolio:</strong> findByPortfolio, findByPortfolioId</li>
 *   <li><strong>Busca por nome:</strong> findByNome, existsByNomeAndPortfolioId</li>
 *   <li><strong>Busca por tipo:</strong> findByTipoAtivo, findAtivosRendaVariavel, findAtivosRendaFixa</li>
 *   <li><strong>Verificações:</strong> existsByCodigoAndPortfolio, existsByCodigoAndPortfolioIdAndNotDeleted</li>
 * </ul>
 * 
 * <p><strong>Padrões de nomenclatura:</strong></p>
 * <ul>
 *   <li>find* - Retorna Optional ou List</li>
 *   <li>exists* - Retorna boolean</li>
 *   <li>*AndNotDeleted - Considera soft delete</li>
 * </ul>
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
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