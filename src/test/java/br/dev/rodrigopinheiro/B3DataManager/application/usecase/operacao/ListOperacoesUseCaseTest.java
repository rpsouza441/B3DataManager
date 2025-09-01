package br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.ListOperacoesCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.criteria.FilterCriteria;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.ListOperacoesResult;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.OperacaoDTO;
import br.dev.rodrigopinheiro.B3DataManager.presentation.mapper.OperacaoDTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ListOperacoesUseCase.
 * 
 * <p>Testa todos os cenários de listagem de operações,
 * incluindo filtros, paginação, sucessos e erros.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ListOperacoesUseCase")
class ListOperacoesUseCaseTest {
    
    @Mock
    private OperacaoRepository operacaoRepository;
    
    @Mock
    private OperacaoDTOMapper mapper;
    
    private ListOperacoesUseCase listOperacoesUseCase;
    
    private ListOperacoesCommand validCommand;
    private UsuarioId usuarioId;
    private List<Operacao> operacoes;
    private List<OperacaoDTO> operacaoDTOs;
    
    @BeforeEach
    void setUp() {
        listOperacoesUseCase = new ListOperacoesUseCase(operacaoRepository, mapper);
        
        usuarioId = new UsuarioId(1L);
        
        validCommand = new ListOperacoesCommand(
            null, // entradaSaida
            null, // startDate
            null, // endDate
            null, // movimentacao
            null, // produto
            null, // instituicao
            null, // duplicado
            null, // dimensionado
            0,    // page
            25,   // size
            1L    // usuarioId
        );
        
        // Criar operações de exemplo
        Operacao operacao1 = new Operacao(
            1L,
            "Entrada",
            LocalDate.of(2025, 8, 29),
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
        
        Operacao operacao2 = new Operacao(
            2L,
            "Saída",
            LocalDate.of(2025, 8, 30),
            "Venda",
            "PETR4 - PETROBRAS S.A.",
            "XP INVESTIMENTOS CCTVM S.A.",
            new Quantidade(new BigDecimal("100")),
            new Dinheiro(new BigDecimal("35.50")),
            new Dinheiro(new BigDecimal("3550.00")),
            false,
            false,
            null,
            false,
            usuarioId
        );
        
        operacoes = List.of(operacao1, operacao2);
        
        // Criar DTOs de exemplo
        OperacaoDTO dto1 = new OperacaoDTO(
            1L,
            "Entrada",
            LocalDate.of(2025, 8, 29),
            "Juros Sobre Capital Próprio",
            "ITSA4 - ITAUSA S.A.",
            "INTER DISTRIBUIDORA DE TITULOS E VALORES MOBILIARIOS LTDA",
            new BigDecimal("19"),
            new BigDecimal("0.059"),
            new BigDecimal("0.96"),
            new BigDecimal("1.121"), // valor calculado
            false,
            false,
            true, // temDiferencaValor
            new BigDecimal("0.161") // diferencaValor
        );
        
        OperacaoDTO dto2 = new OperacaoDTO(
            2L,
            "Saída",
            LocalDate.of(2025, 8, 30),
            "Venda",
            "PETR4 - PETROBRAS S.A.",
            "XP INVESTIMENTOS CCTVM S.A.",
            new BigDecimal("100"),
            new BigDecimal("35.50"),
            new BigDecimal("3550.00"),
            new BigDecimal("3550.00"),
            false,
            false,
            false, // temDiferencaValor
            BigDecimal.ZERO // diferencaValor
        );
        
        operacaoDTOs = List.of(dto1, dto2);
    }
    
    @Nested
    @DisplayName("Listagem com Sucesso")
    class ListagemComSucesso {
        
        @Test
        @DisplayName("Deve listar operações sem filtros")
        void deveListarOperacoesSemFiltros() {
            // Arrange
            Page<Operacao> page = new PageImpl<>(operacoes, PageRequest.of(0, 25), 2);
            
            when(operacaoRepository.findByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId), any(Pageable.class)))
                .thenReturn(page);
            when(mapper.toDTO(operacoes.get(0))).thenReturn(operacaoDTOs.get(0));
            when(mapper.toDTO(operacoes.get(1))).thenReturn(operacaoDTOs.get(1));
            
            // Act
            ListOperacoesResult result = listOperacoesUseCase.execute(validCommand);
            
            // Assert
            assertNotNull(result);
            assertEquals(2, result.operacoes().size());
            assertEquals(1, result.totalPages());
            assertEquals(2L, result.totalElements());
            assertEquals(0, result.currentPage());
            assertEquals(25, result.pageSize());
            
            assertEquals(operacaoDTOs.get(0), result.operacoes().get(0));
            assertEquals(operacaoDTOs.get(1), result.operacoes().get(1));
            
            verify(operacaoRepository).findByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId), any(Pageable.class));
            verify(mapper, times(2)).toDTO(any(Operacao.class));
        }
        
        @Test
        @DisplayName("Deve listar operações com filtros")
        void deveListarOperacoesComFiltros() {
            // Arrange
            ListOperacoesCommand commandComFiltros = new ListOperacoesCommand(
                "Entrada",
                LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 8, 31),
                "Juros",
                "ITSA4",
                "INTER",
                false,
                false,
                0,
                25,
                1L
            );
            
            Page<Operacao> page = new PageImpl<>(List.of(operacoes.get(0)), PageRequest.of(0, 25), 1);
            
            when(operacaoRepository.findByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId), any(Pageable.class)))
                .thenReturn(page);
            when(mapper.toDTO(operacoes.get(0))).thenReturn(operacaoDTOs.get(0));
            
            // Act
            ListOperacoesResult result = listOperacoesUseCase.execute(commandComFiltros);
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.operacoes().size());
            assertEquals(1, result.totalPages());
            assertEquals(1L, result.totalElements());
            
            verify(operacaoRepository).findByFiltersAndUsuarioId(
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
                eq(usuarioId),
                any(Pageable.class)
            );
        }
        
        @Test
        @DisplayName("Deve listar operações com paginação")
        void deveListarOperacoesComPaginacao() {
            // Arrange
            ListOperacoesCommand commandPaginado = new ListOperacoesCommand(
                null, null, null, null, null, null, null, null,
                1, // página 1
                10, // 10 itens por página
                1L
            );
            
            Page<Operacao> page = new PageImpl<>(List.of(operacoes.get(1)), PageRequest.of(1, 10), 2);
            
            when(operacaoRepository.findByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId), any(Pageable.class)))
                .thenReturn(page);
            when(mapper.toDTO(operacoes.get(1))).thenReturn(operacaoDTOs.get(1));
            
            // Act
            ListOperacoesResult result = listOperacoesUseCase.execute(commandPaginado);
            
            // Assert
            assertNotNull(result);
            assertEquals(1, result.operacoes().size());
            assertEquals(1, result.totalPages());
            assertEquals(2L, result.totalElements());
            assertEquals(1, result.currentPage());
            assertEquals(10, result.pageSize());
            
            verify(operacaoRepository).findByFiltersAndUsuarioId(
                any(FilterCriteria.class),
                eq(usuarioId),
                eq(PageRequest.of(1, 10))
            );
        }
        
        @Test
        @DisplayName("Deve retornar lista vazia quando não há operações")
        void deveRetornarListaVaziaQuandoNaoHaOperacoes() {
            // Arrange
            Page<Operacao> pageVazia = new PageImpl<>(List.of(), PageRequest.of(0, 25), 0);
            
            when(operacaoRepository.findByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId), any(Pageable.class)))
                .thenReturn(pageVazia);
            
            // Act
            ListOperacoesResult result = listOperacoesUseCase.execute(validCommand);
            
            // Assert
            assertNotNull(result);
            assertTrue(result.operacoes().isEmpty());
            assertEquals(0, result.totalPages());
            assertEquals(0L, result.totalElements());
            
            verify(mapper, never()).toDTO(any(Operacao.class));
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
                () -> listOperacoesUseCase.execute(null)
            );
            
            assertEquals("Comando não pode ser nulo", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar usuário nulo")
        void deveRejeitarUsuarioNulo() {
            // Arrange
            ListOperacoesCommand commandUsuarioNulo = new ListOperacoesCommand(
                null, null, null, null, null, null, null, null,
                0, 25, null // usuarioId nulo
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> listOperacoesUseCase.execute(commandUsuarioNulo)
            );
            
            assertEquals("ID do usuário é obrigatório", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar página negativa")
        void deveRejeitarPaginaNegativa() {
            // Arrange
            ListOperacoesCommand commandPaginaNegativa = new ListOperacoesCommand(
                null, null, null, null, null, null, null, null,
                -1, // página negativa
                25, 1L
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> listOperacoesUseCase.execute(commandPaginaNegativa)
            );
            
            assertEquals("Número da página deve ser maior ou igual a zero", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar tamanho de página zero")
        void deveRejeitarTamanhoDePaginaZero() {
            // Arrange
            ListOperacoesCommand commandTamanhoZero = new ListOperacoesCommand(
                null, null, null, null, null, null, null, null,
                0, 0, // tamanho zero
                1L
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> listOperacoesUseCase.execute(commandTamanhoZero)
            );
            
            assertEquals("Tamanho da página deve ser maior que zero", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve rejeitar tamanho de página muito grande")
        void deveRejeitarTamanhoDePaginaMuitoGrande() {
            // Arrange
            ListOperacoesCommand commandTamanhoGrande = new ListOperacoesCommand(
                null, null, null, null, null, null, null, null,
                0, 1001, // tamanho muito grande
                1L
            );
            
            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> listOperacoesUseCase.execute(commandTamanhoGrande)
            );
            
            assertEquals("Tamanho da página não pode exceder 1000 itens", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve aceitar tamanho de página no limite")
        void deveAceitarTamanhoDePaginaNoLimite() {
            // Arrange
            ListOperacoesCommand commandTamanhoLimite = new ListOperacoesCommand(
                null, null, null, null, null, null, null, null,
                0, 1000, // tamanho no limite
                1L
            );
            
            Page<Operacao> page = new PageImpl<>(List.of(), PageRequest.of(0, 1000), 0);
            when(operacaoRepository.findByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId), any(Pageable.class)))
                .thenReturn(page);
            
            // Act & Assert - Não deve lançar exceção
            assertDoesNotThrow(() -> listOperacoesUseCase.execute(commandTamanhoLimite));
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
            
            when(operacaoRepository.findByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId), any(Pageable.class)))
                .thenThrow(dataException);
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> listOperacoesUseCase.execute(validCommand)
            );
            
            assertEquals("Erro interno ao listar operações", exception.getMessage());
            assertEquals(dataException, exception.getCause());
        }
        
        @Test
        @DisplayName("Deve tratar erro inesperado")
        void deveTratarErroInesperado() {
            // Arrange
            RuntimeException unexpectedException = new RuntimeException("Erro inesperado");
            
            when(operacaoRepository.findByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId), any(Pageable.class)))
                .thenThrow(unexpectedException);
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> listOperacoesUseCase.execute(validCommand)
            );
            
            assertEquals("Erro inesperado ao listar operações", exception.getMessage());
            assertEquals(unexpectedException, exception.getCause());
        }
        
        @Test
        @DisplayName("Deve tratar erro no mapeamento")
        void deveTratarErroNoMapeamento() {
            // Arrange
            Page<Operacao> page = new PageImpl<>(operacoes, PageRequest.of(0, 25), 2);
            
            when(operacaoRepository.findByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId), any(Pageable.class)))
                .thenReturn(page);
            when(mapper.toDTO(any(Operacao.class)))
                .thenThrow(new RuntimeException("Erro no mapeamento"));
            
            // Act & Assert
            RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> listOperacoesUseCase.execute(validCommand)
            );
            
            assertEquals("Erro inesperado ao listar operações", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Integração com Repository")
    class IntegracaoComRepository {
        
        @Test
        @DisplayName("Deve passar critérios corretos para o repository")
        void devePassarCriteriosCorretosParaORepository() {
            // Arrange
            Page<Operacao> page = new PageImpl<>(List.of(), PageRequest.of(0, 25), 0);
            when(operacaoRepository.findByFiltersAndUsuarioId(any(FilterCriteria.class), eq(usuarioId), any(Pageable.class)))
                .thenReturn(page);
            
            // Act
            listOperacoesUseCase.execute(validCommand);
            
            // Assert
            verify(operacaoRepository).findByFiltersAndUsuarioId(
                any(FilterCriteria.class),
                eq(usuarioId),
                eq(PageRequest.of(0, 25))
            );
        }
        
        @Test
        @DisplayName("Deve garantir ownership obrigatório")
        void deveGarantirOwnershipObrigatorio() {
            // Arrange
            Page<Operacao> page = new PageImpl<>(List.of(), PageRequest.of(0, 25), 0);
            when(operacaoRepository.findByFiltersAndUsuarioId(any(FilterCriteria.class), any(UsuarioId.class), any(Pageable.class)))
                .thenReturn(page);
            
            // Act
            listOperacoesUseCase.execute(validCommand);
            
            // Assert
            verify(operacaoRepository).findByFiltersAndUsuarioId(
                any(FilterCriteria.class),
                eq(usuarioId), // Verifica que o usuário é sempre passado
                any(Pageable.class)
            );
        }
    }
}