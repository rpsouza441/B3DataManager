package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.UsuarioEntity;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends
        JpaRepository<UsuarioEntity, Long>,
        JpaSpecificationExecutor<UsuarioEntity> {



    Optional<UsuarioEntity> findByUsername(String username);
    Optional<UsuarioEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<UsuarioEntity> findByIdAndDeletadoFalse(Long usuarioId);
    List<UsuarioEntity>findByDeletadoFalse();

    Page<UsuarioEntity> findAll(Specification<UsuarioEntity> filter, Pageable pageable);
}
