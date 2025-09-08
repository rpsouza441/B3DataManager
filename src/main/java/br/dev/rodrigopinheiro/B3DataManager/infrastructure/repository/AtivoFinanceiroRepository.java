package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;

import java.util.List;
import java.util.Optional;

public interface AtivoFinanceiroRepository extends JpaRepository<AtivoFinanceiroEntity, Long>, JpaSpecificationExecutor<AtivoFinanceiroEntity> {

    Optional<AtivoFinanceiroEntity> findByNomeAndPortfolio(String nomeAtivo, PortfolioEntity portfolio);


    /**
     * Busca todos os AtivoFinanceiros que não estão deletados.
     *
     * @return Lista de AtivoFinanceiros não deletados.
     */
    List<AtivoFinanceiroEntity> findByDeletadoFalse();

    /**
     * Busca um ativo financeiro pelo ID, verificando se não está deletado.
     *
     * @param id ID do ativo financeiro.
     * @return O ativo financeiro encontrado.
     */
    Optional<AtivoFinanceiroEntity> findByIdAndDeletadoFalse(Long id);

    /**
     * Busca um ativo financeiro pelo nome.
     *
     * @param nome Nome do ativo financeiro.
     * @return Um Optional contendo o ativo financeiro, se encontrado.
     */
    Optional<AtivoFinanceiroEntity> findByNome(String nome);

    //TODO Testar
    @Query("SELECT af FROM AtivoFinanceiroEntity af " +
            "JOIN af.portfolio p " +
            "JOIN p.usuario u " +
            "JOIN u.instituicoes i " +
            "WHERE u.id = :userId AND i.id = :instituicaoId")
    List<AtivoFinanceiroEntity> findAtivosByUsuarioAndInstituicao(@Param("userId") Long userId,
                                                            @Param("instituicaoId") Long instituicaoId);

}
