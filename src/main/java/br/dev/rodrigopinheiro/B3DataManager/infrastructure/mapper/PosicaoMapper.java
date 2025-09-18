package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Posicao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PosicaoEntity;
import org.springframework.stereotype.Component;

@Component
public class PosicaoMapper {

    /**
     * Converte PosicaoEntity para Posicao (domain).
     * Relacionamentos são mapeados sob demanda para evitar lazy loading.
     */
    public Posicao toDomain(PosicaoEntity entity) {
        if (entity == null) return null;
        
        Posicao posicao = new Posicao();
        
        // Campos básicos
        posicao.setId(entity.getId());
        posicao.setQuantidadeAtual(entity.getQuantidadeAtual());
        posicao.setPrecoMedio(entity.getPrecoMedio());
        posicao.setValorAtual(entity.getValorAtual());
        posicao.setPercentualPortfolio(entity.getPercentualPortfolio());
        posicao.setDataUltimaAtualizacao(entity.getDataUltimaAtualizacao());
        posicao.setLucroNaoRealizado(entity.getLucroNaoRealizado());
        posicao.setPercentualGanho(entity.getPercentualGanho());
        posicao.setValorInvestido(entity.getValorInvestido());
        posicao.setAtivo(entity.getAtivo()); // Boolean flag, não objeto
        
        // Nota: Campos de auditoria não são mapeados pois a classe Posicao do domínio
        // não possui esses campos. A auditoria é responsabilidade da camada de infraestrutura.
        
        // Relacionamentos são mapeados sob demanda pelos respectivos mappers
        // para evitar lazy loading issues e referências circulares
        
        return posicao;
    }

    /**
     * Converte Posicao (domain) para PosicaoEntity.
     * Relacionamentos devem ser resolvidos pela camada de serviço.
     */
    public PosicaoEntity toEntity(Posicao domain) {
        if (domain == null) return null;
        
        PosicaoEntity entity = new PosicaoEntity();
        
        // Campos básicos
        entity.setId(domain.getId());
        entity.setQuantidadeAtual(domain.getQuantidadeAtual());
        entity.setPrecoMedio(domain.getPrecoMedio());
        entity.setValorAtual(domain.getValorAtual());
        entity.setPercentualPortfolio(domain.getPercentualPortfolio());
        entity.setDataUltimaAtualizacao(domain.getDataUltimaAtualizacao());
        entity.setLucroNaoRealizado(domain.getLucroNaoRealizado());
        entity.setPercentualGanho(domain.getPercentualGanho());
        entity.setValorInvestido(domain.getValorInvestido());
        entity.setAtivo(domain.getAtivo()); // Boolean flag, não objeto
        
        // Nota: Campos de auditoria não são mapeados pois a classe Posicao do domínio
        // não possui esses campos. A auditoria é gerenciada automaticamente pela entidade.
        
        // Note: ativoFinanceiro e portfolio devem ser resolvidos pela camada de serviço
        // que possui acesso aos respectivos repositories
        
        return entity;
    }
}