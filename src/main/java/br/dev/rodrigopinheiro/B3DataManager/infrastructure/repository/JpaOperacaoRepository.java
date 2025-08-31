package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.entity.OperacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para OperacaoJpaEntity.
 * Interface Spring Data JPA para operações de persistência.
 */
@Repository
public interface JpaOperacaoRepository extends JpaRepository<OperacaoJpaEntity, Long> {
    
    /**
     * Verifica se existe uma operação com o ID original e usuário especificados.
     * 
     * @param idOriginal O ID original da operação
     * @param usuarioId O ID do usuário
     * @return true se existe uma operação com esses parâmetros
     */
    boolean existsByIdOriginalAndUsuarioId(Long idOriginal, Long usuarioId);
    
    /**
     * Busca uma operação por ID original e usuário.
     * 
     * @param idOriginal O ID original da operação
     * @param usuarioId O ID do usuário
     * @return Optional contendo a operação se encontrada
     */
    Optional<OperacaoJpaEntity> findByIdOriginalAndUsuarioId(Long idOriginal, Long usuarioId);
}