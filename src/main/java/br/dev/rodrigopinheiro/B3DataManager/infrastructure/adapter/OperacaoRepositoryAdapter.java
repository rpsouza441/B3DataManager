package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.application.criteria.FilterCriteria;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.entity.OperacaoJpaEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.OperacaoMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.JpaOperacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador que implementa a port OperacaoRepository.
 * Faz a ponte entre a camada de aplicação e a infraestrutura JPA.
 */
@Component
public class OperacaoRepositoryAdapter implements OperacaoRepository {
    
    private final JpaOperacaoRepository jpaRepository;
    private final OperacaoMapper mapper;
    
    public OperacaoRepositoryAdapter(JpaOperacaoRepository jpaRepository, OperacaoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Operacao save(Operacao operacao) {
        OperacaoJpaEntity jpaEntity = mapper.toJpaEntity(operacao);
        OperacaoJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<Operacao> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomainEntity);
    }
    
    @Override
    public boolean existsByIdOriginalAndUsuarioId(Long idOriginal, UsuarioId usuarioId) {
        return jpaRepository.existsByIdOriginalAndUsuarioId(idOriginal, usuarioId.value());
    }
    
    @Override
    public Optional<Operacao> findByIdOriginalAndUsuarioId(Long idOriginal, UsuarioId usuarioId) {
        return jpaRepository.findByIdOriginalAndUsuarioId(idOriginal, usuarioId.value())
                .map(mapper::toDomainEntity);
    }
    
    @Override
    public Page<Operacao> findByFiltersAndUsuarioId(FilterCriteria criteria, UsuarioId usuarioId, Pageable pageable) {
        Page<OperacaoJpaEntity> jpaPage = jpaRepository.findByFiltersAndUsuarioId(
            criteria.entradaSaida(),
            criteria.startDate(),
            criteria.endDate(),
            criteria.movimentacao(),
            criteria.produto(),
            criteria.instituicao(),
            criteria.duplicado(),
            criteria.dimensionado(),
            usuarioId.value(),
            pageable
        );
        
        return jpaPage.map(mapper::toDomainEntity);
    }
    
    @Override
    public long countByFiltersAndUsuarioId(FilterCriteria criteria, UsuarioId usuarioId) {
        return jpaRepository.countByFiltersAndUsuarioId(
            criteria.entradaSaida(),
            criteria.startDate(),
            criteria.endDate(),
            criteria.movimentacao(),
            criteria.produto(),
            criteria.instituicao(),
            criteria.duplicado(),
            criteria.dimensionado(),
            usuarioId.value()
        );
    }
    
    @Override
    public Optional<Operacao> findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
            java.time.LocalDate data,
            String movimentacao,
            String produto,
            String instituicao,
            java.math.BigDecimal quantidade,
            java.math.BigDecimal precoUnitario,
            java.math.BigDecimal valorOperacao,
            boolean duplicado,
            UsuarioId usuarioId) {
        
        return jpaRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                data, movimentacao, produto, instituicao, quantidade, precoUnitario, valorOperacao, duplicado, usuarioId.value()
        ).map(mapper::toDomainEntity);
    }
    
    @Override
    public java.util.List<Operacao> findByDimensionadoAndDuplicadoWithPagination(
            boolean dimensionado, boolean duplicado, int pageSize, int offset) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(offset / pageSize, pageSize, org.springframework.data.domain.Sort.by("id").ascending());
        Page<OperacaoJpaEntity> page = jpaRepository.findByDimensionadoAndDuplicado(dimensionado, duplicado, pageable);
        
        return page.getContent().stream()
                .map(mapper::toDomainEntity)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public long countByDimensionadoAndDuplicado(boolean dimensionado, boolean duplicado) {
        return jpaRepository.countByDimensionadoAndDuplicado(dimensionado, duplicado);
    }
}