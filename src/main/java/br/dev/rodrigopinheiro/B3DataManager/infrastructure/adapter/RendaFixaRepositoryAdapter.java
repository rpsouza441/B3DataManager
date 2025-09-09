package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaFixaRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.RendaFixaMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.RendaFixaEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.RendaFixaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RendaFixaRepositoryAdapter implements RendaFixaRepositoryPort {

    @Autowired
    private RendaFixaRepository rendaFixaRepository;

    @Autowired
    private RendaFixaMapper rendaFixaMapper;

    @Override
    public RendaFixa save(RendaFixa rendaFixa) {
        RendaFixaEntity entity = rendaFixaMapper.toEntity(rendaFixa);
        RendaFixaEntity savedEntity = rendaFixaRepository.save(entity);
        return rendaFixaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RendaFixa> findById(Long id) {
        return rendaFixaRepository.findById(id)
                .map(rendaFixaMapper::toDomain);
    }

    @Override
    public List<RendaFixa> findAll() {
        return rendaFixaRepository.findAll().stream()
                .map(rendaFixaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RendaFixa> findByAtivoFinanceiroId(Long ativoId) {
        return rendaFixaRepository.findByAtivoFinanceiroId(ativoId).stream()
                .map(rendaFixaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        rendaFixaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return rendaFixaRepository.existsById(id);
    }


}