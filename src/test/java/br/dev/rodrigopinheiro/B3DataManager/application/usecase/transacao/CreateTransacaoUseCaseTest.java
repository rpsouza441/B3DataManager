package br.dev.rodrigopinheiro.B3DataManager.application.usecase.transacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.transacao.CreateTransacaoCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.persistence.AggregatePersistenceService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.InstituicaoService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.PortfolioService;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.*;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoTransacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.AtivoFactoryImpl;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.TransacaoFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CreateTransacaoUseCase.
 * 
 * <p>Testa todos os cenários de criação de transações,
 * incluindo operações normais, duplicadas e de lucro.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateTransacaoUseCase")
class CreateTransacaoUseCaseTest {
    
    @Mock
    private PortfolioService portfolioService;
    
    @Mock
    private InstituicaoService instituicaoService;
    
    @Mock
    private TransacaoFactory transacaoFactory;
    
    @Mock
    private AggregatePersistenceService aggregatePersistenceService;
    
    @Mock
    private AtivoFactoryImpl ativoFactoryImpl;
    
    private CreateTransacaoUseCase createTransacaoUseCase;
    
    private OperacaoEntity operacao;
    private Usuario usuario;
    private Portfolio portfolio;
    private Instituicao instituicao;
    private Transacao transacao;
    private AtivoFinanceiro ativoFinanceiro;
    
    @BeforeEach
    void setUp() {
        createTransacaoUseCase = new CreateTransacaoUseCase(
            portfolioService,
            instituicaoService,
            transacaoFactory,
            aggregatePersistenceService,
            ativoFactoryImpl
        );
        
        // Setup mocks
        usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(1L);
        
        operacao = mock(OperacaoEntity.class);
        when(operacao.getId()).thenReturn(1L);
        when(operacao.getDuplicado()).thenReturn(false);
        when(operacao.getUsuario()).thenReturn(usuario);
        when(operacao.getInstituicao()).thenReturn("XP INVESTIMENTOS");
        
        portfolio = mock(Portfolio.class);
        instituicao = mock(Instituicao.class);
        transacao = mock(Transacao.class);
        ativoFinanceiro = mock(AtivoFinanceiro.class);
        
        when(portfolioService.obterOuCriarPortfolio(1L)).thenReturn(portfolio);
        when(instituicaoService.buscarOuCriarInstituicao("XP INVESTIMENTOS")).thenReturn(instituicao);
        when(transacaoFactory.criarTransacao(operacao)).thenReturn(transacao);
        when(ativoFactoryImpl.criarAtivo(operacao, portfolio)).thenReturn(ativoFinanceiro);
    }
    
    @Nested
    @DisplayName("Criação de Transação com Sucesso")
    class CriacaoComSucesso {
        
        @Test
        @DisplayName("Deve criar transação para operação normal")
        void deveCriarTransacaoParaOperacaoNormal() {
            // Arrange
            when(transacao.getTipoTransacao()).thenReturn("COMPRA");
            CreateTransacaoCommand command = new CreateTransacaoCommand(operacao);
            
            // Act
            createTransacaoUseCase.execute(command);
            
            // Assert
            verify(portfolioService).obterOuCriarPortfolio(1L);
            verify(instituicaoService).buscarOuCriarInstituicao("XP INVESTIMENTOS");
            verify(transacaoFactory).criarTransacao(operacao);
            verify(transacao).setDarf(null);
            
            verify(portfolio).adicionarTransacao(transacao);
            verify(instituicao).adicionarTransacoes(transacao);
            verify(usuario).associarInstituicao(instituicao);
            verify(instituicao).associarUsuario(usuario);
            
            verify(ativoFactoryImpl).criarAtivo(operacao, portfolio);
            verify(ativoFinanceiro).adicionarTransacoes(transacao);
            verify(portfolio).adicionarAtivoFinanceiro(ativoFinanceiro);
            
            verify(aggregatePersistenceService).persistAggregate(
                transacao, usuario, portfolio, instituicao, ativoFinanceiro
            );
        }
        
        @Test
        @DisplayName("Deve criar transação de lucro sem ativo financeiro")
        void deveCriarTransacaoDeLucroSemAtivoFinanceiro() {
            // Arrange
            when(transacao.getTipoTransacao()).thenReturn(TipoTransacao.LUCRO_DIVIDENDO.name());
            CreateTransacaoCommand command = new CreateTransacaoCommand(operacao);
            
            // Act
            createTransacaoUseCase.execute(command);
            
            // Assert
            verify(portfolioService).obterOuCriarPortfolio(1L);
            verify(instituicaoService).buscarOuCriarInstituicao("XP INVESTIMENTOS");
            verify(transacaoFactory).criarTransacao(operacao);
            
            // Não deve criar ativo financeiro para lucros
            verify(ativoFactoryImpl, never()).criarAtivo(any(), any());
            verify(portfolio, never()).adicionarAtivoFinanceiro(any());
            
            // Deve persistir sem ativo financeiro
            verify(aggregatePersistenceService).persistAggregate(
                transacao, usuario, portfolio, instituicao
            );
        }
        
        @Test
        @DisplayName("Deve identificar todos os tipos de lucro")
        void deveIdentificarTodosTiposDeLucro() {
            // Arrange
            String[] tiposLucro = {
                TipoTransacao.LUCRO_RENDIMENTO.name(),
                TipoTransacao.LUCRO_DIVIDENDO.name(),
                TipoTransacao.LUCRO_JUROS.name(),
                TipoTransacao.LUCRO_OUTRA.name()
            };
            
            for (String tipoLucro : tiposLucro) {
                // Reset mocks
                reset(ativoFactoryImpl, aggregatePersistenceService);
                
                when(transacao.getTipoTransacao()).thenReturn(tipoLucro);
                CreateTransacaoCommand command = new CreateTransacaoCommand(operacao);
                
                // Act
                createTransacaoUseCase.execute(command);
                
                // Assert
                verify(ativoFactoryImpl, never()).criarAtivo(any(), any());
                verify(aggregatePersistenceService).persistAggregate(
                    transacao, usuario, portfolio, instituicao
                );
            }
        }
    }
    
    @Nested
    @DisplayName("Operações Duplicadas")
    class OperacoesDuplicadas {
        
        @Test
        @DisplayName("Deve ignorar operações duplicadas")
        void deveIgnorarOperacoesDuplicadas() {
            // Arrange
            when(operacao.getDuplicado()).thenReturn(true);
            CreateTransacaoCommand command = new CreateTransacaoCommand(operacao);
            
            // Act
            createTransacaoUseCase.execute(command);
            
            // Assert
            verify(portfolioService, never()).obterOuCriarPortfolio(any());
            verify(instituicaoService, never()).buscarOuCriarInstituicao(any());
            verify(transacaoFactory, never()).criarTransacao(any());
            verify(aggregatePersistenceService, never()).persistAggregate(any(), any(), any(), any());
        }
    }
    
    @Nested
    @DisplayName("Validação de Entrada")
    class ValidacaoDeEntrada {
        
        @Test
        @DisplayName("Deve rejeitar comando nulo")
        void deveRejeitarComandoNulo() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CreateTransacaoCommand(null)
            );
            
            assertEquals("Operação não pode ser nula", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar operação sem usuário")
        void deveRejeitarOperacaoSemUsuario() {
            // Arrange
            when(operacao.getUsuario()).thenReturn(null);
            CreateTransacaoCommand command = new CreateTransacaoCommand(operacao);
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createTransacaoUseCase.execute(command)
            );
            
            assertEquals("Operação sem usuário associado.", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Integração com Services")
    class IntegracaoComServices {
        
        @Test
        @DisplayName("Deve chamar todos os services necessários")
        void deveChamarTodosOsServicesNecessarios() {
            // Arrange
            when(transacao.getTipoTransacao()).thenReturn("COMPRA");
            CreateTransacaoCommand command = new CreateTransacaoCommand(operacao);
            
            // Act
            createTransacaoUseCase.execute(command);
            
            // Assert
            verify(portfolioService).obterOuCriarPortfolio(1L);
            verify(instituicaoService).buscarOuCriarInstituicao("XP INVESTIMENTOS");
            verify(transacaoFactory).criarTransacao(operacao);
            verify(ativoFactoryImpl).criarAtivo(operacao, portfolio);
            verify(aggregatePersistenceService).persistAggregate(
                transacao, usuario, portfolio, instituicao, ativoFinanceiro
            );
        }
        
        @Test
        @DisplayName("Deve configurar transação corretamente")
        void deveConfigurarTransacaoCorretamente() {
            // Arrange
            when(transacao.getTipoTransacao()).thenReturn("VENDA");
            CreateTransacaoCommand command = new CreateTransacaoCommand(operacao);
            
            // Act
            createTransacaoUseCase.execute(command);
            
            // Assert
            verify(transacao).setDarf(null);
            verify(portfolio).adicionarTransacao(transacao);
            verify(instituicao).adicionarTransacoes(transacao);
            verify(usuario).associarInstituicao(instituicao);
            verify(instituicao).associarUsuario(usuario);
        }
        
        @Test
        @DisplayName("Deve associar ativo financeiro para operações não lucro")
        void deveAssociarAtivoFinanceiroParaOperacoesNaoLucro() {
            // Arrange
            when(transacao.getTipoTransacao()).thenReturn("COMPRA");
            when(ativoFinanceiro.getNome()).thenReturn("ITSA4");
            CreateTransacaoCommand command = new CreateTransacaoCommand(operacao);
            
            // Act
            createTransacaoUseCase.execute(command);
            
            // Assert
            verify(ativoFinanceiro).adicionarTransacoes(transacao);
            verify(portfolio).adicionarAtivoFinanceiro(ativoFinanceiro);
        }
    }
    
    @Nested
    @DisplayName("Cenários de Erro")
    class CenariosDeErro {
        
        @Test
        @DisplayName("Deve lidar com erro no portfolio service")
        void deveLidarComErroNoPortfolioService() {
            // Arrange
            when(portfolioService.obterOuCriarPortfolio(1L))
                .thenThrow(new RuntimeException("Erro no portfolio"));
            CreateTransacaoCommand command = new CreateTransacaoCommand(operacao);
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createTransacaoUseCase.execute(command)
            );
            
            assertEquals("Erro no portfolio", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve lidar com erro no transacao factory")
        void deveLidarComErroNoTransacaoFactory() {
            // Arrange
            when(transacaoFactory.criarTransacao(operacao))
                .thenThrow(new RuntimeException("Erro na factory"));
            CreateTransacaoCommand command = new CreateTransacaoCommand(operacao);
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createTransacaoUseCase.execute(command)
            );
            
            assertEquals("Erro na factory", exception.getMessage());
        }
    }
}