package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.AtivoFinanceiroRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.AtivoFinanceiroMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.AtivoFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter que implementa o port de repositório de ativos financeiros.
 * 
 * <p>Este adapter é responsável por fazer a ponte entre o domínio da aplicação
 * e a camada de persistência, convertendo objetos de domínio em entidades JPA
 * e vice-versa através do mapper.</p>
 * 
 * <p><strong>Características principais:</strong></p>
 * <ul>
 *   <li>Implementa o padrão Adapter da arquitetura hexagonal</li>
 *   <li>Utiliza mapper para conversão entre domain models e entities</li>
 *   <li>Suporte a soft delete através de flags de deleção</li>
 *   <li>Operações otimizadas por tipo de ativo e portfolio</li>
 *   <li>Validações de entrada para evitar NPE</li>
 * </ul>
 * 
 * <p><strong>Operações suportadas:</strong></p>
 * <ul>
 *   <li>CRUD básico de ativos financeiros</li>
 *   <li>Busca por código, nome e tipo de ativo</li>
 *   <li>Filtragem por portfolio</li>
 *   <li>Verificações de existência com soft delete</li>
 * </ul>
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AtivoFinanceiroRepositoryAdapter implements AtivoFinanceiroRepositoryPort {

    private final AtivoFinanceiroRepository ativoFinanceiroRepository;
    private final AtivoFinanceiroMapper ativoFinanceiroMapper;

    // ========== OPERAÇÕES BÁSICAS ==========
    
    @Override
    public AtivoFinanceiro save(AtivoFinanceiro ativo) {
        if (ativo == null) {
            log.warn("Tentativa de salvar ativo financeiro nulo");
            throw new IllegalArgumentException("Ativo financeiro não pode ser nulo");
        }
        
        try {
            log.debug("Salvando ativo financeiro: {}", ativo.getCodigo());
            AtivoFinanceiroEntity entity = ativoFinanceiroMapper.toEntity(ativo);
            AtivoFinanceiroEntity savedEntity = ativoFinanceiroRepository.save(entity);
            AtivoFinanceiro result = ativoFinanceiroMapper.toDomain(savedEntity);
            log.debug("Ativo financeiro salvo com sucesso: ID {}", result.getId());
            return result;
        } catch (Exception e) {
            log.error("Erro ao salvar ativo financeiro: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar ativo financeiro", e);
        }
    }

    @Override
    public Optional<AtivoFinanceiro> findById(Long id) {
        if (id == null) {
            log.warn("Tentativa de buscar ativo com ID nulo");
            return Optional.empty();
        }
        
        log.debug("Buscando ativo financeiro por ID: {}", id);
        return ativoFinanceiroRepository.findByIdAndDeletadoFalse(id)
                .map(ativoFinanceiroMapper::toDomain);
    }

    @Override
    public List<AtivoFinanceiro> findAll() {
        log.debug("Buscando todos os ativos financeiros não deletados");
        return ativoFinanceiroRepository.findByDeletadoFalse().stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            log.warn("Tentativa de deletar ativo com ID nulo");
            return;
        }
        
        log.debug("Deletando ativo financeiro por ID: {}", id);
        ativoFinanceiroRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return ativoFinanceiroRepository.existsById(id);
    }

    // ========== BUSCA POR PORTFOLIO ==========
    
    @Override
    public List<AtivoFinanceiro> findByPortfolioId(Long portfolioId) {
        if (portfolioId == null) {
            log.warn("Tentativa de buscar ativos com portfolioId nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos financeiros por portfolio ID: {}", portfolioId);
        return ativoFinanceiroRepository.findByPortfolioId(portfolioId).stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoFinanceiro> findByPortfolio(Portfolio portfolio) {
        if (portfolio == null || portfolio.getId() == null) {
            log.warn("Tentativa de buscar ativos com portfolio nulo ou sem ID");
            return Collections.emptyList();
        }
        return findByPortfolioId(portfolio.getId());
    }

    // ========== BUSCA POR NOME ==========
    
    @Override
    public List<AtivoFinanceiro> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            log.warn("Tentativa de buscar ativos com nome nulo ou vazio");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos financeiros por nome: {}", nome);
        return ativoFinanceiroRepository.findAllByNome(nome.trim()).stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNomeAndPortfolioId(String nome, Long portfolioId) {
        if (nome == null || nome.trim().isEmpty() || portfolioId == null) {
            return false;
        }
        return ativoFinanceiroRepository.existsByNomeAndPortfolioId(nome.trim(), portfolioId);
    }

    // ========== BUSCA POR CÓDIGO ==========
    
    @Override
    public Optional<AtivoFinanceiro> findByCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            log.warn("Tentativa de buscar ativo com código nulo ou vazio");
            return Optional.empty();
        }
        
        log.debug("Buscando ativo financeiro por código: {}", codigo);
        return ativoFinanceiroRepository.findByCodigo(codigo.trim())
                .map(ativoFinanceiroMapper::toDomain);
    }

    @Override
    public Optional<AtivoFinanceiro> findByCodigoAndPortfolioId(String codigo, Long portfolioId) {
        if (codigo == null || codigo.trim().isEmpty() || portfolioId == null) {
            log.warn("Tentativa de buscar ativo com código ou portfolioId inválido");
            return Optional.empty();
        }
        
        log.debug("Buscando ativo financeiro por código: {} e portfolio ID: {}", codigo, portfolioId);
        return ativoFinanceiroRepository.findByCodigoAndPortfolioId(codigo.trim(), portfolioId)
                .map(ativoFinanceiroMapper::toDomain);
    }

    @Override
    public boolean existsByCodigoAndPortfolioId(String codigo, Long portfolioId) {
        if (codigo == null || codigo.trim().isEmpty() || portfolioId == null) {
            return false;
        }
        return ativoFinanceiroRepository.existsByCodigoAndPortfolioId(codigo.trim(), portfolioId);
    }

    // ========== BUSCA POR TIPO DE ATIVO ==========
    
    @Override
    public List<AtivoFinanceiro> findByTipoAtivo(TipoAtivo tipoAtivo) {
        if (tipoAtivo == null) {
            log.warn("Tentativa de buscar ativos com tipo nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos financeiros por tipo: {}", tipoAtivo);
        return ativoFinanceiroRepository.findByDeletadoFalse().stream()
                .filter(entity -> tipoAtivo.equals(entity.getTipoAtivo()))
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoFinanceiro> findByTipoAtivoAndPortfolioId(TipoAtivo tipoAtivo, Long portfolioId) {
        if (tipoAtivo == null || portfolioId == null) {
            log.warn("Tentativa de buscar ativos com tipo ou portfolioId nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos financeiros por tipo: {} e portfolio ID: {}", tipoAtivo, portfolioId);
        return ativoFinanceiroRepository.findByTipoAtivoAndPortfolioIdAndDeletadoFalse(tipoAtivo, portfolioId).stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoFinanceiro> findAtivosRendaVariavel(Long portfolioId) {
        if (portfolioId == null) {
            log.warn("Tentativa de buscar ativos de renda variável com portfolioId nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável para portfolio ID: {}", portfolioId);
        return ativoFinanceiroRepository.findAtivosRendaVariavel(portfolioId).stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoFinanceiro> findAtivosRendaFixa(Long portfolioId) {
        if (portfolioId == null) {
            log.warn("Tentativa de buscar ativos de renda fixa com portfolioId nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda fixa para portfolio ID: {}", portfolioId);
        return ativoFinanceiroRepository.findAtivosRendaFixa(portfolioId).stream()
                .map(ativoFinanceiroMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ========== VERIFICAÇÕES DE EXISTÊNCIA ==========
    
    @Override
    public boolean existsByCodigoAndPortfolio(String codigo, Portfolio portfolio) {
        if (codigo == null || codigo.trim().isEmpty() || portfolio == null || portfolio.getId() == null) {
            return false;
        }
        return existsByCodigoAndPortfolioId(codigo.trim(), portfolio.getId());
    }

    @Override
    public boolean existsByCodigoAndPortfolioIdAndNotDeleted(String codigo, Long portfolioId) {
        if (codigo == null || codigo.trim().isEmpty() || portfolioId == null) {
            return false;
        }
        return ativoFinanceiroRepository.existsByCodigoAndPortfolioIdAndDeletadoFalse(codigo.trim(), portfolioId);
    }
}