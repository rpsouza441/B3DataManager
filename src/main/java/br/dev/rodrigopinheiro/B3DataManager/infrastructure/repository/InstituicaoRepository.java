package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.InstituicaoEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gerenciar instituições financeiras.
 * 
 * Responsável pela persistência e consulta de instituições financeiras
 * como corretoras, bancos e outras entidades do mercado financeiro.
 * 
 * Características:
 * - Busca por nome e código
 * - Verificação de existência para validações
 * - Suporte a soft delete
 * - Filtros por tipo de instituição
 * - Listagem de instituições ativas
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Repository
public interface InstituicaoRepository extends JpaRepository<InstituicaoEntity, Long> {
    
    /**
     * Busca uma instituição pelo nome.
     * Utilizado para validação e busca por nome exato.
     * 
     * @param nome Nome da instituição
     * @return Optional contendo a instituição se encontrada
     */
    Optional<InstituicaoEntity> findByNome(String nome);
    
    /**
     * Busca uma instituição pelo código identificador.
     * Utilizado para integração com APIs externas.
     * 
     * @param codigo Código único da instituição
     * @return Optional contendo a instituição se encontrada
     */
    Optional<InstituicaoEntity> findByCodigo(String codigo);
    
    /**
     * Verifica se já existe uma instituição com o nome especificado.
     * Utilizado para validação durante o cadastro.
     * 
     * @param nome Nome da instituição a ser verificado
     * @return true se já existe uma instituição com este nome
     */
    boolean existsByNome(String nome);
    
    /**
     * Verifica se já existe uma instituição com o código especificado.
     * Utilizado para validação durante o cadastro.
     * 
     * @param codigo Código da instituição a ser verificado
     * @return true se já existe uma instituição com este código
     */
    boolean existsByCodigo(String codigo);
    
    /**
     * Busca todas as instituições que não estão marcadas como deletadas.
     * Implementa soft delete para manter integridade referencial.
     * 
     * @return Lista de instituições ativas
     */
    @Query("SELECT i FROM InstituicaoEntity i WHERE i.deletado = false ORDER BY i.nome")
    List<InstituicaoEntity> findByDeletadoFalse();
    
    /**
     * Busca uma instituição por ID que não esteja marcada como deletada.
     * 
     * @param id ID da instituição
     * @return Optional contendo a instituição ativa se encontrada
     */
    @Query("SELECT i FROM InstituicaoEntity i WHERE i.id = :id AND i.deletado = false")
    Optional<InstituicaoEntity> findByIdAndDeletadoFalse(@Param("id") Long id);
    
    /**
     * Busca instituições por tipo (ex: CORRETORA, BANCO, DISTRIBUIDORA).
     * 
     * @param tipo Tipo da instituição
     * @return Lista de instituições do tipo especificado
     */
    @Query("SELECT i FROM InstituicaoEntity i WHERE i.tipo = :tipo AND i.deletado = false ORDER BY i.nome")
    List<InstituicaoEntity> findByTipoAndDeletadoFalse(@Param("tipo") String tipo);
    
    /**
     * Busca instituições por nome usando LIKE (busca parcial).
     * Útil para funcionalidades de autocomplete e busca.
     * 
     * @param nome Parte do nome da instituição
     * @return Lista de instituições que contêm o nome especificado
     */
    @Query("SELECT i FROM InstituicaoEntity i WHERE LOWER(i.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND i.deletado = false ORDER BY i.nome")
    List<InstituicaoEntity> findByNomeContainingIgnoreCaseAndDeletadoFalse(@Param("nome") String nome);
    
    /**
     * Conta o número total de instituições ativas.
     * 
     * @return Número de instituições não deletadas
     */
    @Query("SELECT COUNT(i) FROM InstituicaoEntity i WHERE i.deletado = false")
    long countByDeletadoFalse();
}
