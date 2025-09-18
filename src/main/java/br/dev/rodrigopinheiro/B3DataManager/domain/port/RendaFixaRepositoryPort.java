package br.dev.rodrigopinheiro.B3DataManager.domain.port;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoRendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Port específico para repositório de ativos de renda fixa.
 * 
 * <p>Esta interface segue os princípios SOLID, especificamente:</p>
 * <ul>
 *   <li><strong>SRP:</strong> Responsabilidade única para operações de renda fixa</li>
 *   <li><strong>ISP:</strong> Interface segregada, sem métodos desnecessários</li>
 *   <li><strong>DIP:</strong> Dependência de abstração, não de implementação</li>
 * </ul>
 * 
 * <p><strong>Vantagens desta abordagem:</strong></p>
 * <ul>
 *   <li>Type safety completo - sem castings necessários</li>
 *   <li>Métodos específicos para renda fixa</li>
 *   <li>Facilita testes unitários com mocks específicos</li>
 *   <li>Melhor manutenibilidade e evolução independente</li>
 * </ul>
 * 
 * @author Rodrigo Pinheiro
 * @since 2.0
 */
public interface RendaFixaRepositoryPort {
    
    // ========== OPERAÇÕES BÁSICAS ==========
    
    /**
     * Salva um ativo de renda fixa.
     * @param ativo o ativo a ser salvo
     * @return o ativo salvo com ID atualizado
     */
    AtivoRendaFixa save(AtivoRendaFixa ativo);
    
    /**
     * Busca um ativo de renda fixa por ID.
     * @param id o ID do ativo
     * @return Optional contendo o ativo se encontrado
     */
    Optional<AtivoRendaFixa> findById(Long id);
    
    /**
     * Lista todos os ativos de renda fixa não deletados.
     * @return lista de ativos de renda fixa
     */
    List<AtivoRendaFixa> findAll();
    
    /**
     * Lista todos os ativos de renda fixa, incluindo deletados se especificado.
     * @param includeDeleted se deve incluir ativos deletados
     * @return lista de ativos de renda fixa
     */
    List<AtivoRendaFixa> findAll(boolean includeDeleted);
    
    /**
     * Remove um ativo de renda fixa por ID.
     * @param id o ID do ativo a ser removido
     */
    void deleteById(Long id);
    
    /**
     * Verifica se existe um ativo de renda fixa com o ID especificado.
     * @param id o ID a ser verificado
     * @return true se existe, false caso contrário
     */
    boolean existsById(Long id);
    
    // ========== BUSCA POR PORTFOLIO ==========
    
    /**
     * Busca ativos de renda fixa por portfolio.
     * @param portfolio o portfolio
     * @return lista de ativos de renda fixa do portfolio
     */
    List<AtivoRendaFixa> findByPortfolio(Portfolio portfolio);
    
    /**
     * Busca ativos de renda fixa por ID do portfolio.
     * @param portfolioId o ID do portfolio
     * @return lista de ativos de renda fixa do portfolio
     */
    List<AtivoRendaFixa> findByPortfolioId(Long portfolioId);
    
    // ========== BUSCA POR CÓDIGO ==========
    
    /**
     * Busca ativo de renda fixa por código.
     * @param codigo o código do ativo
     * @return Optional contendo o ativo se encontrado
     */
    Optional<AtivoRendaFixa> findByCodigo(String codigo);
    
    /**
     * Busca ativo de renda fixa por código e portfolio.
     * @param codigo o código do ativo
     * @param portfolioId o ID do portfolio
     * @return Optional contendo o ativo se encontrado
     */
    Optional<AtivoRendaFixa> findByCodigoAndPortfolioId(String codigo, Long portfolioId);
    
    // ========== BUSCA ESPECÍFICA PARA RENDA FIXA ==========
    
    /**
     * Busca ativos de renda fixa por tipo.
     * @param tipo o tipo de renda fixa
     * @return lista de ativos do tipo especificado
     */
    List<AtivoRendaFixa> findByTipoRendaFixa(TipoAtivoFinanceiroFixa tipo);
    
    /**
     * Busca ativos de renda fixa por tipo e portfolio.
     * @param tipo o tipo de renda fixa
     * @param portfolioId o ID do portfolio
     * @return lista de ativos do tipo especificado no portfolio
     */
    List<AtivoRendaFixa> findByTipoRendaFixaAndPortfolioId(TipoAtivoFinanceiroFixa tipo, Long portfolioId);
    
    /**
     * Busca ativos de renda fixa que vencem em uma data específica.
     * @param dataVencimento a data de vencimento
     * @return lista de ativos que vencem na data
     */
    List<AtivoRendaFixa> findByDataVencimento(LocalDate dataVencimento);
    
    /**
     * Busca ativos de renda fixa que vencem entre duas datas.
     * @param dataInicio data inicial (inclusive)
     * @param dataFim data final (inclusive)
     * @return lista de ativos que vencem no período
     */
    List<AtivoRendaFixa> findByDataVencimentoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca ativos de renda fixa com taxa de juros maior que o valor especificado.
     * @param taxaMinima a taxa mínima
     * @return lista de ativos com taxa superior
     */
    List<AtivoRendaFixa> findByTaxaJurosGreaterThan(BigDecimal taxaMinima);
    
    /**
     * Busca ativos de renda fixa que estão vencidos (data de vencimento no passado).
     * @return lista de ativos vencidos
     */
    List<AtivoRendaFixa> findVencidos();
    
    /**
     * Busca ativos de renda fixa que vencem nos próximos N dias.
     * @param dias número de dias
     * @return lista de ativos que vencem no período
     */
    List<AtivoRendaFixa> findVencendoEm(int dias);
    
    // ========== VERIFICAÇÕES DE EXISTÊNCIA ==========
    
    /**
     * Verifica se existe ativo de renda fixa com o código no portfolio.
     * @param codigo o código do ativo
     * @param portfolioId o ID do portfolio
     * @return true se existe, false caso contrário
     */
    boolean existsByCodigoAndPortfolioId(String codigo, Long portfolioId);
    
    /**
     * Verifica se existe ativo de renda fixa não deletado com o código no portfolio.
     * @param codigo o código do ativo
     * @param portfolioId o ID do portfolio
     * @return true se existe e não está deletado, false caso contrário
     */
    boolean existsByCodigoAndPortfolioIdAndNotDeleted(String codigo, Long portfolioId);
}