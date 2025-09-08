package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.InstituicaoEntity;

import java.util.Optional;

public interface InstituicaoRepository extends JpaRepository<InstituicaoEntity, Long> {
    /**
     * Busca uma instituição pelo nome.
     */
    Optional<InstituicaoEntity> findByNome(String nome);
}
