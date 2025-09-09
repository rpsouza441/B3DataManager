package br.dev.rodrigopinheiro.B3DataManager.domain.port;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Transacao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Port para repositório de transações
 * Define operações usando domain models ao invés de IDs
 */
public interface TransacaoRepositoryPort {
    Transacao save(Transacao transacao);
    Optional<Transacao> findById(Long id);
    List<Transacao> findAll();
    List<Transacao> findByAtivoFinanceiro(AtivoFinanceiro ativo);
    List<Transacao> findByPortfolio(Portfolio portfolio);
    List<Transacao> findByDataOperacao(LocalDate data);
    List<Transacao> findByDataOperacaoBetween(LocalDate dataInicio, LocalDate dataFim);
    List<Transacao> findComprasByAtivoFinanceiro(AtivoFinanceiro ativo);
    void deleteById(Long id);
    boolean existsById(Long id);
}