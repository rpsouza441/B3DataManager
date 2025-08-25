package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.application.persistence.AggregatePersistenceService;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.*;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoTransacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.transacao.InvalidTransacaoException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.transacao.TransacaoNotFoundException;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.AtivoFactoryImpl;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.TransacaoFactory;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.TransacaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final AtivoFinanceiroService ativoFinanceiroService;
    private final InstituicaoService instituicaoService;
    private final MessageSource messageSource;
    private final TransacaoFactory transacaoFactory;
    private final AggregatePersistenceService aggregatePersistenceService;
    private final PortfolioService portfolioService;
    private final AtivoFactoryImpl ativoFactoryImpl;

    public TransacaoService(TransacaoRepository transacaoRepository,
                            AtivoFinanceiroService ativoFinanceiroService,
                            InstituicaoService instituicaoService, MessageSource messageSource,
                            TransacaoFactory transacaoFactory,
                            AggregatePersistenceService aggregatePersistenceService,
                            PortfolioService portfolioService, AtivoFactoryImpl ativoFactoryImpl) {
        this.transacaoRepository = transacaoRepository;
        this.ativoFinanceiroService = ativoFinanceiroService;
        this.instituicaoService = instituicaoService;
        this.messageSource = messageSource;
        this.transacaoFactory = transacaoFactory;
        this.aggregatePersistenceService = aggregatePersistenceService;
        this.portfolioService = portfolioService;
        this.ativoFactoryImpl = ativoFactoryImpl;
    }

    /**
     * Salva uma transação associada a um ativo financeiro e a uma instituição.
     *
     * @param usuarioId ID do usuário logado.
     * @param transacao A transação a ser salva.
     * @return Transacao salva.
     */
    @Transactional
    public Transacao salvarTransacao(Long usuarioId, Transacao transacao, Locale locale) {
        if (transacao == null) {
            throw new InvalidTransacaoException(messageSource);
        }

        // Verificar ou criar AtivoFinanceiro associado ao usuário
        AtivoFinanceiro ativoFinanceiro = ativoFinanceiroService.verificarOuCriarAtivoFinanceiro(
                usuarioId,
                transacao.getAtivoFinanceiro().getNome()
        );

        // Verificar ou criar Instituicao associada à transação
        Instituicao instituicao = instituicaoService.verificarOuCriarInstituicao(
                transacao.getInstituicao().getNome(),
                usuarioId,
                locale
        );

        // Associar entidades à transação
        transacao.setAtivoFinanceiro(ativoFinanceiro);
        transacao.setInstituicao(instituicao);

        // Definir a transação como não deletada
        transacao.setDeletado(false);

        // Salvar transação
        Transacao transacaoSalva = transacaoRepository.save(transacao);
        log.info("Transação salva com sucesso: {}", transacaoSalva);
        return transacaoSalva;
    }

    /**
     * Lista todas as transações de um ativo financeiro para o usuário logado.
     *
     * @param usuarioId         ID do usuário logado.
     * @param ativoFinanceiroId ID do ativo financeiro.
     * @return Lista de transações associadas ao ativo financeiro.
     */
    public List<Transacao> listarTransacoesPorAtivo(Long usuarioId, Long ativoFinanceiroId) {
        // Verificar se o ativo financeiro pertence ao usuário
        AtivoFinanceiro ativoFinanceiro = ativoFinanceiroService.buscarAtivoPorIdEUsuario(ativoFinanceiroId, usuarioId);

        // Buscar transações não deletadas associadas ao ativo financeiro
        List<Transacao> transacoes = transacaoRepository.findByAtivoFinanceiroIdAndDeletadoFalse(ativoFinanceiro.getId());
        log.info("Transações encontradas para o ativo financeiro {}: {}", ativoFinanceiro.getNome(), transacoes);
        return transacoes;
    }

    /**
     * Exclui logicamente uma transação.
     *
     * @param transacaoId ID da transação a ser excluída.
     * @param locale      Locale atual para mensagens internacionalizadas.
     */
    @Transactional
    public void deletarTransacao(Long transacaoId, Locale locale) {
        // Buscar a transação pelo ID
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> {
                    log.warn("Tentativa de excluir uma transação não encontrada com ID: {}", transacaoId);
                    throw new TransacaoNotFoundException(transacaoId, messageSource);
                });

        // Marcar como deletada
        transacao.setDeletado(true);
        transacaoRepository.save(transacao);
        log.info("Transação marcada como deletada: {}", transacaoId);
    }

    @Transactional
    public void criarTransacao(Operacao operacao) {

        // Se a operação é duplicada, encerra sem processar.
        if (operacao.getDuplicado()) {
            return;
        }

        Usuario usuario = operacao.getUsuario();
        if (usuario == null) {
            throw new IllegalArgumentException("Operação sem usuário associado.");
        }
        // Obtém (ou cria) os agregados necessários.

        Portfolio portfolio = portfolioService.obterOuCriarPortfolio(usuario.getId());
        Instituicao instituicao = instituicaoService.buscarOuCriarInstituicao(operacao.getInstituicao());

        // Cria a transação a partir da operação.
        Transacao transacao = transacaoFactory.criarTransacao(operacao);
        transacao.setDarf(null);

        // Associa a transação aos agregados comuns.
        portfolio.adicionarTransacao(transacao);
        instituicao.adicionarTransacoes(transacao);
        usuario.associarInstituicao(instituicao);
        instituicao.associarUsuario(usuario);


        // Verifica se a operação representa um lucro.
        if (!isTransacaoLucro(transacao)) {
            // Fluxo para operações não lucro: cria o ativo financeiro e realiza as associações.
            AtivoFinanceiro ativoFinanceiro = ativoFactoryImpl.criarAtivo(operacao, portfolio);
            ativoFinanceiro.adicionarTransacoes(transacao);
            portfolio.adicionarAtivoFinanceiro(ativoFinanceiro);

            // Persiste o agregado com o ativo financeiro.
            aggregatePersistenceService.persistAggregate(transacao, usuario, portfolio, instituicao, ativoFinanceiro);
        } else {
            // Fluxo para operações de lucro: não cria nem associa o ativo financeiro.
            aggregatePersistenceService.persistAggregate(transacao, usuario, portfolio, instituicao);
        }

    }

    /**
     * Verifica se a operação é do tipo lucro.
     *
     * @param transacao A operação a ser verificada.
     * @return true se o produto da operação for um dos tipos de lucro; false caso contrário.
     */
    private boolean isTransacaoLucro(Transacao transacao) {
        return transacao.getTipoTransacao() != null &&
                (transacao.getTipoTransacao().equals(TipoTransacao.LUCRO_RENDIMENTO.name())
                        || transacao.getTipoTransacao().equals(TipoTransacao.LUCRO_DIVIDENDO.name())
                        || transacao.getTipoTransacao().equals(TipoTransacao.LUCRO_JUROS.name())
                        || transacao.getTipoTransacao().equals(TipoTransacao.LUCRO_OUTRA.name()));
    }

}
