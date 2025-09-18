package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.AtivoRendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.port.RendaVariavelRepositoryPort;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoRendaVariavelEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.AtivoFinanceiroRepository;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.AtivoFinanceiroMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter que implementa RendaVariavelRepositoryPort usando AtivoFinanceiroRepository.
 * 
 * <p>Este adapter:</p>
 * <ul>
 *   <li>Filtra apenas entidades de renda variável (AtivoRendaVariavelEntity)</li>
 *   <li>Converte entre domain objects e entities usando AtivoFinanceiroMapper</li>
 *   <li>Implementa type safety sem necessidade de casting no domain</li>
 *   <li>Adiciona logging para auditoria e debugging</li>
 * </ul>
 * 
 * @author Rodrigo Pinheiro
 * @since 2.0
 */
@Slf4j
@Component
public class RendaVariavelRepositoryAdapter implements RendaVariavelRepositoryPort {

    private final AtivoFinanceiroRepository ativoFinanceiroRepository;
    private final AtivoFinanceiroMapper ativoFinanceiroMapper;

    public RendaVariavelRepositoryAdapter(AtivoFinanceiroRepository ativoFinanceiroRepository,
                                         AtivoFinanceiroMapper ativoFinanceiroMapper) {
        this.ativoFinanceiroRepository = ativoFinanceiroRepository;
        this.ativoFinanceiroMapper = ativoFinanceiroMapper;
    }

    @Override
    public AtivoRendaVariavel save(AtivoRendaVariavel ativo) {
        if (ativo == null) {
            log.warn("Tentativa de salvar ativo de renda variável nulo");
            throw new IllegalArgumentException("Ativo de renda variável não pode ser nulo");
        }
        
        log.debug("Salvando ativo de renda variável: {}", ativo.getCodigo());
        AtivoFinanceiroEntity entity = ativoFinanceiroMapper.toEntity(ativo);
        AtivoFinanceiroEntity savedEntity = ativoFinanceiroRepository.save(entity);
        
        return (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AtivoRendaVariavel> findById(Long id) {
        if (id == null) {
            log.warn("Tentativa de buscar ativo de renda variável com ID nulo");
            return Optional.empty();
        }
        
        log.debug("Buscando ativo de renda variável por ID: {}", id);
        return ativoFinanceiroRepository.findById(id)
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> !entity.isDeletado())
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity));
    }

    @Override
    public List<AtivoRendaVariavel> findAll() {
        return findAll(false);
    }

    @Override
    public List<AtivoRendaVariavel> findAll(boolean includeDeleted) {
        log.debug("Buscando todos os ativos de renda variável (incluir deletados: {})", includeDeleted);
        
        List<AtivoFinanceiroEntity> entities;
        if (includeDeleted) {
            entities = ativoFinanceiroRepository.findAll();
        } else {
            entities = ativoFinanceiroRepository.findByDeletadoFalse();
        }
        
        return entities.stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            log.warn("Tentativa de deletar ativo de renda variável com ID nulo");
            return;
        }
        
        log.debug("Deletando ativo de renda variável por ID: {}", id);
        ativoFinanceiroRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        
        return ativoFinanceiroRepository.findById(id)
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> !entity.isDeletado())
                .isPresent();
    }

    @Override
    public List<AtivoRendaVariavel> findByPortfolio(Portfolio portfolio) {
        if (portfolio == null || portfolio.getId() == null) {
            log.warn("Tentativa de buscar ativos de renda variável com portfolio nulo");
            return Collections.emptyList();
        }
        
        return findByPortfolioId(portfolio.getId());
    }

    @Override
    public List<AtivoRendaVariavel> findByPortfolioId(Long portfolioId) {
        if (portfolioId == null) {
            log.warn("Tentativa de buscar ativos de renda variável com portfolioId nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável por portfolio ID: {}", portfolioId);
        return ativoFinanceiroRepository.findByPortfolioId(portfolioId)
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity && !entity.isDeletado())
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AtivoRendaVariavel> findByCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            log.warn("Tentativa de buscar ativo de renda variável com código nulo ou vazio");
            return Optional.empty();
        }
        
        log.debug("Buscando ativo de renda variável por código: {}", codigo);
        return ativoFinanceiroRepository.findByCodigo(codigo)
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> !entity.isDeletado())
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .findFirst();
    }

    @Override
    public Optional<AtivoRendaVariavel> findByCodigoAndPortfolioId(String codigo, Long portfolioId) {
        if (codigo == null || codigo.trim().isEmpty() || portfolioId == null) {
            log.warn("Tentativa de buscar ativo de renda variável com código ou portfolioId nulo");
            return Optional.empty();
        }
        
        log.debug("Buscando ativo de renda variável por código: {} e portfolio ID: {}", codigo, portfolioId);
        return ativoFinanceiroRepository.findByCodigoAndPortfolioId(codigo, portfolioId)
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> !entity.isDeletado())
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity));
    }

    @Override
    public List<AtivoRendaVariavel> findByTipoRendaVariavel(TipoAtivoFinanceiroVariavel tipo) {
        if (tipo == null) {
            log.warn("Tentativa de buscar ativos de renda variável com tipo nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável por tipo: {}", tipo);
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> tipo.equals(ativo.getTipoRendaVariavel()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaVariavel> findByTipoRendaVariavelAndPortfolioId(TipoAtivoFinanceiroVariavel tipo, Long portfolioId) {
        if (tipo == null || portfolioId == null) {
            log.warn("Tentativa de buscar ativos de renda variável com tipo ou portfolioId nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável por tipo: {} e portfolio ID: {}", tipo, portfolioId);
        return ativoFinanceiroRepository.findByPortfolioId(portfolioId)
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> !entity.isDeletado()) // Filtrar não deletados
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> tipo.equals(ativo.getTipoRendaVariavel()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaVariavel> findBySetor(String setor) {
        if (setor == null || setor.trim().isEmpty()) {
            log.warn("Tentativa de buscar ativos de renda variável com setor nulo ou vazio");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável por setor: {}", setor);
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> setor.equals(ativo.getSetor()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaVariavel> findBySetorAndPortfolioId(String setor, Long portfolioId) {
        if (setor == null || setor.trim().isEmpty() || portfolioId == null) {
            log.warn("Tentativa de buscar ativos de renda variável com setor ou portfolioId nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável por setor: {} e portfolio ID: {}", setor, portfolioId);
        return ativoFinanceiroRepository.findByPortfolioId(portfolioId)
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> !entity.isDeletado()) // Filtrar não deletados
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> setor.equals(ativo.getSetor()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaVariavel> findByPrecoAtualGreaterThan(BigDecimal precoMinimo) {
        if (precoMinimo == null) {
            log.warn("Tentativa de buscar ativos de renda variável com preço mínimo nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável com preço atual maior que: {}", precoMinimo);
        // TODO: Implementar busca por preço atual quando disponível
        log.warn("Método findByPrecoAtualGreaterThan não implementado - AtivoRendaVariavel não possui precoAtual");
        return Collections.emptyList();
    }

    @Override
    public List<AtivoRendaVariavel> findByPrecoAtualLessThan(BigDecimal precoMaximo) {
        if (precoMaximo == null) {
            log.warn("Tentativa de buscar ativos de renda variável com preço máximo nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável com preço atual menor que: {}", precoMaximo);
        // TODO: Implementar busca por preço atual quando disponível
        log.warn("Método findByPrecoAtualLessThan não implementado - AtivoRendaVariavel não possui precoAtual");
        return Collections.emptyList();
    }

    @Override
    public List<AtivoRendaVariavel> findByPrecoAtualBetween(BigDecimal precoMinimo, BigDecimal precoMaximo) {
        if (precoMinimo == null || precoMaximo == null) {
            log.warn("Tentativa de buscar ativos de renda variável com preços nulos");
            return Collections.emptyList();
        }
        
        if (precoMinimo.compareTo(precoMaximo) > 0) {
            log.warn("Preço mínimo não pode ser maior que preço máximo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável com preço atual entre: {} e {}", precoMinimo, precoMaximo);
        // TODO: Implementar busca por preço atual quando disponível
        log.warn("Método findByPrecoAtualBetween não implementado - AtivoRendaVariavel não possui precoAtual");
        return Collections.emptyList();
    }

    @Override
    public List<AtivoRendaVariavel> findByDividendYieldGreaterThan(BigDecimal dividendYieldMinimo) {
        if (dividendYieldMinimo == null) {
            log.warn("Tentativa de buscar ativos de renda variável com dividend yield mínimo nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável com dividend yield maior que: {}", dividendYieldMinimo);
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> ativo.getDividendYield() != null &&
                        ativo.getDividendYield().compareTo(dividendYieldMinimo) > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoRendaVariavel> findByDataCompra(LocalDate dataCompra) {
        if (dataCompra == null) {
            log.warn("Tentativa de buscar ativos de renda variável com data de compra nula");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável por data de compra: {}", dataCompra);
        // TODO: Implementar busca por data de compra quando disponível
        log.warn("Método findByDataCompra não implementado - AtivoRendaVariavel não possui dataCompra");
        return Collections.emptyList();
    }

    @Override
    public List<AtivoRendaVariavel> findByDataCompraBetween(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            log.warn("Tentativa de buscar ativos de renda variável com datas de compra nulas");
            return Collections.emptyList();
        }
        
        if (dataInicio.isAfter(dataFim)) {
            log.warn("Data de início não pode ser posterior à data de fim");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável com compra entre: {} e {}", dataInicio, dataFim);
        // TODO: Implementar busca por data de compra quando disponível
        log.warn("Método findByDataCompraBetween não implementado - AtivoRendaVariavel não possui dataCompra");
        return Collections.emptyList();
    }

    @Override
    public List<AtivoRendaVariavel> findValorizados() {
        log.debug("Buscando ativos de renda variável valorizados");
        // TODO: Implementar busca por ativos valorizados quando disponível
        log.warn("Método findValorizados não implementado - AtivoRendaVariavel não possui precoAtual/precoMedio");
        return Collections.emptyList();
    }

    @Override
    public List<AtivoRendaVariavel> findDesvalorizados() {
        log.debug("Buscando ativos de renda variável desvalorizados");
        // TODO: Implementar busca por ativos desvalorizados quando disponível
        log.warn("Método findDesvalorizados não implementado - AtivoRendaVariavel não possui precoAtual/precoMedio");
        return Collections.emptyList();
    }

    @Override
    public List<AtivoRendaVariavel> findByVolumeGreaterThan(Long volumeMinimo) {
        if (volumeMinimo == null) {
            log.warn("Tentativa de buscar ativos de renda variável com volume mínimo nulo");
            return Collections.emptyList();
        }
        
        log.debug("Buscando ativos de renda variável com volume maior que: {}", volumeMinimo);
        // TODO: Implementar busca por volume quando disponível
        log.warn("Método findByVolumeGreaterThan não implementado - AtivoRendaVariavel não possui volume");
        return Collections.emptyList();
    }

    @Override
    public boolean existsByCodigoAndPortfolioId(String codigo, Long portfolioId) {
        if (codigo == null || codigo.trim().isEmpty() || portfolioId == null) {
            return false;
        }
        
        return ativoFinanceiroRepository.findByCodigoAndPortfolioId(codigo, portfolioId)
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .isPresent();
    }

    @Override
    public boolean existsByCodigoAndPortfolioIdAndNotDeleted(String codigo, Long portfolioId) {
        if (codigo == null || codigo.trim().isEmpty() || portfolioId == null) {
            return false;
        }
        
        return ativoFinanceiroRepository.findByCodigoAndPortfolioId(codigo, portfolioId)
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> !entity.isDeletado())
                .isPresent();
    }
    
    // ========== IMPLEMENTAÇÃO DOS MÉTODOS ESPECÍFICOS PARA O SERVICE ==========
    
    @Override
    public Long countByTipoRendaVariavelAndAtivoFinanceiroUsuarioId(String tipo, Long usuarioId) {
        log.debug("Contando ativos de renda variável por tipo: {} e usuário: {}", tipo, usuarioId);
        // Implementação simplificada - pode ser otimizada com query específica
        return (long) ativoFinanceiroRepository.findByUsuarioId(usuarioId)
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity && !entity.isDeletado())
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> tipo.equals(ativo.getTipoRendaVariavel().name()))
                .count();
    }
    
    @Override
    public Long countByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(List<String> tipos, Long usuarioId) {
        log.debug("Contando ativos de renda variável por tipos: {} e usuário: {}", tipos, usuarioId);
        // Implementação simplificada - pode ser otimizada com query específica
        return (long) ativoFinanceiroRepository.findByUsuarioId(usuarioId)
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity && !entity.isDeletado())
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> tipos.contains(ativo.getTipoRendaVariavel().name()))
                .count();
    }
    
    @Override
    public long countByFilters(String tipo, String nome, LocalDate startDate, LocalDate endDate,
                              BigDecimal precoMedioMin, BigDecimal precoMedioMax, Long usuarioId) {
        log.debug("Contando ativos com filtros - tipo: {}, nome: {}, usuário: {}", tipo, nome, usuarioId);
        // Implementação simplificada - pode ser otimizada com query específica
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> usuarioId == null || (entity.getPortfolio() != null && 
                        entity.getPortfolio().getUsuario() != null && 
                        entity.getPortfolio().getUsuario().getId().equals(usuarioId)))
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> aplicarFiltros(ativo, tipo, nome, startDate, endDate, precoMedioMin, precoMedioMax))
                .count();
    }
    
    @Override
    public long countByFiltersIn(List<String> tipos, String nome, LocalDate startDate, LocalDate endDate,
                                BigDecimal precoMedioMin, BigDecimal precoMedioMax, Long usuarioId) {
        log.debug("Contando ativos com filtros IN - tipos: {}, nome: {}, usuário: {}", tipos, nome, usuarioId);
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> usuarioId == null || (entity.getPortfolio() != null && 
                        entity.getPortfolio().getUsuario() != null && 
                        entity.getPortfolio().getUsuario().getId().equals(usuarioId)))
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> aplicarFiltrosIn(ativo, tipos, nome, startDate, endDate, precoMedioMin, precoMedioMax))
                .count();
    }
    
    @Override
    public org.springframework.data.domain.Page<AtivoRendaVariavel> findByFilters(String tipo, String nome, LocalDate startDate, LocalDate endDate,
                                                                                  BigDecimal precoMedioMin, BigDecimal precoMedioMax,
                                                                                  Long usuarioId, org.springframework.data.domain.Pageable pageable) {
        log.debug("Buscando ativos com filtros e paginação - tipo: {}, nome: {}, usuário: {}", tipo, nome, usuarioId);
        List<AtivoRendaVariavel> filteredAtivos = ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> usuarioId == null || (entity.getPortfolio() != null && 
                        entity.getPortfolio().getUsuario() != null && 
                        entity.getPortfolio().getUsuario().getId().equals(usuarioId)))
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> aplicarFiltros(ativo, tipo, nome, startDate, endDate, precoMedioMin, precoMedioMax))
                .collect(Collectors.toList());
        
        // Aplicar paginação manualmente
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredAtivos.size());
        List<AtivoRendaVariavel> pageContent = filteredAtivos.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filteredAtivos.size());
    }
    
    @Override
    public org.springframework.data.domain.Page<AtivoRendaVariavel> findByFiltersIn(List<String> tipos, String nome, LocalDate startDate, LocalDate endDate,
                                                                                    BigDecimal precoMedioMin, BigDecimal precoMedioMax,
                                                                                    Long usuarioId, org.springframework.data.domain.Pageable pageable) {
        log.debug("Buscando ativos com filtros IN e paginação - tipos: {}, nome: {}, usuário: {}", tipos, nome, usuarioId);
        List<AtivoRendaVariavel> filteredAtivos = ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> usuarioId == null || (entity.getPortfolio() != null && 
                        entity.getPortfolio().getUsuario() != null && 
                        entity.getPortfolio().getUsuario().getId().equals(usuarioId)))
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> aplicarFiltrosIn(ativo, tipos, nome, startDate, endDate, precoMedioMin, precoMedioMax))
                .collect(Collectors.toList());
        
        // Aplicar paginação manualmente
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredAtivos.size());
        List<AtivoRendaVariavel> pageContent = filteredAtivos.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filteredAtivos.size());
    }
    
    @Override
    public List<AtivoRendaVariavel> findByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(List<String> tipos, Long usuarioId) {
        log.debug("Buscando ativos por tipos: {} e usuário: {}", tipos, usuarioId);
        return ativoFinanceiroRepository.findByDeletadoFalse()
                .stream()
                .filter(entity -> entity instanceof AtivoRendaVariavelEntity)
                .filter(entity -> usuarioId == null || (entity.getPortfolio() != null && 
                        entity.getPortfolio().getUsuario() != null && 
                        entity.getPortfolio().getUsuario().getId().equals(usuarioId)))
                .map(entity -> (AtivoRendaVariavel) ativoFinanceiroMapper.toDomain(entity))
                .filter(ativo -> tipos.contains(ativo.getTipoRendaVariavel().name()))
                .collect(Collectors.toList());
    }
    
    // Métodos auxiliares para aplicar filtros
    private boolean aplicarFiltros(AtivoRendaVariavel ativo, String tipo, String nome, LocalDate startDate, LocalDate endDate,
                                  BigDecimal precoMedioMin, BigDecimal precoMedioMax) {
        if (tipo != null && !tipo.equals(ativo.getTipoRendaVariavel().name())) {
            return false;
        }
        if (nome != null && !ativo.getCodigo().toLowerCase().contains(nome.toLowerCase())) {
            return false;
        }
        // TODO: Implementar filtros por data de compra quando disponível
        // if (startDate != null && ativo.getDataCompra().isBefore(startDate)) {
        //     return false;
        // }
        // if (endDate != null && ativo.getDataCompra().isAfter(endDate)) {
        //     return false;
        // }
        // TODO: Implementar filtros por preço quando disponível
        // if (precoMedioMin != null && ativo.getPrecoAtual().compareTo(precoMedioMin) < 0) {
        //     return false;
        // }
        // if (precoMedioMax != null && ativo.getPrecoAtual().compareTo(precoMedioMax) > 0) {
        //     return false;
        // }
        return true;
    }
    
    private boolean aplicarFiltrosIn(AtivoRendaVariavel ativo, List<String> tipos, String nome, LocalDate startDate, LocalDate endDate,
                                    BigDecimal precoMedioMin, BigDecimal precoMedioMax) {
        if (tipos != null && !tipos.contains(ativo.getTipoRendaVariavel().name())) {
            return false;
        }
        if (nome != null && !ativo.getCodigo().toLowerCase().contains(nome.toLowerCase())) {
            return false;
        }
        // TODO: Implementar filtros por data de compra quando disponível
        // if (startDate != null && ativo.getDataCompra().isBefore(startDate)) {
        //     return false;
        // }
        // if (endDate != null && ativo.getDataCompra().isAfter(endDate)) {
        //     return false;
        // }
        // TODO: Implementar filtros por preço quando disponível
        // if (precoMedioMin != null && ativo.getPrecoAtual().compareTo(precoMedioMin) < 0) {
        //     return false;
        // }
        // if (precoMedioMax != null && ativo.getPrecoAtual().compareTo(precoMedioMax) > 0) {
        //     return false;
        // }
        return true;
    }
}