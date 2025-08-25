package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    /**
     * Busca todas as transações não deletadas associadas a um ativo financeiro.
     */
    List<Transacao> findByAtivoFinanceiroIdAndDeletadoFalse(Long ativoFinanceiroId);


}
