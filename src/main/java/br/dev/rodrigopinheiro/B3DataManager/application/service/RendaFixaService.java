package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoRendaFixaEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.AtivoFinanceiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RendaFixaService {

    private final AtivoFinanceiroRepository ativoFinanceiroRepository;

    @Autowired
    public RendaFixaService(AtivoFinanceiroRepository ativoFinanceiroRepository) {
        this.ativoFinanceiroRepository = ativoFinanceiroRepository;
    }

    public AtivoRendaFixaEntity save(AtivoRendaFixaEntity rendaFixa) {
        // Aqui você pode incluir regras de negócio ou validações específicas
        return (AtivoRendaFixaEntity) ativoFinanceiroRepository.save(rendaFixa);
    }

    public Optional<AtivoRendaFixaEntity> findById(Long id) {
        return ativoFinanceiroRepository.findById(id)
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixaEntity) entity);
    }

    public List<AtivoRendaFixaEntity> findAll() {
        return ativoFinanceiroRepository.findAll()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixaEntity) entity)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        ativoFinanceiroRepository.deleteById(id);
    }

    // Outros métodos de negócio, como cálculos de rendimento, podem ser implementados aqui
}
