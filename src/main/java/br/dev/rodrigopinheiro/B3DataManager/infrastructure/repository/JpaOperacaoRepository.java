package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.entity.OperacaoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repositório JPA para OperacaoJpaEntity.
 * Interface Spring Data JPA para operações de persistência.
 */
@Repository
public interface JpaOperacaoRepository extends JpaRepository<OperacaoJpaEntity, Long> {
    
    /**
     * Verifica se existe uma operação com o ID original e usuário especificados.
     * 
     * @param idOriginal O ID original da operação
     * @param usuarioId O ID do usuário
     * @return true se existe uma operação com esses parâmetros
     */
    boolean existsByIdOriginalAndUsuarioId(Long idOriginal, Long usuarioId);
    
    /**
     * Busca uma operação por ID original e usuário.
     * 
     * @param idOriginal O ID original da operação
     * @param usuarioId O ID do usuário
     * @return Optional contendo a operação se encontrada
     */
    Optional<OperacaoJpaEntity> findByIdOriginalAndUsuarioId(Long idOriginal, Long usuarioId);
    
    /**
     * Busca operações com filtros e ownership obrigatório.
     * Query otimizada sem conversões desnecessárias de data.
     */
    @Query("SELECT o FROM OperacaoJpaEntity o WHERE " +
           "(:entradaSaida IS NULL OR o.entradaSaida = :entradaSaida) AND " +
           "(:startDate IS NULL OR o.data >= :startDate) AND " +
           "(:endDate IS NULL OR o.data <= :endDate) AND " +
           "(:movimentacao IS NULL OR LOWER(o.movimentacao) LIKE LOWER(CONCAT('%', :movimentacao, '%'))) AND " +
           "(:produto IS NULL OR LOWER(o.produto) LIKE LOWER(CONCAT('%', :produto, '%'))) AND " +
           "(:instituicao IS NULL OR LOWER(o.instituicao) LIKE LOWER(CONCAT('%', :instituicao, '%'))) AND " +
           "(:duplicado IS NULL OR o.duplicado = :duplicado) AND " +
           "(:dimensionado IS NULL OR o.dimensionado = :dimensionado) AND " +
           "o.usuarioId = :usuarioId")
    Page<OperacaoJpaEntity> findByFiltersAndUsuarioId(
        @Param("entradaSaida") String entradaSaida,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("movimentacao") String movimentacao,
        @Param("produto") String produto,
        @Param("instituicao") String instituicao,
        @Param("duplicado") Boolean duplicado,
        @Param("dimensionado") Boolean dimensionado,
        @Param("usuarioId") Long usuarioId,
        Pageable pageable
    );
    
    /**
     * Conta operações com filtros e ownership obrigatório.
     * Query otimizada para contagem.
     */
    @Query("SELECT COUNT(o) FROM OperacaoJpaEntity o WHERE " +
           "(:entradaSaida IS NULL OR o.entradaSaida = :entradaSaida) AND " +
           "(:startDate IS NULL OR o.data >= :startDate) AND " +
           "(:endDate IS NULL OR o.data <= :endDate) AND " +
           "(:movimentacao IS NULL OR LOWER(o.movimentacao) LIKE LOWER(CONCAT('%', :movimentacao, '%'))) AND " +
           "(:produto IS NULL OR LOWER(o.produto) LIKE LOWER(CONCAT('%', :produto, '%'))) AND " +
           "(:instituicao IS NULL OR LOWER(o.instituicao) LIKE LOWER(CONCAT('%', :instituicao, '%'))) AND " +
           "(:duplicado IS NULL OR o.duplicado = :duplicado) AND " +
           "(:dimensionado IS NULL OR o.dimensionado = :dimensionado) AND " +
           "o.usuarioId = :usuarioId")
    long countByFiltersAndUsuarioId(
        @Param("entradaSaida") String entradaSaida,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("movimentacao") String movimentacao,
        @Param("produto") String produto,
        @Param("instituicao") String instituicao,
        @Param("duplicado") Boolean duplicado,
        @Param("dimensionado") Boolean dimensionado,
        @Param("usuarioId") Long usuarioId
    );
}