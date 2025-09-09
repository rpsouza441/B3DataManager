package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repository - AtivoFinanceiroRepository (Arquitetura Corrigida)
 * 
 * Repository para gerenciar ativos financeiros unificados.
 * Suporta tanto renda variável quanto renda fixa.
 * 
 * Características da arquitetura corrigida:
 * - Queries por tipo de ativo usando enums tipados
 * - Type safety completa
 * - Sem dependências de propriedades genéricas
 */
@Repository
public interface AtivoFinanceiroRepository extends JpaRepository<AtivoFinanceiroEntity, Long>, JpaSpecificationExecutor<AtivoFinanceiroEntity> {

    // ========== QUERIES DE COMPATIBILIDADE (mantidas) ==========
    
    Optional<AtivoFinanceiroEntity> findByNomeAndPortfolio(String nomeAtivo, PortfolioEntity portfolio);

    /**
     * Busca todos os AtivoFinanceiros que não estão deletados.
     */
    @Query("SELECT af FROM AtivoFinanceiroEntity af WHERE af.deletado = false")
    List<AtivoFinanceiroEntity> findByDeletadoFalse();

    /**
     * Busca um ativo financeiro pelo ID, verificando se não está deletado.
     */
    @Query("SELECT af FROM AtivoFinanceiroEntity af WHERE af.id = :id AND af.deletado = false")
    Optional<AtivoFinanceiroEntity> findByIdAndDeletadoFalse(@Param("id") Long id);
    
    // ========== QUERIES TIPADAS E SEGURAS ==========
    
    /**
     * Busca ativo por código
     */
    Optional<AtivoFinanceiroEntity> findByCodigo(String codigo);
    
    /**
     * Busca ativo por código e portfolio
     */
    Optional<AtivoFinanceiroEntity> findByCodigoAndPortfolioId(String codigo, Long portfolioId);
    
    /**
     * Busca ativos por tipo usando enum tipado
     */
    List<AtivoFinanceiroEntity> findByTipoAtivoAndPortfolioIdAndDeletadoFalse(br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo tipoAtivo, Long portfolioId);
    
    /**
     * Busca ativos de renda variável
     */
    @Query("SELECT af FROM AtivoFinanceiroEntity af WHERE af.tipoAtivo = 'RENDA_VARIAVEL' AND af.portfolio.id = :portfolioId AND af.deletado = false")
    List<AtivoFinanceiroEntity> findAtivosRendaVariavel(@Param("portfolioId") Long portfolioId);
    
    /**
     * Busca ativos de renda fixa
     */
    @Query("SELECT af FROM AtivoFinanceiroEntity af WHERE af.tipoAtivo = 'RENDA_FIXA' AND af.portfolio.id = :portfolioId AND af.deletado = false")
    List<AtivoFinanceiroEntity> findAtivosRendaFixa(@Param("portfolioId") Long portfolioId);
    
    /**
     * Conta ativos por tipo
     */
    @Query("SELECT af.tipoAtivo, COUNT(af) FROM AtivoFinanceiroEntity af WHERE af.portfolio.id = :portfolioId AND af.deletado = false GROUP BY af.tipoAtivo")
    List<Object[]> contarAtivosPorTipo(@Param("portfolioId") Long portfolioId);
    
    // Queries específicas removidas - dados específicos vêm das operações
    // Se necessário, adicionar campos tipados via migrations
    
    /**
     * Verifica se existe ativo com código no portfolio
     */
    boolean existsByCodigoAndPortfolioId(String codigo, Long portfolioId);
    
    /**
     * Verifica se existe ativo ativo (não deletado) com código no portfolio
     */
    boolean existsByCodigoAndPortfolioIdAndDeletadoFalse(String codigo, Long portfolioId);

    /**
     * Busca um ativo financeiro pelo nome.
     *
     * @param nome Nome do ativo financeiro.
     * @return Um Optional contendo o ativo financeiro, se encontrado.
     */
    Optional<AtivoFinanceiroEntity> findByNome(String nome);

    /**
     * Busca todos os ativos financeiros pelo nome.
     *
     * @param nome Nome do ativo financeiro.
     * @return Lista de ativos financeiros com o nome especificado.
     */
    List<AtivoFinanceiroEntity> findAllByNome(String nome);

    /**
     * Busca todos os ativos financeiros de um portfolio.
     *
     * @param portfolioId ID do portfolio.
     * @return Lista de ativos financeiros do portfolio.
     */
    List<AtivoFinanceiroEntity> findByPortfolioId(Long portfolioId);

    /**
     * Verifica se existe um ativo financeiro com o nome e portfolio especificados.
     *
     * @param nome Nome do ativo financeiro.
     * @param portfolioId ID do portfolio.
     * @return true se existir, false caso contrário.
     */
    boolean existsByNomeAndPortfolioId(String nome, Long portfolioId);

    //TODO Testar
    @Query("SELECT af FROM AtivoFinanceiroEntity af " +
            "JOIN af.portfolio p " +
            "JOIN p.usuario u " +
            "JOIN u.instituicoes i " +
            "WHERE u.id = :userId AND i.id = :instituicaoId")
    List<AtivoFinanceiroEntity> findAtivosByUsuarioAndInstituicao(@Param("userId") Long userId,
                                                            @Param("instituicaoId") Long instituicaoId);

}
