package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.RendaFixaEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.RendaFixaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RendaFixaService {

    private final RendaFixaRepository rendaFixaRepository;

    @Autowired
    public RendaFixaService(RendaFixaRepository rendaFixaRepository) {
        this.rendaFixaRepository = rendaFixaRepository;
    }

    public RendaFixaEntity save(RendaFixaEntity rendaFixa) {
        // Aqui você pode incluir regras de negócio ou validações específicas
        return rendaFixaRepository.save(rendaFixa);
    }

    public Optional<RendaFixaEntity> findById(Long id) {
        return rendaFixaRepository.findById(id);
    }

    public List<RendaFixaEntity> findAll() {
        return rendaFixaRepository.findAll();
    }

    public void delete(Long id) {
        rendaFixaRepository.deleteById(id);
    }

    // Outros métodos de negócio, como cálculos de rendimento, podem ser implementados aqui
}
