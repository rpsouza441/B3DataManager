package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.DarfEntity;

public interface DarfRepository extends JpaRepository<DarfEntity, Long> {
}