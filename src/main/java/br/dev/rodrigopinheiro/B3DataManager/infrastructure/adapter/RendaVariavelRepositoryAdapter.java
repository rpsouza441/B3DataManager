package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaVariavelRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.RendaVariavelMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.RendaVariavelEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.RendaVariavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RendaVariavelRepositoryAdapter implements RendaVariavelRepositoryPort {

    @Autowired
    private RendaVariavelRepository rendaVariavelRepository;

    @Autowired
    private RendaVariavelMapper rendaVariavelMapper;

    @Override
    public RendaVariavel save(RendaVariavel rendaVariavel) {
        RendaVariavelEntity entity = rendaVariavelMapper.toEntity(rendaVariavel);
        RendaVariavelEntity savedEntity = rendaVariavelRepository.save(entity);
        return rendaVariavelMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RendaVariavel> findById(Long id) {
        return rendaVariavelRepository.findById(id)
                .map(rendaVariavelMapper::toDomain);
    }

    @Override
    public List<RendaVariavel> findAll() {
        return rendaVariavelRepository.findAll().stream()
                .map(rendaVariavelMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RendaVariavel> findByAtivoFinanceiroId(Long ativoId) {
        return rendaVariavelRepository.findByAtivoFinanceiroId(ativoId).stream()
                .map(rendaVariavelMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        rendaVariavelRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return rendaVariavelRepository.existsById(id);
    }


}