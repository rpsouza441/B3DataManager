package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Instituicao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.InstituicaoEntity;
import org.springframework.stereotype.Component;

@Component
public class InstituicaoMapper {

    public Instituicao toDomain(InstituicaoEntity entity) {
        if (entity == null) return null;
        
        Instituicao instituicao = new Instituicao();
        instituicao.setId(entity.getId());
        instituicao.setNome(entity.getNome());
        
        // Relacionamentos são mapeados sob demanda pelos respectivos mappers
        // para evitar lazy loading issues e referências circulares
        
        return instituicao;
    }

    public InstituicaoEntity toEntity(Instituicao domain) {
        if (domain == null) return null;
        
        InstituicaoEntity entity = new InstituicaoEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        
        // Relacionamentos são mapeados sob demanda pelos respectivos mappers
        // para evitar lazy loading issues e referências circulares
        
        return entity;
    }
}