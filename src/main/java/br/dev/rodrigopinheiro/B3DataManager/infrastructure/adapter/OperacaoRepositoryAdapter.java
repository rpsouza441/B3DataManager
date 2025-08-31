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
}