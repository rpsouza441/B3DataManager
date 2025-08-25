package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByUsuarioId(Long id);

    @Query("SELECT p FROM Portfolio p " +
            "LEFT JOIN FETCH p.ativosFinanceiro af " +
            "LEFT JOIN FETCH af.transacoes " +
            "LEFT JOIN FETCH af.rendaFixas " +
            "LEFT JOIN FETCH af.rendaVariaveis " +
            "LEFT JOIN FETCH p.transacoes " +
            "WHERE p.id = :id")
    Optional<Portfolio> findByIdFetchAssociations(@Param("id") Long id);
}