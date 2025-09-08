package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.TransacaoEntity;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<TransacaoEntity, Long> {

    /**
     * Busca todas as transações não deletadas associadas a um ativo financeiro.
     */
    List<TransacaoEntity> findByAtivoFinanceiroIdAndDeletadoFalse(Long ativoFinanceiroId);


}
