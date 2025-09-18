package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;
import org.springframework.stereotype.Component;

@Component
public class PortfolioMapper {

    /**
     * Converte PortfolioEntity para Portfolio (domain).
     * Relacionamentos são mapeados sob demanda para evitar lazy loading.
     */
    public Portfolio toDomain(PortfolioEntity entity) {
        if (entity == null) return null;
        
        Portfolio portfolio = new Portfolio();
        portfolio.setId(entity.getId());
        portfolio.setSaldoTotal(entity.getSaldoTotal());
        portfolio.setSaldoAplicado(entity.getSaldoAplicado());
        portfolio.setLucroVenda(entity.getLucroVenda());
        portfolio.setLucroRendimento(entity.getLucroRendimento());
        portfolio.setDeletado(false); // Campo não existe na entity
    
        
        return portfolio;
    }

    /**
     * Converte Portfolio (domain) para PortfolioEntity.
     * Relacionamentos devem ser resolvidos pela camada de serviço.
     */
    public PortfolioEntity toEntity(Portfolio domain) {
        if (domain == null) return null;
        
        PortfolioEntity entity = new PortfolioEntity();
        entity.setId(domain.getId());
        entity.setSaldoTotal(domain.getSaldoTotal());
        entity.setSaldoAplicado(domain.getSaldoAplicado());
        entity.setLucroVenda(domain.getLucroVenda());
        entity.setLucroRendimento(domain.getLucroRendimento());
        
        // Note: usuarioId deve ser resolvido pela camada de serviço
        // que possui acesso ao UsuarioRepository
        
        return entity;
    }
}