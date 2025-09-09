package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.AtivoFinanceiroMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.AtivoFinanceiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AtivoFinanceiroRepositoryAdapter implements AtivoFinanceiroRepositoryPort {

    @Autowired
    private AtivoFinanceiroRepository ativoFinanceiroRepository;

    @Autowired
    private AtivoFinanceiroMapper ativoFinanceiroMapper;

    @Override
    public AtivoFinanceiro save(AtivoFinanceiro ativo) {
        AtivoFinanceiroEntity entity = ativoFinanceiroMapper.toEntity(ativo);
        AtivoFinanceiroEntity savedEntity = ativoFinanceiroRepository.save(entity);
        return ativoFinanceiroMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AtivoFinanceiro> findById(Long id) {
        return ativoFinanceiroRepository.findByIdAndDeletadoFalse(id)
                .map(ativoFinanceiroMapper::toDomain);
    }

    @Override
    public List<AtivoFinanceiro> findAll() {
        return ativoFinanceiroRepository.findByDeletadoFalse().stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoFinanceiro> findByPortfolioId(Long portfolioId) {
        return ativoFinanceiroRepository.findByPortfolioId(portfolioId).stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoFinanceiro> findByPortfolio(br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio portfolio) {
        if (portfolio == null || portfolio.getId() == null) {
            return List.of();
        }
        return findByPortfolioId(portfolio.getId());
    }

    @Override
    public List<AtivoFinanceiro> findByNome(String nome) {
        return ativoFinanceiroRepository.findAllByNome(nome).stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        ativoFinanceiroRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return ativoFinanceiroRepository.existsById(id);
    }

    @Override
    public boolean existsByNomeAndPortfolioId(String nome, Long portfolioId) {
        return ativoFinanceiroRepository.existsByNomeAndPortfolioId(nome, portfolioId);
    }

    // ========== IMPLEMENTAÇÃO DOS MÉTODOS DO PORT ==========

    @Override
    public Optional<AtivoFinanceiro> findByCodigo(String codigo) {
        return ativoFinanceiroRepository.findByCodigo(codigo)
                .map(ativoFinanceiroMapper::toDomain);
    }

    @Override
    public Optional<AtivoFinanceiro> findByCodigoAndPortfolioId(String codigo, Long portfolioId) {
        return ativoFinanceiroRepository.findByCodigoAndPortfolioId(codigo, portfolioId)
                .map(ativoFinanceiroMapper::toDomain);
    }

    @Override
    public boolean existsByCodigoAndPortfolioId(String codigo, Long portfolioId) {
        return ativoFinanceiroRepository.existsByCodigoAndPortfolioId(codigo, portfolioId);
    }

    @Override
    public List<AtivoFinanceiro> findByTipoAtivo(TipoAtivo tipoAtivo) {
        // Busca em todos os portfolios - pode ser otimizado se necessário
        return ativoFinanceiroRepository.findByDeletadoFalse().stream()
                .filter(entity -> tipoAtivo.equals(entity.getTipoAtivo()))
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoFinanceiro> findByTipoAtivoAndPortfolioId(TipoAtivo tipoAtivo, Long portfolioId) {
        return ativoFinanceiroRepository.findByTipoAtivoAndPortfolioIdAndDeletadoFalse(tipoAtivo, portfolioId).stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoFinanceiro> findAtivosRendaVariavel(Long portfolioId) {
        return ativoFinanceiroRepository.findAtivosRendaVariavel(portfolioId).stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoFinanceiro> findAtivosRendaFixa(Long portfolioId) {
        return ativoFinanceiroRepository.findAtivosRendaFixa(portfolioId).stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCodigoAndPortfolio(String codigo, br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio portfolio) {
        if (portfolio == null || portfolio.getId() == null) {
            return false;
        }
        return existsByCodigoAndPortfolioId(codigo, portfolio.getId());
    }

    @Override
    public boolean existsByCodigoAndPortfolioIdAndNotDeleted(String codigo, Long portfolioId) {
        return ativoFinanceiroRepository.existsByCodigoAndPortfolioIdAndDeletadoFalse(codigo, portfolioId);
    }

}