package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivo;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoRendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaFixaRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.AtivoFinanceiroMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoRendaFixaEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.AtivoFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter específico para repositório de ativos de renda fixa.
 * 
 * <p>Este adapter implementa o padrão Adapter da arquitetura hexagonal,
 * fornecendo operações específicas e type-safe para ativos de renda fixa.</p>
 * 
 * <p><strong>Vantagens desta implementação:</strong></p>
 * <ul>
 *   <li>Type safety completo - sem castings desnecessários</li>
 *   <li>Métodos específicos para renda fixa</li>
 *   <li>Reutilização do repositório e mapper existentes</li>
 *   <li>Validações específicas para o domínio de renda fixa</li>
 *   <li>Logs específicos para operações de renda fixa</li>
 * </ul>
 * 
 * <p><strong>Operações suportadas:</strong></p>
 * <ul>
 *   <li>CRUD básico de ativos de renda fixa</li>
 *   <li>Busca por tipo de renda fixa</li>
 *   <li>Busca por data de vencimento</li>
 *   <li>Busca por taxa de juros</li>
 *   <li>Identificação de ativos vencidos</li>
 * </ul>
 * 
 * @author Rodrigo Pinheiro
 * @since 2.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RendaFixaRepositoryAdapter implements RendaFixaRepositoryPort {

    private final AtivoFinanceiroRepository ativoFinanceiroRepository;
    private final AtivoFinanceiroMapper ativoFinanceiroMapper;

    // ========== OPERAÇÕES BÁSICAS ==========
    
    @Override
    public AtivoRendaFixa save(AtivoRendaFixa ativo) {
        if (ativo == null) {
            log.warn("Tentativa de salvar ativo de renda fixa nulo");
            throw new IllegalArgumentException("Ativo de renda fixa não pode ser nulo");
        }
        
        try {
            log.debug("Salvando ativo de renda fixa: {}", ativo.getCodigo());
            AtivoFinanceiroEntity entity = ativoFinanceiroMapper.toEntity(ativo);
            AtivoFinanceiroEntity savedEntity = ativoFinanceiroRepository.save(entity);
            AtivoRendaFixa result = (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(savedEntity);
            log.debug("Ativo de renda fixa salvo com sucesso: ID {}", result.getId());
            return result;
        } catch (Exception e) {
            log.error("Erro ao salvar ativo de renda fixa: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar ativo de renda fixa", e);
        }
    }

    @Override
    public Optional<AtivoRendaFixa> findById(Long id) {
        if (id == null) {
            log.warn("Tentativa de buscar ativo de renda fixa com ID nulo");
            return Optional.empty();
        }
        
        log.debug("Buscando ativo de renda fixa por ID: {}", id);
        return ativoFinanceiroRepository.findByIdAndDeletadoFalse(id)
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity));
    }

    @Override
    public List<AtivoRendaFixa> findAll() {
        return findAll(false);
    }

    @Override
    public List<AtivoRendaFixa> findAll(boolean includeDeleted) {
        log.debug("Buscando todos os ativos de renda fixa (incluir deletados: {})", includeDeleted);
        
        List<AtivoFinanceiroEntity> entities;
        if (includeDeleted) {
            entities = ativoFinanceiroRepository.findAll();
        } else {
            entities = ativoFinanceiroRepository.findByDeletadoFalse();
        }
        
        return entities.stream()
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            log.warn("Tentativa de deletar ativo de renda fixa com ID nulo");
            return;
        }
        
        log.debug("Deletando ativo de renda fixa por ID: {}", id);
        Optional<AtivoFinanceiroEntity> entity = ativoFinanceiroRepository.findByIdAndDeletadoFalse(id);
        if (entity.isPresent() && entity.get() instanceof AtivoRendaFixaEntity) {
            entity.get().setDeletado(true);
            ativoFinanceiroRepository.save(entity.get());
            log.debug("Ativo de renda fixa deletado com sucesso: ID {}", id);
        } else {
            log.warn("Ativo de renda fixa não encontrado para deleção: ID {}", id);
        }
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        
        return ativoFinanceiroRepository.findByIdAndDeletadoFalse(id)
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .isPresent();
    }

    // ========== OPERAÇÕES POR PORTFOLIO ==========
    
    @Override
    public List<AtivoRendaFixa> findByPortfolio(Portfolio portfolio) {
        if (portfolio == null || portfolio.getId() == null) {
            log.warn("Tentativa de buscar ativos de renda fixa com portfolio nulo");
            return Collections.emptyList();
        }
        
        return findByPortfolioId(portfolio.getId());
    }

    @Override
    public List<AtivoRendaFixa> findByPortfolioId(Long portfolioId) {
        if (portfolioId == null) {
            log.warn("Tentativa de buscar ativos de renda fixa com portfolioId nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda fixa por portfolio ID: {}", portfolioId);
        return ativoFinanceiroRepository.findByTipoAtivoAndPortfolioIdAndDeletadoFalse(TipoAtivo.RENDA_FIXA, portfolioId)
                .stream()
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    // ========== OPERAÇÕES POR CÓDIGO ==========
    
    @Override
    public Optional<AtivoRendaFixa> findByCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            log.warn("Tentativa de buscar ativo de renda fixa com código nulo ou vazio");
            return Optional.empty();
        }
        
        log.debug("Buscando ativo de renda fixa por código: {}", codigo);
        return ativoFinanceiroRepository.findByCodigo(codigo)
                .filter(entity -> entity instanceof AtivoRendaFixaEntity && !entity.isDeletado())
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity));
    }

    @Override
    public Optional<AtivoRendaFixa> findByCodigoAndPortfolioId(String codigo, Long portfolioId) {
        if (codigo == null || codigo.trim().isEmpty() || portfolioId == null) {
            log.warn("Tentativa de buscar ativo de renda fixa com código ou portfolioId nulo");
            return Optional.empty();
        }
        
        log.debug("Buscando ativo de renda fixa por código: {} e portfolio ID: {}", codigo, portfolioId);
        return ativoFinanceiroRepository.findByCodigoAndPortfolioId(codigo, portfolioId)
                .filter(entity -> entity instanceof AtivoRendaFixaEntity && !entity.isDeletado())
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity));
    }

    // ========== OPERAÇÕES ESPECÍFICAS DE RENDA FIXA ==========
    
    @Override
    public List<AtivoRendaFixa> findByTipoRendaFixa(TipoAtivoFinanceiroFixa tipo) {
        if (tipo == null) {
            log.warn("Tentativa de buscar ativos de renda fixa com tipo nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda fixa por tipo: {}", tipo);
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> tipo.equals(ativo.getTipoRendaFixa()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaFixa> findByTipoRendaFixaAndPortfolioId(TipoAtivoFinanceiroFixa tipo, Long portfolioId) {
        if (tipo == null || portfolioId == null) {
            log.warn("Tentativa de buscar ativos de renda fixa com tipo ou portfolioId nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda fixa por tipo: {} e portfolio ID: {}", tipo, portfolioId);
        return ativoFinanceiroRepository.findByTipoAtivoAndPortfolioIdAndDeletadoFalse(TipoAtivo.RENDA_FIXA, portfolioId)
                .stream()
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> tipo.equals(ativo.getTipoRendaFixa()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaFixa> findByDataVencimento(LocalDate dataVencimento) {
        if (dataVencimento == null) {
            log.warn("Tentativa de buscar ativos de renda fixa com data de vencimento nula");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda fixa por data de vencimento: {}", dataVencimento);
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> dataVencimento.equals(ativo.getDataVencimento()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaFixa> findByDataVencimentoBetween(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            log.warn("Tentativa de buscar ativos de renda fixa com datas de vencimento nulas");
            return Collections.emptyList();
        }
        
        if (dataInicio.isAfter(dataFim)) {
            log.warn("Data de início não pode ser posterior à data de fim");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda fixa com vencimento entre: {} e {}", dataInicio, dataFim);
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> ativo.getDataVencimento() != null &&
                        !ativo.getDataVencimento().isBefore(dataInicio) &&
                        !ativo.getDataVencimento().isAfter(dataFim))
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaFixa> findByTaxaJurosGreaterThan(BigDecimal taxaMinima) {
        if (taxaMinima == null) {
            log.warn("Tentativa de buscar ativos de renda fixa com taxa mínima nula");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda fixa com taxa de juros maior que: {}", taxaMinima);
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> ativo.getTaxaJuros() != null &&
                        ativo.getTaxaJuros().compareTo(taxaMinima) > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaFixa> findVencidos() {
        LocalDate hoje = LocalDate.now();
        log.debug("Buscando ativos de renda fixa vencidos (data < {})", hoje);
        
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> ativo.getDataVencimento() != null &&
                        ativo.getDataVencimento().isBefore(hoje))
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaFixa> findVencendoEm(int dias) {
        if (dias < 0) {
            log.warn("Número de dias não pode ser negativo: {}", dias);
            return Collections.emptyList();
        }
        
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        log.debug("Buscando ativos de renda fixa vencendo em {} dias (até {})", dias, dataLimite);
        
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .map(entity -> (AtivoRendaFixa) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> ativo.getDataVencimento() != null &&
                        !ativo.getDataVencimento().isBefore(LocalDate.now()) &&
                        !ativo.getDataVencimento().isAfter(dataLimite))
                .collect(Collectors.toList());
    }

    // ========== OPERAÇÕES DE VERIFICAÇÃO ==========
    
    @Override
    public boolean existsByCodigoAndPortfolioId(String codigo, Long portfolioId) {
        if (codigo == null || codigo.trim().isEmpty() || portfolioId == null) {
            return false;
        }
        
        return ativoFinanceiroRepository.findByCodigoAndPortfolioId(codigo, portfolioId)
                .filter(entity -> entity instanceof AtivoRendaFixaEntity)
                .filter(entity -> !entity.isDeletado())
                .isPresent();
    }

    @Override
    public boolean existsByCodigoAndPortfolioIdAndNotDeleted(String codigo, Long portfolioId) {
        return existsByCodigoAndPortfolioId(codigo, portfolioId);
    }
}