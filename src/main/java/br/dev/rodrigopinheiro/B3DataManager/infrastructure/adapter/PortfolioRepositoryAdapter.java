package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.PortfolioRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.PortfolioMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter que implementa o port de repositório de portfolios.
 * 
 * <p>Este adapter é responsável por fazer a ponte entre o domínio da aplicação
 * e a camada de persistência para operações relacionadas a portfolios de investimento.</p>
 * 
 * <p><strong>Características principais:</strong></p>
 * <ul>
 *   <li>Implementa o padrão Adapter da arquitetura hexagonal</li>
 *   <li>Utiliza mapper para conversão entre domain models e entities</li>
 *   <li>Operações CRUD completas para portfolios</li>
 *   <li>Busca otimizada por usuário</li>
 *   <li>Validações de entrada para evitar NPE</li>
 * </ul>
 * 
 * <p><strong>Operações suportadas:</strong></p>
 * <ul>
 *   <li>CRUD básico de portfolios</li>
 *   <li>Busca por usuário proprietário</li>
 *   <li>Verificações de existência</li>
 * </ul>
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PortfolioRepositoryAdapter implements PortfolioRepositoryPort {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;

    @Override
    public Portfolio save(Portfolio portfolio) {
        if (portfolio == null) {
            log.warn("Tentativa de salvar portfolio nulo");
            throw new IllegalArgumentException("Portfolio não pode ser nulo");
        }
        
        try {
            log.debug("Salvando portfolio");
            PortfolioEntity entity = portfolioMapper.toEntity(portfolio);
            PortfolioEntity savedEntity = portfolioRepository.save(entity);
            Portfolio result = portfolioMapper.toDomain(savedEntity);
            log.debug("Portfolio salvo com sucesso: ID {}", result.getId());
            return result;
        } catch (Exception e) {
            log.error("Erro ao salvar portfolio: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar portfolio", e);
        }
    }

    @Override
    public Optional<Portfolio> findById(Long id) {
        if (id == null) {
            log.warn("Tentativa de buscar portfolio com ID nulo");
            return Optional.empty();
        }
        
        log.debug("Buscando portfolio por ID: {}", id);
        return portfolioRepository.findById(id)
                .map(portfolioMapper::toDomain);
    }

    @Override
    public List<Portfolio> findAll() {
        log.debug("Buscando todos os portfolios");
        return portfolioRepository.findAll().stream()
                .map(portfolioMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Portfolio> findByUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            log.warn("Tentativa de buscar portfolios com usuarioId nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando portfolios por usuário ID: {}", usuarioId);
        return portfolioRepository.findByUsuarioId(usuarioId)
                .map(portfolioMapper::toDomain)
                .map(List::of)
                .orElse(Collections.emptyList());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            log.warn("Tentativa de deletar portfolio com ID nulo");
            return;
        }
        
        log.debug("Deletando portfolio por ID: {}", id);
        portfolioRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return portfolioRepository.existsById(id);
    }


}