package br.dev.rodrigopinheiro.B3DataManager.application.usecase;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.RegisterOperacaoCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.port.OperacaoRepository;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.RegisterOperacaoUseCase;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao.OperacaoInvalidaException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario.UsuarioNaoAutorizadoException;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class RegisterOperacaoUseCaseTest {
    
    private RegisterOperacaoUseCase useCase;
    private FakeOperacaoRepository fakeRepository;
    
    @BeforeEach
    void setUp() {
        fakeRepository = new FakeOperacaoRepository();
        useCase = new RegisterOperacaoUseCase(fakeRepository);
    }
    
    @Test
    void deveRegistrarOperacaoComSucesso() {
        // Arrange
        RegisterOperacaoCommand command = new RegisterOperacaoCommand(
            "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
            BigDecimal.valueOf(100), BigDecimal.valueOf(10.50), BigDecimal.valueOf(1050.00),
            false, false, null, false, 1L
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
        // Arrange
        RegisterOperacaoCommand command = new RegisterOperacaoCommand(
            "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
            BigDecimal.valueOf(100), BigDecimal.valueOf(10.50), BigDecimal.valueOf(1050.00),
            false, false, null, false, null
        );
        
        // Act & Assert
        UsuarioNaoAutorizadoException exception = assertThrows(UsuarioNaoAutorizadoException.class, () -> {
            useCase.execute(command);
        });
        
        assertEquals("UsuarioId é obrigatório para registrar uma operação", exception.getMessage());
    }
    
    @Test
    void deveRejeitarOperacaoComQuantidadeInvalida() {
        // Arrange
        RegisterOperacaoCommand command = new RegisterOperacaoCommand(
            "Compra", LocalDate.now(), "Compra à vista", "PETR4", "XP Investimentos",
            BigDecimal.valueOf(-100), BigDecimal.valueOf(10.50), BigDecimal.valueOf(1050.00),
            false, false, null, false, 1L
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
            BigDecimal.valueOf(100), BigDecimal.valueOf(10.50), BigDecimal.valueOf(-1050.00),
            false, false, null, false, 1L
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
            BigDecimal.valueOf(100), BigDecimal.valueOf(10.50), BigDecimal.valueOf(1050.00),
            false, false, idOriginal, false, 1L
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
        
        public void addExistingOperation(Long idOriginal, UsuarioId usuarioId) {
            String key = idOriginal + "_" + usuarioId.value();
            existingOperations.put(key, true);
        }
    }
}