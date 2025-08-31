package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao.OperacaoInvalidaException;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OperacaoTest {
    
    @Test
    void deveCriarOperacaoValida() {
        // Arrange
        LocalDate data = LocalDate.now();
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(100));
        Dinheiro precoUnitario = new Dinheiro(BigDecimal.valueOf(10.50));
        Dinheiro valorOperacao = new Dinheiro(BigDecimal.valueOf(1050.00));
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Act
        Operacao operacao = new Operacao(
            null, "Compra", data, "Compra à vista", "PETR4", "XP Investimentos",
            quantidade, precoUnitario, valorOperacao, false, false, null, false, usuarioId
        );
        
        // Assert
        assertNotNull(operacao);
        assertEquals(data, operacao.getData());
        assertEquals(quantidade, operacao.getQuantidade());
        assertEquals(precoUnitario, operacao.getPrecoUnitario());
        assertEquals(valorOperacao, operacao.getValorOperacao());
        assertEquals(usuarioId, operacao.getUsuarioId());
    }
    
    @Test
    void deveRejeitarOperacaoComDataNula() {
        // Arrange
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(100));
        Dinheiro precoUnitario = new Dinheiro(BigDecimal.valueOf(10.50));
        Dinheiro valorOperacao = new Dinheiro(BigDecimal.valueOf(1050.00));
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Act & Assert
        OperacaoInvalidaException exception = assertThrows(OperacaoInvalidaException.class, () -> {
            new Operacao(
                null, "Compra", null, "Compra à vista", "PETR4", "XP Investimentos",
                quantidade, precoUnitario, valorOperacao, false, false, null, false, usuarioId
            );
        });
        
        assertEquals("Data da operação não pode ser nula", exception.getMessage());
    }
    
    @Test
    void deveRejeitarOperacaoComQuantidadeNula() {
        // Arrange
        LocalDate data = LocalDate.now();
        Dinheiro precoUnitario = new Dinheiro(BigDecimal.valueOf(10.50));
        Dinheiro valorOperacao = new Dinheiro(BigDecimal.valueOf(1050.00));
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Act & Assert
        OperacaoInvalidaException exception = assertThrows(OperacaoInvalidaException.class, () -> {
            new Operacao(
                null, "Compra", data, "Compra à vista", "PETR4", "XP Investimentos",
                null, precoUnitario, valorOperacao, false, false, null, false, usuarioId
            );
        });
        
        assertEquals("Quantidade não pode ser nula", exception.getMessage());
    }
    
    @Test
    void deveRejeitarOperacaoComPrecoUnitarioNulo() {
        // Arrange
        LocalDate data = LocalDate.now();
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(100));
        Dinheiro valorOperacao = new Dinheiro(BigDecimal.valueOf(1050.00));
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Act & Assert
        OperacaoInvalidaException exception = assertThrows(OperacaoInvalidaException.class, () -> {
            new Operacao(
                null, "Compra", data, "Compra à vista", "PETR4", "XP Investimentos",
                quantidade, null, valorOperacao, false, false, null, false, usuarioId
            );
        });
        
        assertEquals("Preço unitário não pode ser nulo", exception.getMessage());
    }
    
    @Test
    void deveRejeitarOperacaoComValorOperacaoNulo() {
        // Arrange
        LocalDate data = LocalDate.now();
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(100));
        Dinheiro precoUnitario = new Dinheiro(BigDecimal.valueOf(10.50));
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Act & Assert
        OperacaoInvalidaException exception = assertThrows(OperacaoInvalidaException.class, () -> {
            new Operacao(
                null, "Compra", data, "Compra à vista", "PETR4", "XP Investimentos",
                quantidade, precoUnitario, null, false, false, null, false, usuarioId
            );
        });
        
        assertEquals("Valor da operação não pode ser nulo", exception.getMessage());
    }
    
    @Test
    void deveRejeitarOperacaoSemUsuarioId() {
        // Arrange
        LocalDate data = LocalDate.now();
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(100));
        Dinheiro precoUnitario = new Dinheiro(BigDecimal.valueOf(10.50));
        Dinheiro valorOperacao = new Dinheiro(BigDecimal.valueOf(1050.00));
        
        // Act & Assert
        OperacaoInvalidaException exception = assertThrows(OperacaoInvalidaException.class, () -> {
            new Operacao(
                null, "Compra", data, "Compra à vista", "PETR4", "XP Investimentos",
                quantidade, precoUnitario, valorOperacao, false, false, null, false, null
            );
        });
        
        assertEquals("UsuarioId é obrigatório para toda operação", exception.getMessage());
    }
    
    @Test
    void deveRejeitarOperacaoComValorIncoerenteComPrecoEQuantidade() {
        // Arrange
        LocalDate data = LocalDate.now();
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(100));
        Dinheiro precoUnitario = new Dinheiro(BigDecimal.valueOf(10.50));
        Dinheiro valorOperacao = new Dinheiro(BigDecimal.valueOf(2000.00)); // Valor incorreto
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Act & Assert
        OperacaoInvalidaException exception = assertThrows(OperacaoInvalidaException.class, () -> {
            new Operacao(
                null, "Compra", data, "Compra à vista", "PETR4", "XP Investimentos",
                quantidade, precoUnitario, valorOperacao, false, false, null, false, usuarioId
            );
        });
        
        assertTrue(exception.getMessage().contains("Valor da operação"));
        assertTrue(exception.getMessage().contains("não confere com preço × quantidade"));
    }
    
    @Test
    void deveAceitarOperacaoComValorDentroTolerancia() {
        // Arrange
        LocalDate data = LocalDate.now();
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(100));
        Dinheiro precoUnitario = new Dinheiro(BigDecimal.valueOf(10.50));
        Dinheiro valorOperacao = new Dinheiro(BigDecimal.valueOf(1050.01)); // Diferença de 1 centavo
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Act & Assert - Não deve lançar exceção
        assertDoesNotThrow(() -> {
            new Operacao(
                null, "Compra", data, "Compra à vista", "PETR4", "XP Investimentos",
                quantidade, precoUnitario, valorOperacao, false, false, null, false, usuarioId
            );
        });
    }
}