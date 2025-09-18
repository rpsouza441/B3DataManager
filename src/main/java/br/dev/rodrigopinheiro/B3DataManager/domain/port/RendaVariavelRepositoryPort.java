package br.dev.rodrigopinheiro.B3DataManager.domain.port;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoRendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Port específico para repositório de ativos de renda variável.
 * 
 * <p>Esta interface segue os princípios SOLID, especificamente:</p>
 * <ul>
 *   <li><strong>SRP:</strong> Responsabilidade única para operações de renda variável</li>
 *   <li><strong>ISP:</strong> Interface segregada, sem métodos desnecessários</li>
 *   <li><strong>DIP:</strong> Dependência de abstração, não de implementação</li>
 * </ul>
 * 
 * <p><strong>Vantagens desta abordagem:</strong></p>
 * <ul>
 *   <li>Type safety completo - sem castings necessários</li>
 *   <li>Métodos específicos para renda variável</li>
 *   <li>Facilita testes unitários com mocks específicos</li>
 *   <li>Melhor manutenibilidade e evolução independente</li>
 * </ul>
 * 
 * @author Rodrigo Pinheiro
 * @since 2.0
 */
public interface RendaVariavelRepositoryPort {
    
    // ========== OPERAÇÕES BÁSICAS ==========
    
    /**
     * Salva um ativo de renda variável.
     * @param ativo o ativo a ser salvo
     * @return o ativo salvo com ID atualizado
     */
    AtivoRendaVariavel save(AtivoRendaVariavel ativo);
    
    /**
     * Busca um ativo de renda variável por ID.
     * @param id o ID do ativo
     * @return Optional contendo o ativo se encontrado
     */
    Optional<AtivoRendaVariavel> findById(Long id);
    
    /**
     * Lista todos os ativos de renda variável não deletados.
     * @return lista de ativos de renda variável
     */
    List<AtivoRendaVariavel> findAll();
    
    /**
     * Lista todos os ativos de renda variável, incluindo deletados se especificado.
     * @param includeDeleted se deve incluir ativos deletados
     * @return lista de ativos de renda variável
     */
    List<AtivoRendaVariavel> findAll(boolean includeDeleted);
    
    /**
     * Remove um ativo de renda variável por ID.
     * @param id o ID do ativo a ser removido
     */
    void deleteById(Long id);
    
    /**
     * Verifica se existe um ativo de renda variável com o ID especificado.
     * @param id o ID a ser verificado
     * @return true se existe, false caso contrário
     */
    boolean existsById(Long id);
    
    // ========== BUSCA POR PORTFOLIO ==========
    
    /**
     * Busca ativos de renda variável por portfolio.
     * @param portfolio o portfolio
     * @return lista de ativos de renda variável do portfolio
     */
    List<AtivoRendaVariavel> findByPortfolio(Portfolio portfolio);
    
    /**
     * Busca ativos de renda variável por ID do portfolio.
     * @param portfolioId o ID do portfolio
     * @return lista de ativos de renda variável do portfolio
     */
    List<AtivoRendaVariavel> findByPortfolioId(Long portfolioId);
    
    // ========== BUSCA POR CÓDIGO ==========
    
    /**
     * Busca ativo de renda variável por código.
     * @param codigo o código do ativo
     * @return Optional contendo o ativo se encontrado
     */
    Optional<AtivoRendaVariavel> findByCodigo(String codigo);
    
    /**
     * Busca ativo de renda variável por código e portfolio.
     * @param codigo o código do ativo
     * @param portfolioId o ID do portfolio
     * @return Optional contendo o ativo se encontrado
     */
    Optional<AtivoRendaVariavel> findByCodigoAndPortfolioId(String codigo, Long portfolioId);
    
    // ========== BUSCA ESPECÍFICA PARA RENDA VARIÁVEL ==========
    
    /**
     * Busca ativos de renda variável por tipo.
     * @param tipo o tipo de renda variável
     * @return lista de ativos do tipo especificado
     */
    List<AtivoRendaVariavel> findByTipoRendaVariavel(TipoAtivoFinanceiroVariavel tipo);
    
    /**
     * Busca ativos de renda variável por tipo e portfolio.
     * @param tipo o tipo de renda variável
     * @param portfolioId o ID do portfolio
     * @return lista de ativos do tipo especificado no portfolio
     */
    List<AtivoRendaVariavel> findByTipoRendaVariavelAndPortfolioId(TipoAtivoFinanceiroVariavel tipo, Long portfolioId);
    
    /**
     * Busca ativos de renda variável por setor.
     * @param setor o setor
     * @return lista de ativos do setor especificado
     */
    List<AtivoRendaVariavel> findBySetor(String setor);
    
    /**
     * Busca ativos de renda variável por setor e portfolio.
     * @param setor o setor
     * @param portfolioId o ID do portfolio
     * @return lista de ativos do setor no portfolio
     */
    List<AtivoRendaVariavel> findBySetorAndPortfolioId(String setor, Long portfolioId);
    
    /**
     * Busca ativos de renda variável com preço atual maior que o valor especificado.
     * @param precoMinimo o preço mínimo
     * @return lista de ativos com preço superior
     */
    List<AtivoRendaVariavel> findByPrecoAtualGreaterThan(BigDecimal precoMinimo);
    
    /**
     * Busca ativos de renda variável com preço atual menor que o valor especificado.
     * @param precoMaximo o preço máximo
     * @return lista de ativos com preço inferior
     */
    List<AtivoRendaVariavel> findByPrecoAtualLessThan(BigDecimal precoMaximo);
    
    /**
     * Busca ativos de renda variável com preço atual entre dois valores.
     * @param precoMinimo o preço mínimo (inclusive)
     * @param precoMaximo o preço máximo (inclusive)
     * @return lista de ativos na faixa de preço
     */
    List<AtivoRendaVariavel> findByPrecoAtualBetween(BigDecimal precoMinimo, BigDecimal precoMaximo);
    
    /**
     * Busca ativos de renda variável com dividend yield maior que o valor especificado.
     * @param dividendYieldMinimo o dividend yield mínimo
     * @return lista de ativos com dividend yield superior
     */
    List<AtivoRendaVariavel> findByDividendYieldGreaterThan(BigDecimal dividendYieldMinimo);
    
    /**
     * Busca ativos de renda variável comprados em uma data específica.
     * @param dataCompra a data de compra
     * @return lista de ativos comprados na data
     */
    List<AtivoRendaVariavel> findByDataCompra(LocalDate dataCompra);
    
    /**
     * Busca ativos de renda variável comprados entre duas datas.
     * @param dataInicio data inicial (inclusive)
     * @param dataFim data final (inclusive)
     * @return lista de ativos comprados no período
     */
    List<AtivoRendaVariavel> findByDataCompraBetween(LocalDate dataInicio, LocalDate dataFim);
    
    // ========== ANÁLISES ESPECÍFICAS ==========
    
    /**
     * Busca ativos de renda variável com valorização positiva.
     * @return lista de ativos valorizados
     */
    List<AtivoRendaVariavel> findValorizados();
    
    /**
     * Busca ativos de renda variável com desvalorização.
     * @return lista de ativos desvalorizados
     */
    List<AtivoRendaVariavel> findDesvalorizados();
    
    /**
     * Busca ativos de renda variável com maior volume de negociação.
     * @param volumeMinimo o volume mínimo
     * @return lista de ativos com volume superior
     */
    List<AtivoRendaVariavel> findByVolumeGreaterThan(Long volumeMinimo);
    
    // ========== VERIFICAÇÕES DE EXISTÊNCIA ==========
    
    /**
     * Verifica se existe ativo de renda variável com o código no portfolio.
     * @param codigo o código do ativo
     * @param portfolioId o ID do portfolio
     * @return true se existe, false caso contrário
     */
    boolean existsByCodigoAndPortfolioId(String codigo, Long portfolioId);
    
    /**
     * Verifica se existe um ativo com o código especificado no portfolio e não está deletado.
     * @param codigo o código do ativo
     * @param portfolioId o ID do portfolio
     * @return true se existe e não está deletado, false caso contrário
     */
    boolean existsByCodigoAndPortfolioIdAndNotDeleted(String codigo, Long portfolioId);
    
    // ========== MÉTODOS ESPECÍFICOS PARA O SERVICE ==========
    
    /**
     * Conta ativos de renda variável por tipo e usuário.
     * @param tipo o tipo de ativo
     * @param usuarioId o ID do usuário
     * @return quantidade de ativos
     */
    Long countByTipoRendaVariavelAndAtivoFinanceiroUsuarioId(String tipo, Long usuarioId);
    
    /**
     * Conta ativos de renda variável por tipos (lista) e usuário.
     * @param tipos lista de tipos de ativo
     * @param usuarioId o ID do usuário
     * @return quantidade de ativos
     */
    Long countByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(List<String> tipos, Long usuarioId);
    
    /**
     * Conta ativos com filtros específicos.
     * @param tipo tipo do ativo
     * @param nome nome do ativo
     * @param startDate data inicial
     * @param endDate data final
     * @param precoMedioMin preço médio mínimo
     * @param precoMedioMax preço médio máximo
     * @param usuarioId ID do usuário
     * @return quantidade de ativos
     */
    long countByFilters(String tipo, String nome, LocalDate startDate, LocalDate endDate,
                       BigDecimal precoMedioMin, BigDecimal precoMedioMax, Long usuarioId);
    
    /**
     * Conta ativos com filtros específicos usando lista de tipos.
     * @param tipos lista de tipos de ativo
     * @param nome nome do ativo
     * @param startDate data inicial
     * @param endDate data final
     * @param precoMedioMin preço médio mínimo
     * @param precoMedioMax preço médio máximo
     * @param usuarioId ID do usuário
     * @return quantidade de ativos
     */
    long countByFiltersIn(List<String> tipos, String nome, LocalDate startDate, LocalDate endDate,
                         BigDecimal precoMedioMin, BigDecimal precoMedioMax, Long usuarioId);
    
    /**
     * Busca ativos com filtros específicos e paginação.
     * @param tipo tipo do ativo
     * @param nome nome do ativo
     * @param startDate data inicial
     * @param endDate data final
     * @param precoMedioMin preço médio mínimo
     * @param precoMedioMax preço médio máximo
     * @param usuarioId ID do usuário
     * @param pageable configuração de paginação
     * @return página de ativos
     */
    Page<AtivoRendaVariavel> findByFilters(String tipo, String nome, LocalDate startDate, LocalDate endDate,
                                          BigDecimal precoMedioMin, BigDecimal precoMedioMax,
                                          Long usuarioId, Pageable pageable);
    
    /**
     * Busca ativos com filtros específicos usando lista de tipos e paginação.
     * @param tipos lista de tipos de ativo
     * @param nome nome do ativo
     * @param startDate data inicial
     * @param endDate data final
     * @param precoMedioMin preço médio mínimo
     * @param precoMedioMax preço médio máximo
     * @param usuarioId ID do usuário
     * @param pageable configuração de paginação
     * @return página de ativos
     */
    Page<AtivoRendaVariavel> findByFiltersIn(List<String> tipos, String nome, LocalDate startDate, LocalDate endDate,
                                            BigDecimal precoMedioMin, BigDecimal precoMedioMax,
                                            Long usuarioId, Pageable pageable);
    
    /**
     * Busca ativos por tipos de renda variável e usuário.
     * @param tipos lista de tipos de renda variável
     * @param usuarioId ID do usuário
     * @return lista de ativos
     */
    List<AtivoRendaVariavel> findByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(List<String> tipos, Long usuarioId);
}