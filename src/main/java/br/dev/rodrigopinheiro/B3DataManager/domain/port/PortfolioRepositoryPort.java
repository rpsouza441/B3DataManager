package br.dev.rodrigopinheiro.B3DataManager.domain.port;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepositoryPort {
    Portfolio save(Portfolio portfolio);
    Optional<Portfolio> findById(Long id);
    List<Portfolio> findAll();
    List<Portfolio> findByUsuarioId(Long usuarioId);
    void deleteById(Long id);
    boolean existsById(Long id);
}