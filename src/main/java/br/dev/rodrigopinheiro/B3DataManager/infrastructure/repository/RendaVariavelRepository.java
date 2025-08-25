package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.RendaVariavel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RendaVariavelRepository extends JpaRepository<RendaVariavel, Long> {

    /**
     * Busca uma renda variável de um tipo específico associada a um usuário.
     */
    @Query("SELECT rv FROM RendaVariavel rv " +
            "WHERE rv.tipoRendaVariavel = :tipoRendaVariavel " +
            "AND rv.ativoFinanceiro.portfolio.usuario.id = :usuarioId " +
            "AND rv.ativoFinanceiro.deletado = false")
    List<RendaVariavel> findByTipoRendaVariavelAndAtivoFinanceiroUsuarioId(String tipoRendaVariavel, Long usuarioId);


    /**
     * Busca entidades de Renda Variável do tipo especificado associadas ao usuário, com suporte a paginação.
     *
     * @param tipoRendaVariavel Tipo de renda variável (ex.: "FII").
     * @param usuarioId         ID do usuário.
     * @param pageable          Objeto de paginação.
     * @return Página de entidades de Renda Variável.
     */
    @Query("SELECT rv FROM RendaVariavel rv " +
            "JOIN FETCH rv.ativoFinanceiro af " +
            "LEFT JOIN FETCH af.rendaVariaveis " +
            "WHERE rv.tipoRendaVariavel = :tipoRendaVariavel " +
            "AND af.portfolio.usuario.id = :usuarioId " +
            "AND af.deletado = false")
    Page<RendaVariavel> findByTipoRendaVariavelAndAtivoFinanceiroUsuarioId(
            String tipoRendaVariavel,
            Long usuarioId,
            Pageable pageable
    );


    /**
     * Busca entidades de Renda Variável por múltiplos tipos associadas ao usuário, com suporte à paginação.
     *
     * @param tipos     Lista de tipos de renda variável (ex.: "ACAO_ON", "ACAO_PN").
     * @param usuarioId ID do usuário logado.
     * @param pageable  Objeto de paginação.
     * @return Página de entidades de Renda Variável.
     */
    @Query("SELECT rv FROM RendaVariavel rv " +
            "WHERE rv.tipoRendaVariavel IN :tipos " +
            "AND rv.ativoFinanceiro.portfolio.usuario.id = :usuarioId " +
            "AND rv.ativoFinanceiro.deletado = false")
    Page<RendaVariavel> findByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(
            List<String> tipos,
            Long usuarioId,
            Pageable pageable
    );

    /**
     * Busca todas as rendas variáveis de múltiplos tipos associadas a um usuário.
     */
    @Query("SELECT rv FROM RendaVariavel rv " +
            "WHERE rv.tipoRendaVariavel IN :tipos " +
            "AND rv.ativoFinanceiro.portfolio.usuario.id = :usuarioId " +
            "AND rv.ativoFinanceiro.deletado = false")
    List<RendaVariavel> findByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(
            List<String> tipos,
            Long usuarioId
    );


    @Query("SELECT rv FROM RendaVariavel rv " +
            "WHERE rv.tipoRendaVariavel = :tipo " +
            "AND (:nome IS NULL OR LOWER(rv.ativoFinanceiro.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "AND (:startDate IS NULL OR rv.dataCompra >= :startDate) " +
            "AND (:endDate IS NULL OR rv.dataCompra <= :endDate) " +
            "AND (:precoMedioMin IS NULL OR rv.precoUnitario >= :precoMedioMin) " +
            "AND (:precoMedioMax IS NULL OR rv.precoUnitario <= :precoMedioMax) " +
            "AND rv.ativoFinanceiro.portfolio.usuario.id = :usuarioId")
    Page<RendaVariavel> findByFilters(
            @Param("tipo") String tipo,
            @Param("nome") String nome,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("precoMedioMin") BigDecimal precoMedioMin,
            @Param("precoMedioMax") BigDecimal precoMedioMax,
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );

    // **NOVO**: versão que aceita vários tipos de renda variável
    @Query("SELECT rv FROM RendaVariavel rv " +
            " WHERE rv.tipoRendaVariavel IN :tipos " +
            "   AND (:nome IS NULL OR LOWER(rv.ativoFinanceiro.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "   AND (:startDate IS NULL OR rv.dataCompra >= :startDate) " +
            "   AND (:endDate   IS NULL OR rv.dataCompra <= :endDate) " +
            "   AND (:precoMedioMin IS NULL OR rv.precoUnitario >= :precoMedioMin) " +
            "   AND (:precoMedioMax IS NULL OR rv.precoUnitario <= :precoMedioMax) " +
            "   AND rv.ativoFinanceiro.portfolio.usuario.id = :usuarioId")
    Page<RendaVariavel> findByFiltersIn(
            @Param("tipos") List<String> tipos,
            @Param("nome") String nome,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("precoMedioMin") BigDecimal precoMedioMin,
            @Param("precoMedioMax") BigDecimal precoMedioMax,
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );


    @Query("SELECT COUNT(rv) FROM RendaVariavel rv " +
            "WHERE rv.tipoRendaVariavel = :tipo " +
            "AND (:nome IS NULL OR LOWER(rv.ativoFinanceiro.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "AND (:startDate IS NULL OR rv.dataCompra >= :startDate) " +
            "AND (:endDate IS NULL OR rv.dataCompra <= :endDate) " +
            "AND (:precoMedioMin IS NULL OR rv.precoUnitario >= :precoMedioMin) " +
            "AND (:precoMedioMax IS NULL OR rv.precoUnitario <= :precoMedioMax) " +
            "AND rv.ativoFinanceiro.portfolio.usuario.id = :usuarioId")
    long countByFilters(
            @Param("tipo") String tipo,
            @Param("nome") String nome,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("precoMedioMin") BigDecimal precoMedioMin,
            @Param("precoMedioMax") BigDecimal precoMedioMax,
            @Param("usuarioId") Long usuarioId
    );

    // NOVO: suporta vários tipos
    @Query("SELECT COUNT(rv) FROM RendaVariavel rv " +
            " WHERE rv.tipoRendaVariavel IN :tipos " +
            "   AND (:nome IS NULL OR LOWER(rv.ativoFinanceiro.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "   AND (:startDate IS NULL OR rv.dataCompra >= :startDate) " +
            "   AND (:endDate   IS NULL OR rv.dataCompra <= :endDate) " +
            "   AND (:precoMedioMin IS NULL OR rv.precoUnitario >= :precoMedioMin) " +
            "   AND (:precoMedioMax IS NULL OR rv.precoUnitario <= :precoMedioMax) " +
            "   AND rv.ativoFinanceiro.portfolio.usuario.id = :usuarioId")
    long countByFiltersIn(
            @Param("tipos") List<String> tipos,
            @Param("nome") String nome,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("precoMedioMin") BigDecimal precoMedioMin,
            @Param("precoMedioMax") BigDecimal precoMedioMax,
            @Param("usuarioId") Long usuarioId
    );


    /**
     * Conta a quantidade de registros de RendaVariavel do tipo FII para um usuário específico.
     *
     * @param tipoRendaVariavel Deve ser "FII".
     * @param usuarioId         ID do usuário.
     * @return Número total de registros.
     */
    @Query("SELECT COUNT(rv) FROM RendaVariavel rv WHERE rv.tipoRendaVariavel = :tipoRendaVariavel AND rv.ativoFinanceiro.portfolio.usuario.id = :usuarioId")
    Long countByTipoRendaVariavelAndAtivoFinanceiroUsuarioId(@Param("tipoRendaVariavel") String tipoRendaVariavel,
                                                             @Param("usuarioId") Long usuarioId);


    /**
     * Conta a quantidade de registros de RendaVariavel para vários tipos (e.g. ações)
     * associados ao usuário.
     *
     * @param tipos     Lista de tipos de renda variável (ex.: ["ACAO_ON","ACAO_PN","ACAO_UNIT"])
     * @param usuarioId ID do usuário.
     * @return Número total de registros.
     */
    @Query("SELECT COUNT(rv) " +
            "FROM RendaVariavel rv " +
            "WHERE rv.tipoRendaVariavel IN :tipos " +
            "  AND rv.ativoFinanceiro.portfolio.usuario.id = :usuarioId")
    Long countByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(
            @Param("tipos") List<String> tipos,
            @Param("usuarioId") Long usuarioId);


}
