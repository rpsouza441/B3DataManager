package br.dev.rodrigopinheiro.B3DataManager.domain.port;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaFixa;

import java.util.List;
import java.util.Optional;

public interface RendaFixaRepositoryPort {
    RendaFixa save(RendaFixa rendaFixa);
    Optional<RendaFixa> findById(Long id);
    List<RendaFixa> findAll();
    List<RendaFixa> findByAtivoFinanceiroId(Long ativoId);
    void deleteById(Long id);
    boolean existsById(Long id);
}