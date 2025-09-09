package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PortfolioMapper {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private AtivoFinanceiroMapper ativoFinanceiroMapper;

    @Autowired
    private TransacaoMapper transacaoMapper;

    public Portfolio toDomain(PortfolioEntity entity) {
        if (entity == null) return null;
        
        Portfolio portfolio = new Portfolio();
        portfolio.setId(entity.getId());
        portfolio.setSaldoTotal(entity.getSaldoTotal());
        portfolio.setSaldoAplicado(entity.getSaldoAplicado());
        portfolio.setLucroVenda(entity.getLucroVenda());
        portfolio.setLucroRendimento(entity.getLucroRendimento());
        // Note: deletado não existe na PortfolioEntity, mantendo false como padrão
        portfolio.setDeletado(false);
        
        // Mapear usuarioId ao invés do objeto completo (conforme design do domain)
        if (entity.getUsuario() != null) {
            portfolio.setUsuarioId(entity.getUsuario().getId());
        }
        
        // Note: Collections são mapeadas sob demanda pelos respectivos mappers
        // para evitar problemas de performance e lazy loading
        
        return portfolio;
    }

    public PortfolioEntity toEntity(Portfolio domain) {
        if (domain == null) return null;
        
        PortfolioEntity entity = new PortfolioEntity();
        entity.setId(domain.getId());
        entity.setSaldoTotal(domain.getSaldoTotal());
        entity.setSaldoAplicado(domain.getSaldoAplicado());
        entity.setLucroVenda(domain.getLucroVenda());
        entity.setLucroRendimento(domain.getLucroRendimento());
        // Note: deletado não existe na PortfolioEntity, ignorando esse campo
        
        // Mapear usuarioId - buscar Usuario entity se necessário
        if (domain.getUsuarioId() != null) {
            // Criar uma referência mínima do Usuario para manter a FK
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setId(domain.getUsuarioId());
            entity.setUsuario(usuario);
        }
        
        return entity;
    }
}