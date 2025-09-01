package br.dev.rodrigopinheiro.B3DataManager.application.usecase;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.RegisterOperacaoCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.criteria.FilterCriteria;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.RegisterOperacaoUseCase;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.transacao.CreateTransacaoUseCase;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao.OperacaoInvalidaException;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegisterOperacaoUseCaseTest {
    
    private RegisterOperacaoUseCase useCase;
    private FakeOperacaoRepository fakeRepository;
    
    @Mock
    private CreateTransacaoUseCase createTransacaoUseCase;
    
    @BeforeEach
    void setUp() {
        fakeRepository = new FakeOperacaoRepository();
        useCase = new RegisterOperacaoUseCase(fakeRepository, createTransacaoUseCase);
    }
    
    @Test
    void deveRegistrarOperacaoComSucesso() {
        // Arrange
        RegisterOperacaoCommand command = new RegisterOperacaoCommand(
            "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
            new Quantidade(BigDecimal.valueOf(100)), 
            new Dinheiro(BigDecimal.valueOf(10.50)), 
            new Dinheiro(BigDecimal.valueOf(1050.00)),
            false, false, null, false, new UsuarioId(1L)
        );
        
        // Act
        Operacao resultado = useCase.execute(command);
        
        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("PETR4", resultado.getProduto());
        assertEquals(new UsuarioId(1L), resultado.getUsuarioId());
        assertEquals(1, fakeRepository.operacoes.size());
    }
    
    @Test
    void deveRejeitarOperacaoSemUsuarioId() {
        // Act & Assert - O command já valida no constructor
        assertThrows(NullPointerException.class, () -> {
            new RegisterOperacaoCommand(
                "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
                new Quantidade(BigDecimal.valueOf(100)), 
                new Dinheiro(BigDecimal.valueOf(10.50)), 
                new Dinheiro(BigDecimal.valueOf(1050.00)),
                false, false, null, false, null // usuarioId null
            );
        });
    }
    
    @Test
    void deveRejeitarOperacaoComQuantidadeInvalida() {
        // Arrange
        RegisterOperacaoCommand command = new RegisterOperacaoCommand(
            "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
            new Quantidade(BigDecimal.valueOf(-100)), 
            new Dinheiro(BigDecimal.valueOf(10.50)), 
            new Dinheiro(BigDecimal.valueOf(1050.00)),
            false, false, null, false, new UsuarioId(1L)
        );
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute(command);
        });
        
        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }
    
    @Test
    void deveRejeitarOperacaoComValorNegativo() {
        // Arrange
        RegisterOperacaoCommand command = new RegisterOperacaoCommand(
            "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
            new Quantidade(BigDecimal.valueOf(100)), 
            new Dinheiro(BigDecimal.valueOf(10.50)), 
            new Dinheiro(BigDecimal.valueOf(-1050.00)),
            false, false, null, false, new UsuarioId(1L)
        );
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute(command);
        });
        
        assertEquals("Valor monetário não pode ser negativo", exception.getMessage());
    }
    
    @Test
    void deveRejeitarOperacaoDuplicada() {
        // Arrange
        Long idOriginal = 123L;
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Simular operação já existente
        fakeRepository.addExistingOperation(idOriginal, usuarioId);
        
        RegisterOperacaoCommand command = new RegisterOperacaoCommand(
            "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
            new Quantidade(BigDecimal.valueOf(100)), 
            new Dinheiro(BigDecimal.valueOf(10.50)), 
            new Dinheiro(BigDecimal.valueOf(1050.00)),
            false, false, idOriginal, false, new UsuarioId(1L)
        );
        
        // Act & Assert
        OperacaoInvalidaException exception = assertThrows(OperacaoInvalidaException.class, () -> {
            useCase.execute(command);
        });
        
        assertTrue(exception.getMessage().contains("Já existe uma operação com idOriginal"));
    }
    
    /**
     * Implementação fake do repositório para testes.
     */
    private static class FakeOperacaoRepository implements OperacaoRepository {
        
        private final Map<Long, Operacao> operacoes = new HashMap<>();
        private final Map<String, Boolean> existingOperations = new HashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(1);
        
        @Override
        public Operacao save(Operacao operacao) {
            Long id = idGenerator.getAndIncrement();
            
            // Criar nova operação com ID gerado
            Operacao savedOperacao = new Operacao(
                id, operacao.getEntradaSaida(), operacao.getData(), operacao.getMovimentacao(),
                operacao.getProduto(), operacao.getInstituicao(), operacao.getQuantidade(),
                operacao.getPrecoUnitario(), operacao.getValorOperacao(), operacao.getDuplicado(),
                operacao.getDimensionado(), operacao.getIdOriginal(), operacao.getDeletado(),
                operacao.getUsuarioId()
            );
            
            operacoes.put(id, savedOperacao);
            return savedOperacao;
        }
        
        @Override
        public Optional<Operacao> findById(Long id) {
            return Optional.ofNullable(operacoes.get(id));
        }
        
        @Override
        public boolean existsByIdOriginalAndUsuarioId(Long idOriginal, UsuarioId usuarioId) {
            String key = idOriginal + "_" + usuarioId.value();
            return existingOperations.getOrDefault(key, false);
        }
        
        @Override
        public Optional<Operacao> findByIdOriginalAndUsuarioId(Long idOriginal, UsuarioId usuarioId) {
            return operacoes.values().stream()
                .filter(op -> idOriginal.equals(op.getIdOriginal()) && usuarioId.equals(op.getUsuarioId()))
                .findFirst();
        }
        
        @Override
        public Page<Operacao> findByFiltersAndUsuarioId(FilterCriteria criteria, UsuarioId usuarioId, Pageable pageable) {
            // Implementação simples para testes - retorna lista vazia
            return Page.empty();
        }
        
        @Override
        public long countByFiltersAndUsuarioId(FilterCriteria criteria, UsuarioId usuarioId) {
            // Implementação simples para testes - retorna 0
            return 0;
        }
        
        @Override
        public Optional<Operacao> findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuarioId(
                java.time.LocalDate data, String movimentacao, String produto, String instituicao,
                java.math.BigDecimal quantidade, java.math.BigDecimal precoUnitario, java.math.BigDecimal valorOperacao,
                boolean duplicado, UsuarioId usuarioId) {
            // Implementação simples para testes - retorna vazio
            return Optional.empty();
        }
        
        @Override
        public java.util.List<Operacao> findByDimensionadoAndDuplicadoWithPagination(
                boolean dimensionado, boolean duplicado, int pageSize, int offset) {
            // Implementação simples para testes - retorna lista vazia
            return java.util.List.of();
        }
        
        @Override
        public long countByDimensionadoAndDuplicado(boolean dimensionado, boolean duplicado) {
            // Implementação simples para testes - retorna 0
            return 0;
        }
        
        public void addExistingOperation(Long idOriginal, UsuarioId usuarioId) {
            String key = idOriginal + "_" + usuarioId.value();
            existingOperations.put(key, true);
        }
    }
}