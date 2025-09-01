package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.CheckDuplicateCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.CheckDuplicateResult;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CheckDuplicateOperacaoUseCase.
 * 
 * <p>Testa todos os cenários de verificação de duplicidade,
 * incluindo sucessos, erros e validações.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CheckDuplicateOperacaoUseCase")
class CheckDuplicateOperacaoUseCaseTest {
    
    @Mock
    private OperacaoRepository operacaoRepository;
    
    private CheckDuplicateOperacaoUseCase checkDuplicateUseCase;
    
    private CheckDuplicateCommand validCommand;
    private UsuarioId usuarioId;
    private LocalDate data;
    private Operacao existingOperacao;
    
    @BeforeEach
    void setUp() {
        checkDuplicateUseCase = new CheckDuplicateOperacaoUseCase(operacaoRepository);
        
        usuarioId = new UsuarioId(1L);
        data = LocalDate.of(2025, 8, 29);
        
        validCommand = new CheckDuplicateCommand(
            data,
            "Juros Sobre Capital Próprio",
            "ITSA4 - ITAUSA S.A.",
            "INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA",
            new BigDecimal("19"),
            new BigDecimal("0.059"),
            new BigDecimal("0.96"),
            usuarioId
        );
        
        existingOperacao = new Operacao(
            1L,
            "Entrada",
            data,
            "Juros Sobre Capital Próprio",
            "ITSA4 - ITAUSA S.A.",
            "INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA",
            new Quantidade(new BigDecimal("19")),
            new Dinheiro(new BigDecimal("0.059")),
            new Dinheiro(new BigDecimal("0.96")),
            false, // duplicado
            false, // dimensionado
            null,  // idOriginal
            false, // deletado
            usuarioId
        );
    }
    
    @Nested
    @DisplayName("Verificação Sem Duplicidade")
    class VerificacaoSemDuplicidade {
        
        @Test
        @DisplayName("Deve retornar não duplicado quando operação não existe")
        void deveRetornarNaoDuplicadoQuandoOperacaoNaoExiste() {
            // Arrange
            when(operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), eq(false), any()
            )).thenReturn(Optional.empty());
            
            // Act
            CheckDuplicateResult result = checkDuplicateUseCase.execute(validCommand);
            
            // Assert
            assertFalse(result.isDuplicate());
            assertNull(result.originalId());
            
            verify(operacaoRepository).findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                eq(data),
                eq("Juros Sobre Capital Próprio"),
                eq("ITSA4 - ITAUSA S.A."),
                eq("INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA"),
                eq(new BigDecimal("19")),
                eq(new BigDecimal("0.059")),
                eq(new BigDecimal("0.96")),
                eq(false),
                eq(usuarioId)
            );
        }
        
        @Test
        @DisplayName("Deve retornar não duplicado para operação com dados diferentes")
        void deveRetornarNaoDuplicadoParaOperacaoComDadosDiferentes() {
            // Arrange
            CheckDuplicateCommand commandDiferente = new CheckDuplicateCommand(
                data,
                "Dividendo", // Movimentação diferente
                "ITSA4 - ITAUSA S.A.",
                "INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA",
                new BigDecimal("19"),
                new BigDecimal("0.059"),
                new BigDecimal("0.96"),
                usuarioId
            );
            
            when(operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), eq(false), any()
            )).thenReturn(Optional.empty());
            
            // Act
            CheckDuplicateResult result = checkDuplicateUseCase.execute(commandDiferente);
            
            // Assert
            assertFalse(result.isDuplicate());
            assertNull(result.originalId());
        }
    }
    
    @Nested
    @DisplayName("Verificação Com Duplicidade")
    class VerificacaoComDuplicidade {
        
        @Test
        @DisplayName("Deve retornar duplicado quando operação idêntica existe")
        void deveRetornarDuplicadoQuandoOperacaoIdenticaExiste() {
            // Arrange
            when(operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), eq(false), any()
            )).thenReturn(Optional.of(existingOperacao));
            
            // Act
            CheckDuplicateResult result = checkDuplicateUseCase.execute(validCommand);
            
            // Assert
            assertTrue(result.isDuplicate());
            assertEquals(1L, result.originalId());
            
            verify(operacaoRepository).findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                eq(data),
                eq("Juros Sobre Capital Próprio"),
                eq("ITSA4 - ITAUSA S.A."),
                eq("INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA"),
                eq(new BigDecimal("19")),
                eq(new BigDecimal("0.059")),
                eq(new BigDecimal("0.96")),
                eq(false),
                eq(usuarioId)
            );
        }
        
        @Test
        @DisplayName("Deve retornar duplicado com ID correto da operação original")
        void deveRetornarDuplicadoComIdCorretoDaOperacaoOriginal() {
            // Arrange
            Operacao operacaoComIdDiferente = new Operacao(
                999L, // ID diferente
                "Entrada",
                data,
                "Juros Sobre Capital Próprio",
                "ITSA4 - ITAUSA S.A.",
                "INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA",
                new Quantidade(new BigDecimal("19")),
                new Dinheiro(new BigDecimal("0.059")),
                new Dinheiro(new BigDecimal("0.96")),
                false,
                false,
                null,
                false,
                usuarioId
            );
            
            when(operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), eq(false), any()
            )).thenReturn(Optional.of(operacaoComIdDiferente));
            
            // Act
            CheckDuplicateResult result = checkDuplicateUseCase.execute(validCommand);
            
            // Assert
            assertTrue(result.isDuplicate());
            assertEquals(999L, result.originalId());
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
                () -> checkDuplicateUseCase.execute(null)
            );
            
            assertEquals("Comando não pode ser nulo", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar data nula")
        void deveRejeitarDataNula() {
            // Arrange
            CheckDuplicateCommand commandDataNula = new CheckDuplicateCommand(
                null, // Data nula
                "Juros Sobre Capital Próprio",
                "ITSA4 - ITAUSA S.A.",
                "INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA",
                new BigDecimal("19"),
                new BigDecimal("0.059"),
                new BigDecimal("0.96"),
                usuarioId
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkDuplicateUseCase.execute(commandDataNula)
            );
            
            assertEquals("Data é obrigatória", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar usuário nulo")
        void deveRejeitarUsuarioNulo() {
            // Arrange
            CheckDuplicateCommand commandUsuarioNulo = new CheckDuplicateCommand(
                data,
                "Juros Sobre Capital Próprio",
                "ITSA4 - ITAUSA S.A.",
                "INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA",
                new BigDecimal("19"),
                new BigDecimal("0.059"),
                new BigDecimal("0.96"),
                null // Usuário nulo
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkDuplicateUseCase.execute(commandUsuarioNulo)
            );
            
            assertEquals("Usuário é obrigatório", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar produto vazio")
        void deveRejeitarProdutoVazio() {
            // Arrange
            CheckDuplicateCommand commandProdutoVazio = new CheckDuplicateCommand(
                data,
                "Juros Sobre Capital Próprio",
                "", // Produto vazio
                "INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA",
                new BigDecimal("19"),
                new BigDecimal("0.059"),
                new BigDecimal("0.96"),
                usuarioId
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkDuplicateUseCase.execute(commandProdutoVazio)
            );
            
            assertEquals("Produto é obrigatório", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Tratamento de Erros")
    class TratamentoDeErros {
        
        @Test
        @DisplayName("Deve tratar erro de acesso aos dados")
        void deveTratarErroDeAcessoAosDados() {
            // Arrange
            DataAccessException dataException = mock(DataAccessException.class);
            when(dataException.getMessage()).thenReturn("Conexão perdida");
            
            when(operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), eq(false), any()
            )).thenThrow(dataException);
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> checkDuplicateUseCase.execute(validCommand)
            );
            
            assertEquals("Erro interno ao verificar duplicidade da operação", exception.getMessage());
            assertEquals(dataException, exception.getCause());
        }
        
        @Test
        @DisplayName("Deve tratar erro inesperado")
        void deveTratarErroInesperado() {
            // Arrange
            RuntimeException unexpectedException = new RuntimeException("Erro inesperado");
            
            when(operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), eq(false), any()
            )).thenThrow(unexpectedException);
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> checkDuplicateUseCase.execute(validCommand)
            );
            
            assertEquals("Erro inesperado ao verificar duplicidade da operação", exception.getMessage());
            assertEquals(unexpectedException, exception.getCause());
        }
    }
    
    @Nested
    @DisplayName("Cenários de Negócio")
    class CenariosDeNegocio {
        
        @Test
        @DisplayName("Deve verificar apenas operações não duplicadas")
        void deveVerificarApenasOperacoesNaoDuplicadas() {
            // Arrange
            when(operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), eq(false), any()
            )).thenReturn(Optional.empty());
            
            // Act
            checkDuplicateUseCase.execute(validCommand);
            
            // Assert
            verify(operacaoRepository).findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), 
                eq(false), // Verifica que busca apenas operações não duplicadas
                any()
            );
        }
        
        @Test
        @DisplayName("Deve verificar apenas operações do mesmo usuário")
        void deveVerificarApenasOperacoesDOMesmoUsuario() {
            // Arrange
            when(operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), eq(false), any()
            )).thenReturn(Optional.empty());
            
            // Act
            checkDuplicateUseCase.execute(validCommand);
            
            // Assert
            verify(operacaoRepository).findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), any(),
                eq(usuarioId) // Verifica que busca apenas operações do usuário
            );
        }
        
        @Test
        @DisplayName("Deve comparar todos os campos relevantes")
        void deveCompararTodosOsCamposRelevantes() {
            // Arrange
            when(operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                any(), any(), any(), any(), any(), any(), any(), eq(false), any()
            )).thenReturn(Optional.empty());
            
            // Act
            checkDuplicateUseCase.execute(validCommand);
            
            // Assert
            verify(operacaoRepository).findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                eq(validCommand.data()),
                eq(validCommand.movimentacao()),
                eq(validCommand.produto()),
                eq(validCommand.instituicao()),
                eq(validCommand.quantidade()),
                eq(validCommand.precoUnitario()),
                eq(validCommand.valorOperacao()),
                eq(false),
                eq(validCommand.usuarioId())
            );
        }
    }
}