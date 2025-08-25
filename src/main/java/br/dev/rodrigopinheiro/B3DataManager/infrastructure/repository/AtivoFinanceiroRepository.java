package br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository;


import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AtivoFinanceiroRepository extends JpaRepository<AtivoFinanceiro, Long>, JpaSpecificationExecutor<AtivoFinanceiro> {

    Optional<AtivoFinanceiro> findByNomeAndPortfolio(String nomeAtivo, Portfolio portfolio);


    /**
     * Busca todos os AtivoFinanceiros que não estão deletados.
     *
     * @return Lista de AtivoFinanceiros não deletados.
     */
    List<AtivoFinanceiro> findByDeletadoFalse();

    /**
     * Busca um ativo financeiro pelo ID, verificando se não está deletado.
     *
     * @param id ID do ativo financeiro.
     * @return O ativo financeiro encontrado.
     */
    Optional<AtivoFinanceiro> findByIdAndDeletadoFalse(Long id);

    /**
     * Busca um ativo financeiro pelo nome.
     *
     * @param nome Nome do ativo financeiro.
     * @return Um Optional contendo o ativo financeiro, se encontrado.
     */
    Optional<AtivoFinanceiro> findByNome(String nome);

    //TODO Testar
    @Query("SELECT af FROM AtivoFinanceiro af " +
            "JOIN af.portfolio p " +
            "JOIN p.usuario u " +
            "JOIN u.instituicoes i " +
            "WHERE u.id = :userId AND i.id = :instituicaoId")
    List<AtivoFinanceiro> findAtivosByUsuarioAndInstituicao(@Param("userId") Long userId,
                                                            @Param("instituicaoId") Long instituicaoId);

}
