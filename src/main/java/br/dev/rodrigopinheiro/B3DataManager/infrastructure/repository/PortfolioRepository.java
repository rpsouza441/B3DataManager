package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {

    Optional<PortfolioEntity> findByUsuarioId(Long id);

    @Query("SELECT p FROM PortfolioEntity p " +
            "LEFT JOIN FETCH p.ativosFinanceiro af " +
            "LEFT JOIN FETCH af.transacoes " +
            "LEFT JOIN FETCH af.rendaFixas " +
            "LEFT JOIN FETCH af.rendaVariaveis " +
            "LEFT JOIN FETCH p.transacoes " +
            "WHERE p.id = :id")
    Optional<PortfolioEntity> findByIdFetchAssociations(@Param("id") Long id);
}