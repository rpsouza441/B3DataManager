package br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Usuario;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        
        Usuario usuario = new Usuario();
        usuario.setId(entity.getId());
        usuario.setUsername(entity.getUsername());
        usuario.setPassword(entity.getPassword());
        usuario.setEmail(entity.getEmail());
        usuario.setRoles(entity.getRoles());
        usuario.setDeletado(entity.getDeletado());
        
        // Note: Portfolio e Instituicoes collections são mapeadas sob demanda
        // para evitar problemas de performance e lazy loading
        
        return usuario;
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setEmail(domain.getEmail());
        entity.setRoles(domain.getRoles());
        entity.setDeletado(domain.getDeletado());
        
        return entity;
    }
}