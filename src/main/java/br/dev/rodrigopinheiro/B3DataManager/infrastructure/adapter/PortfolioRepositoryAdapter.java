package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.PortfolioRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.PortfolioMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PortfolioRepositoryAdapter implements PortfolioRepositoryPort {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private PortfolioMapper portfolioMapper;

    @Override
    public Portfolio save(Portfolio portfolio) {
        PortfolioEntity entity = portfolioMapper.toEntity(portfolio);
        PortfolioEntity savedEntity = portfolioRepository.save(entity);
        return portfolioMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Portfolio> findById(Long id) {
        return portfolioRepository.findById(id)
                .map(portfolioMapper::toDomain);
    }

    @Override
    public List<Portfolio> findAll() {
        return portfolioRepository.findAll().stream()
                .map(portfolioMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Portfolio> findByUsuarioId(Long usuarioId) {
        return portfolioRepository.findByUsuarioId(usuarioId)
                .map(portfolioMapper::toDomain)
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public void deleteById(Long id) {
        portfolioRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return portfolioRepository.existsById(id);
    }


}