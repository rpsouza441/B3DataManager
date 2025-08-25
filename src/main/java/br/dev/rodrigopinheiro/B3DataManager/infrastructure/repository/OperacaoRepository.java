package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface OperacaoRepository extends JpaRepository<Operacao, Long>, JpaSpecificationExecutor<Operacao> {

    @Query("SELECT o FROM Operacao o " +
            "WHERE (:entradaSaida IS NULL OR LOWER(o.entradaSaida) = LOWER(:entradaSaida)) " +
            "AND (:startDate IS NULL OR STR_TO_DATE(o.data, '%d/%m/%Y') >= STR_TO_DATE(:startDate, '%d/%m/%Y')) " +
            "AND (:endDate IS NULL OR STR_TO_DATE(o.data, '%d/%m/%Y') <= STR_TO_DATE(:endDate, '%d/%m/%Y')) " +
            "AND (:movimentacao IS NULL OR LOWER(o.movimentacao) LIKE LOWER(CONCAT('%', :movimentacao, '%'))) " +
            "AND (:produto IS NULL OR LOWER(o.produto) LIKE LOWER(CONCAT('%', :produto, '%'))) " +
            "AND (:instituicao IS NULL OR LOWER(o.instituicao) LIKE LOWER(CONCAT('%', :instituicao, '%'))) " +
            "AND (:duplicado IS NULL OR o.duplicado = :duplicado) " +
            "AND (:dimensionado IS NULL OR o.dimensionado = :dimensionado)")
    Page<Operacao> findByFilters(
            @Param("entradaSaida") String entradaSaida,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("movimentacao") String movimentacao,
            @Param("produto") String produto,
            @Param("instituicao") String instituicao,
            @Param("duplicado") Boolean duplicado,
            @Param("dimensionado") Boolean dimensionado,
            Pageable pageable
    );


    @Query("SELECT COUNT(o) FROM Operacao o " +
            "WHERE (:entradaSaida IS NULL OR o.entradaSaida = :entradaSaida) " +
            "AND (:startDate IS NULL OR o.data >= :startDate) " +
            "AND (:endDate IS NULL OR o.data <= :endDate) " +
            "AND (:movimentacao IS NULL OR o.movimentacao LIKE %:movimentacao%) " +
            "AND (:produto IS NULL OR o.produto LIKE %:produto%) " +
            "AND (:instituicao IS NULL OR o.instituicao LIKE %:instituicao%) " +
            "AND (:duplicado IS NULL OR o.duplicado = :duplicado) " +
            "AND (:dimensionado IS NULL OR o.dimensionado = :dimensionado)")
    long countByFilters(
            @Param("entradaSaida") String entradaSaida,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("movimentacao") String movimentacao,
            @Param("produto") String produto,
            @Param("instituicao") String instituicao,
//            @Param("tipoMovimentacao") TipoMovimentacao tipoMovimentacao,
            @Param("duplicado") Boolean duplicado,
            @Param("dimensionado") Boolean dimensionado
    );

    Optional<Operacao> findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuario(
            LocalDate data,
            String movimentacao,
            String produto,
            String instituicao,
            Double quantidade,
            BigDecimal precoUnitario,
            BigDecimal valorOperacao,
            Boolean duplicado,
            Usuario usuario
    );

    List<Operacao> findByDimensionadoAndDuplicado(boolean dimensionado, boolean duplicado);

}
