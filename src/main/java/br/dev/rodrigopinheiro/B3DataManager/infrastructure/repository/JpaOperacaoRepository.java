package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Repositório JPA para OperacaoJpaEntity.
 * Interface Spring Data JPA para operações de persistência.
 */
@Repository
public interface JpaOperacaoRepository extends JpaRepository<OperacaoEntity, Long> {
    
    /**
     * Verifica se existe uma operação com o ID original e usuário especificados.
     * 
     * @param idOriginal O ID original da operação
     * @param usuarioId O ID do usuário
     * @return true se existe uma operação com esses parâmetros
     */
    boolean existsByIdOriginalAndUsuario_Id(Long idOriginal, Long usuarioId);
    
    /**
     * Busca uma operação por ID original e usuário.
     * 
     * @param idOriginal O ID original da operação
     * @param usuarioId O ID do usuário
     * @return Optional contendo a operação se encontrada
     */
    Optional<OperacaoEntity> findByIdOriginalAndUsuario_Id(Long idOriginal, Long usuarioId);
    
    /**
     * Busca operações com filtros e ownership obrigatório.
     * Query otimizada sem conversões desnecessárias de data.
     */
    @Query("SELECT o FROM OperacaoEntity o WHERE " +
           "(:entradaSaida IS NULL OR o.entradaSaida = :entradaSaida) AND " +
           "(:startDate IS NULL OR o.data >= :startDate) AND " +
           "(:endDate IS NULL OR o.data <= :endDate) AND " +
           "(:movimentacao IS NULL OR LOWER(o.movimentacao) LIKE LOWER(CONCAT('%', :movimentacao, '%'))) AND " +
           "(:produto IS NULL OR LOWER(o.produto) LIKE LOWER(CONCAT('%', :produto, '%'))) AND " +
           "(:instituicao IS NULL OR LOWER(o.instituicao) LIKE LOWER(CONCAT('%', :instituicao, '%'))) AND " +
           "(:duplicado IS NULL OR o.duplicado = :duplicado) AND " +
           "(:dimensionado IS NULL OR o.dimensionado = :dimensionado) AND " +
           "o.usuario.id = :usuarioId")
    Page<OperacaoEntity> findByFiltersAndUsuarioId(
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
    @Query("SELECT COUNT(o) FROM OperacaoEntity o WHERE " +
           "(:entradaSaida IS NULL OR o.entradaSaida = :entradaSaida) AND " +
           "(:startDate IS NULL OR o.data >= :startDate) AND " +
           "(:endDate IS NULL OR o.data <= :endDate) AND " +
           "(:movimentacao IS NULL OR LOWER(o.movimentacao) LIKE LOWER(CONCAT('%', :movimentacao, '%'))) AND " +
           "(:produto IS NULL OR LOWER(o.produto) LIKE LOWER(CONCAT('%', :produto, '%'))) AND " +
           "(:instituicao IS NULL OR LOWER(o.instituicao) LIKE LOWER(CONCAT('%', :instituicao, '%'))) AND " +
           "(:duplicado IS NULL OR o.duplicado = :duplicado) AND " +
           "(:dimensionado IS NULL OR o.dimensionado = :dimensionado) AND " +
           "o.usuario.id = :usuarioId")
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
    
    /**
     * Busca a primeira operação que corresponde exatamente aos parâmetros fornecidos.
     * Usado para verificação de duplicidade durante importação.
     * 
     * @param data Data da operação
     * @param movimentacao Tipo de movimentação
     * @param produto Produto da operação
     * @param instituicao Instituição da operação
     * @param quantidade Quantidade da operação
     * @param precoUnitario Preço unitário da operação
     * @param valorOperacao Valor total da operação
     * @param duplicado Flag indicando se deve buscar apenas operações não duplicadas
     * @param usuarioId ID do usuário proprietário
     * @return Optional contendo a primeira operação encontrada
     */
    Optional<OperacaoEntity> findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuario_Id(
        LocalDate data,
        String movimentacao,
        String produto,
        String instituicao,
        BigDecimal quantidade,
        BigDecimal precoUnitario,
        BigDecimal valorOperacao,
        boolean duplicado,
        Long usuarioId
    );
    
    /**
     * Busca operações por critérios de dimensionado e duplicado com paginação.
     */
    Page<OperacaoEntity> findByDimensionadoAndDuplicado(
        boolean dimensionado,
        boolean duplicado,
        Pageable pageable
    );
    
    /**
     * Conta operações por critérios de dimensionado e duplicado.
     */
    long countByDimensionadoAndDuplicado(
        boolean dimensionado,
        boolean duplicado
    );
}