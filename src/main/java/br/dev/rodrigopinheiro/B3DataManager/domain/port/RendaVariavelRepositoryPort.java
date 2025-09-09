package br.dev.rodrigopinheiro.B3DataManager.domain.port;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaVariavel;

import java.util.List;
import java.util.Optional;

public interface RendaVariavelRepositoryPort {
    RendaVariavel save(RendaVariavel rendaVariavel);
    Optional<RendaVariavel> findById(Long id);
    List<RendaVariavel> findAll();
    List<RendaVariavel> findByAtivoFinanceiroId(Long ativoId);
    void deleteById(Long id);
    boolean existsById(Long id);
}