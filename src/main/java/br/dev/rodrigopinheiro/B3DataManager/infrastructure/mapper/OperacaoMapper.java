package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Mapper responsável por converter entre entidades de domínio e JPA.
 * Isola as conversões e evita vazamento de entidades JPA para camadas superiores.
 */
@Component
public class OperacaoMapper {

    public Operacao toDomain(OperacaoEntity entity) {
        if (entity == null) return null;
        
        // Criar Value Objects
        UsuarioId usuarioId = new UsuarioId(entity.getUsuarioId());
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(entity.getQuantidade()));
        Dinheiro precoUnitario = new Dinheiro(entity.getPrecoUnitario());
        Dinheiro valorOperacao = new Dinheiro(entity.getValorOperacao());
        Dinheiro valorCalculado = entity.getValorCalculado() != null ? 
            new Dinheiro(entity.getValorCalculado()) : null;
        
        Operacao operacao = new Operacao(
            entity.getId(),
            entity.getEntradaSaida(),
            entity.getData(),
            entity.getMovimentacao(),
            entity.getProduto(),
            entity.getInstituicao(),
            quantidade,
            precoUnitario,
            valorOperacao,
            entity.getDuplicado(),
            entity.getProcessado(),
            entity.getIdOriginal(),
            entity.getDeletado(),
            usuarioId
        );
        
        // Define o valorCalculado usando setter, pois não é passado no construtor
        if (valorCalculado != null) {
            operacao.setValorCalculado(valorCalculado);
        }
        
        return operacao;
    }

    public OperacaoEntity toEntity(Operacao domain) {
        if (domain == null) return null;
        
        OperacaoEntity entity = new OperacaoEntity();
        
        entity.setId(domain.getId());
        entity.setEntradaSaida(domain.getEntradaSaida());
        entity.setData(domain.getData());
        entity.setMovimentacao(domain.getMovimentacao());
        entity.setProduto(domain.getProduto());
        entity.setInstituicao(domain.getInstituicao());
        entity.setQuantidade(domain.getQuantidade().value().doubleValue());
        entity.setPrecoUnitario(domain.getPrecoUnitario().getValue());
        entity.setValorOperacao(domain.getValorOperacao().getValue());
        
        if (domain.getValorCalculado() != null) {
            entity.setValorCalculado(domain.getValorCalculado().getValue());
        }
        
        entity.setDuplicado(domain.getDuplicado());
        entity.setProcessado(domain.getProcessado());
        entity.setIdOriginal(domain.getIdOriginal());
        entity.setDeletado(domain.getDeletado());
        entity.setUsuarioId(domain.getUsuarioId().value());
        
        // O relacionamento com UsuarioEntity será mapeado pela camada de serviço
        // para evitar dependências circulares entre mappers
        
        return entity;
    }
}