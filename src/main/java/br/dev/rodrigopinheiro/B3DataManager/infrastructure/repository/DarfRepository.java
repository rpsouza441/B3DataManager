package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Darf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DarfRepository extends JpaRepository<Darf, Long> {
}