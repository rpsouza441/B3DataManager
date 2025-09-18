package br.dev.rodrigopinheiro.B3DataManager.infrastructure.adapter;

import br.dev.rodrigopinheiro.B3DataManager.application.criteria.FilterCriteria;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.OperacaoMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.JpaOperacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter que implementa o port de repositório de operações.
 * 
 * <p>Este adapter é responsável por fazer a ponte entre o domínio da aplicação
 * e a camada de persistência para operações relacionadas a operações financeiras.</p>
 * 
 * <p><strong>Características principais:</strong></p>
 * <ul>
 *   <li>Implementa o padrão Adapter da arquitetura hexagonal</li>
 *   <li>Utiliza mapper para conversão entre domain models e entities</li>
 *   <li>Operações CRUD completas para operações</li>
 *   <li>Buscas especializadas com filtros e paginação</li>
 *   <li>Verificações de duplicação e dimensionamento</li>
 * </ul>
 * 
 * <p><strong>Operações suportadas:</strong></p>
 * <ul>
 *   <li>CRUD básico de operações</li>
 *   <li>Busca com filtros complexos e paginação</li>
 *   <li>Verificação de existência por ID original</li>
 *   <li>Busca de duplicatas e operações dimensionadas</li>
 *   <li>Contagem de registros com filtros</li>
 * </ul>
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
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
        OperacaoEntity jpaEntity = mapper.toEntity(operacao);
        OperacaoEntity savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Operacao> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByIdOriginalAndUsuarioId(Long idOriginal, UsuarioId usuarioId) {
        return jpaRepository.existsByIdOriginalAndUsuario_Id(idOriginal, usuarioId.value());
    }
    
    @Override
    public Optional<Operacao> findByIdOriginalAndUsuarioId(Long idOriginal, UsuarioId usuarioId) {
        return jpaRepository.findByIdOriginalAndUsuario_Id(idOriginal, usuarioId.value())
                .map(mapper::toDomain);
    }
    
    @Override
    public Page<Operacao> findByFiltersAndUsuarioId(FilterCriteria criteria, UsuarioId usuarioId, Pageable pageable) {
        Page<OperacaoEntity> jpaPage = jpaRepository.findByFiltersAndUsuarioId(
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
        
        return jpaPage.map(mapper::toDomain);
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
        
        return jpaRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuario_Id(
                data, movimentacao, produto, instituicao, quantidade, precoUnitario, valorOperacao, duplicado, usuarioId.value()
        ).map(mapper::toDomain);
    }
    
    @Override
    public java.util.List<Operacao> findByDimensionadoAndDuplicadoWithPagination(
            boolean dimensionado, boolean duplicado, int pageSize, int offset) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(offset / pageSize, pageSize, org.springframework.data.domain.Sort.by("id").ascending());
        Page<OperacaoEntity> page = jpaRepository.findByDimensionadoAndDuplicado(dimensionado, duplicado, pageable);
        
        return page.getContent().stream()
                .map(mapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public long countByDimensionadoAndDuplicado(boolean dimensionado, boolean duplicado) {
        return jpaRepository.countByDimensionadoAndDuplicado(dimensionado, duplicado);
    }
}