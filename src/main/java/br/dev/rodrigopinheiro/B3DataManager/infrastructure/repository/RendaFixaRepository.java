package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.RendaFixaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface RendaFixaRepository extends JpaRepository<RendaFixaEntity, Long> {

    /**
     * Busca uma renda fixa de um tipo específico associada a um usuário.
     */
    @Query("SELECT rf FROM RendaFixaEntity rf " +
            "WHERE rf.tipoRendaFixa = :tipoRendaFixa " +
            "AND rf.ativoFinanceiro.portfolio.usuario.id = :usuarioId " +
            "AND rf.ativoFinanceiro.deletado = false")
    Optional<RendaFixaEntity> findByTipoRendaFixaAndAtivoFinanceiroUsuarioId(String tipoRendaFixa, Long usuarioId);

    /**
     * Busca todas as rendas fixas de múltiplos tipos associadas a um usuário.
     */
    @Query("SELECT rf FROM RendaFixaEntity rf " +
            "WHERE rf.tipoRendaFixa IN :tiposRendaFixa " +
            "AND rf.ativoFinanceiro.portfolio.usuario.id = :usuarioId " +
            "AND rf.ativoFinanceiro.deletado = false")
    List<RendaFixaEntity> findByTipoRendaFixaInAndAtivoFinanceiroUsuarioId(List<String> tiposRendaFixa, Long usuarioId);
}
