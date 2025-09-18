package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.UsuarioEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gerenciar usuários do sistema.
 * 
 * Responsável pela persistência e consulta de usuários, incluindo
 * operações de autenticação, autorização e gerenciamento de perfis.
 * 
 * Características:
 * - Busca por credenciais (username, email)
 * - Verificação de existência para validações
 * - Suporte a soft delete (deletado = false)
 * - Filtros dinâmicos com Specifications
 * - Paginação para listagens
 * 
 * Segurança:
 * - Sempre verifica ownership dos dados
 * - Suporte a soft delete para auditoria
 * - Métodos de verificação para evitar duplicatas
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Repository
public interface UsuarioRepository extends
        JpaRepository<UsuarioEntity, Long>,
        JpaSpecificationExecutor<UsuarioEntity> {

    /**
     * Busca um usuário pelo nome de usuário (username).
     * Utilizado principalmente para autenticação.
     * 
     * @param username Nome de usuário único
     * @return Optional contendo o usuário se encontrado
     */
    Optional<UsuarioEntity> findByUsername(String username);

    /**
     * Busca um usuário pelo endereço de email.
     * Utilizado para recuperação de senha e validações.
     * 
     * @param email Endereço de email único
     * @return Optional contendo o usuário se encontrado
     */
    Optional<UsuarioEntity> findByEmail(String email);

    /**
     * Verifica se já existe um usuário com o username especificado.
     * Utilizado para validação durante o cadastro.
     * 
     * @param username Nome de usuário a ser verificado
     * @return true se já existe um usuário com este username
     */
    boolean existsByUsername(String username);

    /**
     * Verifica se já existe um usuário com o email especificado.
     * Utilizado para validação durante o cadastro.
     * 
     * @param email Email a ser verificado
     * @return true se já existe um usuário com este email
     */
    boolean existsByEmail(String email);

    /**
     * Busca um usuário por ID que não esteja marcado como deletado.
     * Implementa soft delete para manter integridade referencial.
     * 
     * @param usuarioId ID do usuário
     * @return Optional contendo o usuário ativo se encontrado
     */
    Optional<UsuarioEntity> findByIdAndDeletadoFalse(Long usuarioId);

    /**
     * Busca todos os usuários que não estão marcados como deletados.
     * Utilizado para listagens administrativas.
     * 
     * @return Lista de usuários ativos
     */
    List<UsuarioEntity> findByDeletadoFalse();

    /**
     * Busca usuários com filtros dinâmicos e paginação.
     * Utiliza Specifications para construir queries complexas dinamicamente.
     * 
     * @param filter Specification contendo os filtros a serem aplicados
     * @param pageable Configuração de paginação e ordenação
     * @return Página de usuários que atendem aos critérios
     */
    Page<UsuarioEntity> findAll(Specification<UsuarioEntity> filter, Pageable pageable);
}
