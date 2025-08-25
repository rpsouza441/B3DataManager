package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends
        JpaRepository<Usuario, Long>,
        JpaSpecificationExecutor<Usuario> {



    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<Usuario> findByIdAndDeletadoFalse(Long usuarioId);
    List<Usuario>findByDeletadoFalse();

    Page<Usuario> findAll(Specification<Usuario> filter, Pageable pageable);
}
