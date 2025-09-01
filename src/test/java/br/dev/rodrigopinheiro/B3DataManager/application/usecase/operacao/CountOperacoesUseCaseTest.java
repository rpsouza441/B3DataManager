package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.CountOperacoesCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.criteria.FilterCriteria;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CountOperacoesUseCase.
 * 
 * <p>Testa todos os cenários de contagem de operações,
 * incluindo filtros, sucessos e erros.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CountOperacoesUseCase")
class CountOperacoesUseCaseTest {
    
    @Mock
    private OperacaoRepository operacaoRepository;
    
    private CountOperacoesUseCase countOperacoesUseCase;
    
    private CountOperacoesCommand validCommand;
    private UsuarioId usuarioId;
    
    @BeforeEach
    void setUp() {
        countOperacoesUseCase = new CountOperacoesUseCase(operacaoRepository);
        
        usuarioId = new UsuarioId(1L);
        
        validCommand = new CountOperacoesCommand(
            null, // entradaSaida
            null, // startDate
            null, // endDate
            null, // movimentacao
            null, // produto
            null, // instituicao
            null, // duplicado
            null, // dimensionado
            1L    // usuarioId
        );
    }
    
    @Nested
    @DisplayName("Contagem com Sucesso")
    class ContagemComSucesso {
        
        @Test
        @DisplayName("Deve contar operações sem filtros")
        void deveContarOperacoesSemFiltros() {
            // Arrange
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(150L);
            
            // Act
            long result = countOperacoesUseCase.execute(validCommand);
            
            // Assert
            assertEquals(150L, result);
            
            verify(operacaoRepository).countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId));
        }
        
        @Test
        @DisplayName("Deve contar operações com filtros")
        void deveContarOperacoesComFiltros() {
            // Arrange
            CountOperacoesCommand commandComFiltros = new CountOperacoesCommand(
                "Entrada",
                LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 8, 31),
                "Juros",
                "ITSA4",
                "INTER",
                false,
                false,
                1L
            );
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(25L);
            
            // Act
            long result = countOperacoesUseCase.execute(commandComFiltros);
            
            // Assert
            assertEquals(25L, result);
            
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                argThat(criteria -> 
                    "Entrada".equals(criteria.entradaSaida()) &&
                    LocalDate.of(2025, 8, 1).equals(criteria.startDate()) &&
                    LocalDate.of(2025, 8, 31).equals(criteria.endDate()) &&
                    "Juros".equals(criteria.movimentacao()) &&
                    "ITSA4".equals(criteria.produto()) &&
                    "INTER".equals(criteria.instituicao()) &&
                    Boolean.FALSE.equals(criteria.duplicado()) &&
                    Boolean.FALSE.equals(criteria.dimensionado())
                ),
                eq(usuarioId)
            );
        }
        
        @Test
        @DisplayName("Deve retornar zero quando não há operações")
        void deveRetornarZeroQuandoNaoHaOperacoes() {
            // Arrange
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(0L);
            
            // Act
            long result = countOperacoesUseCase.execute(validCommand);
            
            // Assert
            assertEquals(0L, result);
        }
        
        @Test
        @DisplayName("Deve contar operações duplicadas")
        void deveContarOperacoesDuplicadas() {
            // Arrange
            CountOperacoesCommand commandDuplicadas = new CountOperacoesCommand(
                null, null, null, null, null, null,
                true, // apenas duplicadas
                null, 1L
            );
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(5L);
            
            // Act
            long result = countOperacoesUseCase.execute(commandDuplicadas);
            
            // Assert
            assertEquals(5L, result);
            
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                argThat(criteria -> Boolean.TRUE.equals(criteria.duplicado())),
                eq(usuarioId)
            );
        }
        
        @Test
        @DisplayName("Deve contar operações não duplicadas")
        void deveContarOperacoesNaoDuplicadas() {
            // Arrange
            CountOperacoesCommand commandNaoDuplicadas = new CountOperacoesCommand(
                null, null, null, null, null, null,
                false, // apenas não duplicadas
                null, 1L
            );
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(145L);
            
            // Act
            long result = countOperacoesUseCase.execute(commandNaoDuplicadas);
            
            // Assert
            assertEquals(145L, result);
            
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                argThat(criteria -> Boolean.FALSE.equals(criteria.duplicado())),
                eq(usuarioId)
            );
        }
        
        @Test
        @DisplayName("Deve contar operações dimensionadas")
        void deveContarOperacoesDimensionadas() {
            // Arrange
            CountOperacoesCommand commandDimensionadas = new CountOperacoesCommand(
                null, null, null, null, null, null, null,
                true, // apenas dimensionadas
                1L
            );
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(75L);
            
            // Act
            long result = countOperacoesUseCase.execute(commandDimensionadas);
            
            // Assert
            assertEquals(75L, result);
            
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                argThat(criteria -> Boolean.TRUE.equals(criteria.dimensionado())),
                eq(usuarioId)
            );
        }
    }
    
    @Nested
    @DisplayName("Filtros Específicos")
    class FiltrosEspecificos {
        
        @Test
        @DisplayName("Deve contar apenas operações de entrada")
        void deveContarApenasOperacoesDeEntrada() {
            // Arrange
            CountOperacoesCommand commandEntrada = new CountOperacoesCommand(
                "Entrada", null, null, null, null, null, null, null, 1L
            );
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(80L);
            
            // Act
            long result = countOperacoesUseCase.execute(commandEntrada);
            
            // Assert
            assertEquals(80L, result);
            
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                argThat(criteria -> "Entrada".equals(criteria.entradaSaida())),
                eq(usuarioId)
            );
        }
        
        @Test
        @DisplayName("Deve contar apenas operações de saída")
        void deveContarApenasOperacoesDeSaida() {
            // Arrange
            CountOperacoesCommand commandSaida = new CountOperacoesCommand(
                "Saída", null, null, null, null, null, null, null, 1L
            );
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(70L);
            
            // Act
            long result = countOperacoesUseCase.execute(commandSaida);
            
            // Assert
            assertEquals(70L, result);
            
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                argThat(criteria -> "Saída".equals(criteria.entradaSaida())),
                eq(usuarioId)
            );
        }
        
        @Test
        @DisplayName("Deve contar operações por período")
        void deveContarOperacoesPorPeriodo() {
            // Arrange
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);
            
            CountOperacoesCommand commandPeriodo = new CountOperacoesCommand(
                null, startDate, endDate, null, null, null, null, null, 1L
            );
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(120L);
            
            // Act
            long result = countOperacoesUseCase.execute(commandPeriodo);
            
            // Assert
            assertEquals(120L, result);
            
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                argThat(criteria -> 
                    startDate.equals(criteria.startDate()) &&
                    endDate.equals(criteria.endDate())
                ),
                eq(usuarioId)
            );
        }
        
        @Test
        @DisplayName("Deve contar operações por produto")
        void deveContarOperacoesPorProduto() {
            // Arrange
            CountOperacoesCommand commandProduto = new CountOperacoesCommand(
                null, null, null, null, "ITSA4", null, null, null, 1L
            );
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(15L);
            
            // Act
            long result = countOperacoesUseCase.execute(commandProduto);
            
            // Assert
            assertEquals(15L, result);
            
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                argThat(criteria -> "ITSA4".equals(criteria.produto())),
                eq(usuarioId)
            );
        }
        
        @Test
        @DisplayName("Deve contar operações por instituição")
        void deveContarOperacoesPorInstituicao() {
            // Arrange
            CountOperacoesCommand commandInstituicao = new CountOperacoesCommand(
                null, null, null, null, null, "XP INVESTIMENTOS", null, null, 1L
            );
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(45L);
            
            // Act
            long result = countOperacoesUseCase.execute(commandInstituicao);
            
            // Assert
            assertEquals(45L, result);
            
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                argThat(criteria -> "XP INVESTIMENTOS".equals(criteria.instituicao())),
                eq(usuarioId)
            );
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
                () -> countOperacoesUseCase.execute(null)
            );
            
            assertEquals("Comando não pode ser nulo", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar usuário nulo")
        void deveRejeitarUsuarioNulo() {
            // Arrange
            CountOperacoesCommand commandUsuarioNulo = new CountOperacoesCommand(
                null, null, null, null, null, null, null, null,
                null // usuarioId nulo
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> countOperacoesUseCase.execute(commandUsuarioNulo)
            );
            
            assertEquals("ID do usuário é obrigatório", exception.getMessage());
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
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenThrow(dataException);
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> countOperacoesUseCase.execute(validCommand)
            );
            
            assertEquals("Erro interno ao contar operações", exception.getMessage());
            assertEquals(dataException, exception.getCause());
        }
        
        @Test
        @DisplayName("Deve tratar erro inesperado")
        void deveTratarErroInesperado() {
            // Arrange
            RuntimeException unexpectedException = new RuntimeException("Erro inesperado");
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenThrow(unexpectedException);
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> countOperacoesUseCase.execute(validCommand)
            );
            
            assertEquals("Erro inesperado ao contar operações", exception.getMessage());
            assertEquals(unexpectedException, exception.getCause());
        }
        
        @Test
        @DisplayName("Deve tratar timeout de consulta")
        void deveTratarTimeoutDeConsulta() {
            // Arrange
            DataAccessException timeoutException = mock(DataAccessException.class);
            when(timeoutException.getMessage()).thenReturn("Query timeout");
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenThrow(timeoutException);
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> countOperacoesUseCase.execute(validCommand)
            );
            
            assertEquals("Erro interno ao contar operações", exception.getMessage());
            assertEquals(timeoutException, exception.getCause());
        }
    }
    
    @Nested
    @DisplayName("Integração com Repository")
    class IntegracaoComRepository {
        
        @Test
        @DisplayName("Deve passar critérios corretos para o repository")
        void devePassarCriteriosCorretosParaORepository() {
            // Arrange
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(100L);
            
            // Act
            countOperacoesUseCase.execute(validCommand);
            
            // Assert
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                any(FilterCriteria.class),
                eq(usuarioId)
            );
        }
        
        @Test
        @DisplayName("Deve garantir ownership obrigatório")
        void deveGarantirOwnershipObrigatorio() {
            // Arrange
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), any(UsuarioId.class)))
                .thenReturn(50L);
            
            // Act
            countOperacoesUseCase.execute(validCommand);
            
            // Assert
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                any(FilterCriteria.class),
                eq(usuarioId) // Verifica que o usuário é sempre passado
            );
        }
        
        @Test
        @DisplayName("Deve criar FilterCriteria com todos os parâmetros")
        void deveCriarFilterCriteriaComTodosOsParametros() {
            // Arrange
            CountOperacoesCommand commandCompleto = new CountOperacoesCommand(
                "Entrada",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "Dividendo",
                "PETR4",
                "XP INVESTIMENTOS",
                false,
                true,
                1L
            );
            
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(10L);
            
            // Act
            countOperacoesUseCase.execute(commandCompleto);
            
            // Assert
            verify(operacaoRepository).countByFiltersAndUsuarioId(
                argThat(criteria -> 
                    "Entrada".equals(criteria.entradaSaida()) &&
                    LocalDate.of(2025, 1, 1).equals(criteria.startDate()) &&
                    LocalDate.of(2025, 12, 31).equals(criteria.endDate()) &&
                    "Dividendo".equals(criteria.movimentacao()) &&
                    "PETR4".equals(criteria.produto()) &&
                    "XP INVESTIMENTOS".equals(criteria.instituicao()) &&
                    Boolean.FALSE.equals(criteria.duplicado()) &&
                    Boolean.TRUE.equals(criteria.dimensionado())
                ),
                eq(usuarioId)
            );
        }
    }
    
    @Nested
    @DisplayName("Cenários de Performance")
    class CenariosDePerformance {
        
        @Test
        @DisplayName("Deve lidar com contagem grande")
        void deveLidarComContagemGrande() {
            // Arrange
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(1_000_000L);
            
            // Act
            long result = countOperacoesUseCase.execute(validCommand);
            
            // Assert
            assertEquals(1_000_000L, result);
        }
        
        @Test
        @DisplayName("Deve lidar com múltiplas chamadas")
        void deveLidarComMultiplasChamadas() {
            // Arrange
            when(operacaoRepository.countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId)))
                .thenReturn(100L, 200L, 300L);
            
            // Act
            long result1 = countOperacoesUseCase.execute(validCommand);
            long result2 = countOperacoesUseCase.execute(validCommand);
            long result3 = countOperacoesUseCase.execute(validCommand);
            
            // Assert
            assertEquals(100L, result1);
            assertEquals(200L, result2);
            assertEquals(300L, result3);
            
            verify(operacaoRepository, times(3)).countByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId));
        }
    }
}