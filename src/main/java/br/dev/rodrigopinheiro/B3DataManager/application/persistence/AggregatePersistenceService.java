package br.dev.rodrigopinheiro.B3DataManager.application.persistence;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.*;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AggregatePersistenceService {

    private final PortfolioRepository portfolioRepository;
    private final TransacaoRepository transacaoRepository;
    private final AtivoFinanceiroRepository ativoFinanceiroRepository;
    private final UsuarioRepository usuarioRepository;
    private final InstituicaoRepository instituicaoRepository;

    public AggregatePersistenceService(PortfolioRepository portfolioRepository,
                                       TransacaoRepository transacaoRepository,
                                       AtivoFinanceiroRepository ativoFinanceiroRepository,
                                       UsuarioRepository usuarioRepository, InstituicaoRepository instituicaoRepository) {
        this.portfolioRepository = portfolioRepository;
        this.transacaoRepository = transacaoRepository;
        this.ativoFinanceiroRepository = ativoFinanceiroRepository;
        this.usuarioRepository = usuarioRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    /**
     * Persiste de forma consistente o agregado do Portfolio e suas entidades associadas.
     *
     * <p>
     * Esta abordagem adere aos princípios do Domain-Driven Design (DDD) ao tratar o Portfolio como o agregado raiz.
     * São persistidas explicitamente as entidades relacionadas – Transacao, Usuario, Instituicao e, quando aplicável,
     * AtivoFinanceiro – garantindo a integridade do agregado e evitando problemas como o "MultipleBagFetchException"
     * que podem ocorrer ao carregar múltiplas coleções (por exemplo, ativosFinanceiro, rendaFixas e rendaVariaveis)
     * com fetch join.
     * </p>
     *
     * @param transacao       A transação associada à operação.
     * @param usuario         O usuário responsável pela operação.
     * @param portfolio       O Portfolio (agregado raiz) que contém os ativos, transações e demais entidades.
     * @param instituicao     A instituição associada à operação.
     * @param ativoFinanceiro (Opcional) O ativo financeiro relacionado, se aplicável à operação.
     */
    @Transactional
    public void persistAggregate(Transacao transacao, Usuario usuario, Portfolio portfolio, Instituicao instituicao, AtivoFinanceiro ativoFinanceiro) {
        log.info("Persistindo Portfolio com ID: {}", portfolio.getId());

        ativoFinanceiroRepository.save(ativoFinanceiro);
        transacaoRepository.save(transacao);
        portfolioRepository.save(portfolio);
        usuarioRepository.save(usuario);
        instituicaoRepository.save(instituicao);
    }

    /**
     * Persiste de forma consistente o agregado do Portfolio e suas entidades associadas,
     * quando não há um AtivoFinanceiro relacionado à operação (por exemplo, em operações de lucro).
     *
     * <p>
     * Esta versão do métod persiste o Portfolio (agregado raiz) e as entidades associadas – Transacao, Usuario e Instituicao –
     * sem incluir um AtivoFinanceiro, mantendo a integridade do agregado e evitando problemas de carregamento de múltiplas coleções.
     * </p>
     *
     * @param transacao   A transação associada à operação.
     * @param usuario     O usuário responsável pela operação.
     * @param portfolio   O Portfolio (agregado raiz) que contém os ativos, transações e demais entidades.
     * @param instituicao A instituição associada à operação.
     */
    @Transactional
    public void persistAggregate(Transacao transacao, Usuario usuario, Portfolio portfolio, Instituicao instituicao) {
        log.info("Persistindo Portfolio com ID: {}", portfolio.getId());

        transacaoRepository.save(transacao);
        portfolioRepository.save(portfolio);
        usuarioRepository.save(usuario);
        instituicaoRepository.save(instituicao);
    }

}
