package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.entity.OperacaoJpaEntity;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Mapper responsável por converter entre entidades de domínio e JPA.
 * Isola as conversões e evita vazamento de entidades JPA para camadas superiores.
 */
@Component
public class OperacaoMapper {
    
    /**
     * Converte uma entidade de domínio para entidade JPA.
     * 
     * @param domainOperacao Entidade de domínio
     * @return Entidade JPA
     */
    public OperacaoJpaEntity toJpaEntity(Operacao domainOperacao) {
        if (domainOperacao == null) {
            return null;
        }
        
        OperacaoJpaEntity jpaEntity = new OperacaoJpaEntity();
        
        jpaEntity.setId(domainOperacao.getId());
        jpaEntity.setEntradaSaida(domainOperacao.getEntradaSaida());
        jpaEntity.setData(domainOperacao.getData());
        jpaEntity.setMovimentacao(domainOperacao.getMovimentacao());
        jpaEntity.setProduto(domainOperacao.getProduto());
        jpaEntity.setInstituicao(domainOperacao.getInstituicao());
        
        // Conversão BigDecimal -> double para compatibilidade com DB
        jpaEntity.setQuantidade(domainOperacao.getQuantidade().value().doubleValue());
        
        jpaEntity.setPrecoUnitario(domainOperacao.getPrecoUnitario().getValue());
        jpaEntity.setValorOperacao(domainOperacao.getValorOperacao().getValue());
        jpaEntity.setValorCalculado(domainOperacao.getValorCalculado().getValue());
        jpaEntity.setDuplicado(domainOperacao.getDuplicado());
        jpaEntity.setDimensionado(domainOperacao.getDimensionado());
        jpaEntity.setIdOriginal(domainOperacao.getIdOriginal());
        jpaEntity.setDeletado(domainOperacao.getDeletado());
        jpaEntity.setUsuarioId(domainOperacao.getUsuarioId().value());
        
        return jpaEntity;
    }
    
    /**
     * Converte uma entidade JPA para entidade de domínio.
     * 
     * @param jpaEntity Entidade JPA
     * @return Entidade de domínio
     */
    public Operacao toDomainEntity(OperacaoJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        // Criar Value Objects
        UsuarioId usuarioId = new UsuarioId(jpaEntity.getUsuarioId());
        
        // Conversão double -> BigDecimal para o domínio
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(jpaEntity.getQuantidade()));
        
        Dinheiro precoUnitario = new Dinheiro(jpaEntity.getPrecoUnitario());
        Dinheiro valorOperacao = new Dinheiro(jpaEntity.getValorOperacao());
         
         return new Operacao(
             jpaEntity.getId(),
             jpaEntity.getEntradaSaida(),
             jpaEntity.getData(),
             jpaEntity.getMovimentacao(),
             jpaEntity.getProduto(),
             jpaEntity.getInstituicao(),
             quantidade,
             precoUnitario,
             valorOperacao,
             jpaEntity.getDuplicado(),
             jpaEntity.getDimensionado(),
             jpaEntity.getIdOriginal(),
             jpaEntity.getDeletado(),
             usuarioId
         );
    }
}