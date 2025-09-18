package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;

import java.util.Optional;

/**
 * Repository para gerenciar portfolios de investimentos.
 * 
 * Responsável pela persistência e consulta de portfolios, incluindo
 * operações otimizadas para carregamento de associações relacionadas.
 * 
 * Características:
 * - Busca por usuário proprietário
 * - Carregamento otimizado de associações (fetch joins)
 * - Prevenção do problema N+1 em consultas complexas
 * 
 * @author Rodrigo Pinheiro
 * @since 1.0
 */
@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {

    /**
     * Busca um portfolio pelo ID do usuário proprietário.
     * 
     * @param id ID do usuário proprietário do portfolio
     * @return Optional contendo o portfolio se encontrado
     */
    Optional<PortfolioEntity> findByUsuarioId(Long id);

    /**
     * Busca um portfolio por ID com carregamento otimizado de todas as associações.
     * 
     * Esta query utiliza LEFT JOIN FETCH para carregar eagerly todas as associações
     * relacionadas ao portfolio, evitando o problema N+1 e melhorando a performance
     * quando é necessário acessar os dados completos do portfolio.
     * 
     * Associações carregadas:
     * - Ativos financeiros
     * - Transações dos ativos
     * - Dados específicos de renda fixa
     * - Dados específicos de renda variável
     * - Transações do portfolio
     * 
     * @param id ID do portfolio a ser buscado
     * @return Optional contendo o portfolio com todas as associações carregadas
     */
    @Query("SELECT p FROM PortfolioEntity p " +
            "LEFT JOIN FETCH p.ativosFinanceiro af " +
            "LEFT JOIN FETCH af.transacoes " +
            "LEFT JOIN FETCH af.rendaFixas " +
            "LEFT JOIN FETCH af.rendaVariaveis " +
            "LEFT JOIN FETCH p.transacoes " +
            "WHERE p.id = :id")
    Optional<PortfolioEntity> findByIdFetchAssociations(@Param("id") Long id);
}