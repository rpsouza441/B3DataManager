package br.dev.rodrigopinheiro.B3DataManager.application.usecase.transacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.transacao.CreateTransacaoCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.persistence.AggregatePersistenceService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.InstituicaoService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.PortfolioService;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoTransacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.AtivoFactoryImpl;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.TransacaoFactory;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.InstituicaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Transacao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.mapper.TransacaoMapper;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.TransacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.UsuarioEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case responsável por criar transações a partir de operações.
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Processar operações e criar transações correspondentes</li>
 *   <li>Gerenciar agregados (Portfolio, Instituição, AtivoFinanceiro)</li>
 *   <li>Aplicar regras de negócio para diferentes tipos de transação</li>
 *   <li>Persistir agregados de forma consistente</li>
 * </ul>
 * 
 * <h3>Regras de Negócio:</h3>
 * <ul>
 *   <li>Operações duplicadas são ignoradas</li>
 *   <li>Transações de lucro não criam ativos financeiros</li>
 *   <li>Portfolio é criado automaticamente se não existir</li>
 *   <li>Instituições são criadas automaticamente se não existirem</li>
 * </ul>
 * 
 * <h3>Tipos de Transação de Lucro:</h3>
 * <ul>
 *   <li>LUCRO_RENDIMENTO</li>
 *   <li>LUCRO_DIVIDENDO</li>
 *   <li>LUCRO_JUROS</li>
 *   <li>LUCRO_OUTRA</li>
 * </ul>
 * 
 * <h3>Fluxo de Processamento:</h3>
 * <ol>
 *   <li>Validação da operação (não duplicada, usuário válido)</li>
 *   <li>Obtenção/criação do Portfolio do usuário</li>
 *   <li>Obtenção/criação da Instituição</li>
 *   <li>Criação da transação via TransacaoFactory</li>
 *   <li>Associações entre agregados</li>
 *   <li>Criação de AtivoFinanceiro (se não for lucro)</li>
 *   <li>Persistência dos agregados</li>
 * </ol>
 * 
 * @author Sistema B3DataManager
 * @since 1.0.0
 */
@Slf4j
@Component
public class CreateTransacaoUseCase {
    
    private final PortfolioService portfolioService;
    private final InstituicaoService instituicaoService;
    private final TransacaoFactory transacaoFactory;
    private final AggregatePersistenceService aggregatePersistenceService;
    private final AtivoFactoryImpl ativoFactoryImpl;
    private final TransacaoMapper transacaoMapper;
    
    public CreateTransacaoUseCase(
            PortfolioService portfolioService,
            InstituicaoService instituicaoService,
            TransacaoFactory transacaoFactory,
            AggregatePersistenceService aggregatePersistenceService,
            AtivoFactoryImpl ativoFactoryImpl,
            TransacaoMapper transacaoMapper) {
        this.portfolioService = portfolioService;
        this.instituicaoService = instituicaoService;
        this.transacaoFactory = transacaoFactory;
        this.aggregatePersistenceService = aggregatePersistenceService;
        this.ativoFactoryImpl = ativoFactoryImpl;
        this.transacaoMapper = transacaoMapper;
    }
    
    /**
     * Cria uma transação a partir de uma operação.
     * 
     * @param command Comando contendo a operação a ser processada
     * @throws IllegalArgumentException se a operação não tiver usuário associado
     */
    @Transactional
    public void execute(CreateTransacaoCommand command) {
        log.debug("Iniciando criação de transação para operação: {}", command.operacao().getId());
        
       OperacaoEntity operacao = command.operacao();
        
        // Se a operação é duplicada, encerra sem processar
        if (operacao.getDuplicado()) {
            log.debug("Operação {} é duplicada, ignorando criação de transação", operacao.getId());
            return;
        }
        
        UsuarioEntity usuario = operacao.getUsuario();
        if (usuario == null) {
            throw new IllegalArgumentException("Operação sem usuário associado.");
        }
        
        log.debug("Processando operação para usuário: {}", usuario.getId());
        
        // Obtém (ou cria) os agregados necessários
        PortfolioEntity portfolio = portfolioService.obterOuCriarPortfolio(usuario.getId());
        InstituicaoEntity instituicao = instituicaoService.buscarOuCriarInstituicao(operacao.getInstituicao());
        
        // Cria a transação a partir da operação
        Transacao transacao = transacaoFactory.criarTransacao(operacao);
        
        // Converte para entity para uso na infrastructure
        TransacaoEntity transacaoEntity = transacaoMapper.toEntity(transacao);
        
        // Inicializa DARF como null (será configurado posteriormente se necessário)
        transacaoEntity.setDarf(null);
        
        log.debug("Transação criada: tipo={}, valor={}", 
                 transacao.getTipoTransacao(), transacao.getValorTotal());
        
        // Associa a transação aos agregados comuns
        portfolio.adicionarTransacao(transacaoEntity);
        instituicao.adicionarTransacoes(transacaoEntity);
        usuario.associarInstituicao(instituicao);
        instituicao.associarUsuario(usuario);
        
        // Verifica se a operação representa um lucro
        if (!isTransacaoLucro(transacao)) {
            log.debug("Transação não é lucro, criando ativo financeiro");
            
            // Fluxo para operações não lucro: cria o ativo financeiro e realiza as associações
            AtivoFinanceiroEntity ativoFinanceiro = ativoFactoryImpl.criarAtivo(operacao, portfolio);
            ativoFinanceiro.adicionarTransacao(transacaoEntity);
            portfolio.adicionarAtivoFinanceiro(ativoFinanceiro);
            
            // Persiste o agregado com o ativo financeiro
            aggregatePersistenceService.persistAggregate(transacaoEntity, usuario, portfolio, instituicao, ativoFinanceiro);
            
            log.debug("Transação criada com ativo financeiro: {}", ativoFinanceiro.getNome());
        } else {
            log.debug("Transação é lucro, não criando ativo financeiro");
            
            // Fluxo para operações de lucro: não cria nem associa o ativo financeiro
            aggregatePersistenceService.persistAggregate(transacaoEntity, usuario, portfolio, instituicao);
        }
        
        log.info("Transação criada com sucesso para operação: {}", operacao.getId());
    }
    
    /**
     * Verifica se a transação é do tipo lucro.
     * 
     * @param transacao A transação a ser verificada
     * @return true se a transação for de lucro; false caso contrário
     */
    private boolean isTransacaoLucro(Transacao transacao) {
        return transacao.getTipoTransacao() != null &&
                (transacao.getTipoTransacao().equals(TipoTransacao.LUCRO_RENDIMENTO)
                        || transacao.getTipoTransacao().equals(TipoTransacao.LUCRO_DIVIDENDO)
                        || transacao.getTipoTransacao().equals(TipoTransacao.LUCRO_JUROS)
                        || transacao.getTipoTransacao().equals(TipoTransacao.LUCRO_OUTRA));
    }
}