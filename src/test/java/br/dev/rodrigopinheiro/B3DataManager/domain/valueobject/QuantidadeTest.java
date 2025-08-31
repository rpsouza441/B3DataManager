package br.dev.rodrigopinheiro.B3DataManager.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class QuantidadeTest {
    
    @Test
    void deveCriarQuantidadeValida() {
        // Arrange & Act
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(100));
        
        // Assert
        assertEquals(0, BigDecimal.valueOf(100).compareTo(quantidade.value()));
    }
    
    @Test
    void deveCriarQuantidadeComDouble() {
        // Arrange & Act
        Quantidade quantidade = new Quantidade(100.5);
        
        // Assert
        assertEquals(0, BigDecimal.valueOf(100.5).compareTo(quantidade.value()));
    }
    
    @Test
    void deveCriarQuantidadeComString() {
        // Arrange & Act
        Quantidade quantidade = new Quantidade("100.75");
        
        // Assert
        assertEquals(0, new BigDecimal("100.75").compareTo(quantidade.value()));
    }
    
    @Test
    void deveRejeitarQuantidadeNula() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Quantidade((BigDecimal) null);
        });
        
        assertEquals("Quantidade não pode ser nula", exception.getMessage());
    }
    
    @Test
    void deveRejeitarQuantidadeZero() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Quantidade(BigDecimal.ZERO);
        });
        
        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }
    
    @Test
    void deveRejeitarQuantidadeNegativa() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Quantidade(BigDecimal.valueOf(-10));
        });
        
        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }
    
    @Test
    void deveSomarQuantidades() {
        // Arrange
        Quantidade quantidade1 = new Quantidade(BigDecimal.valueOf(100));
        Quantidade quantidade2 = new Quantidade(BigDecimal.valueOf(50));
        
        // Act
        Quantidade resultado = quantidade1.add(quantidade2);
        
        // Assert
        assertEquals(0, BigDecimal.valueOf(150).compareTo(resultado.value()));
    }
    
    @Test
    void deveSubtrairQuantidades() {
        // Arrange
        Quantidade quantidade1 = new Quantidade(BigDecimal.valueOf(100));
        Quantidade quantidade2 = new Quantidade(BigDecimal.valueOf(30));
        
        // Act
        Quantidade resultado = quantidade1.subtract(quantidade2);
        
        // Assert
        assertEquals(0, BigDecimal.valueOf(70).compareTo(resultado.value()));
    }
    
    @Test
    void deveMultiplicarQuantidade() {
        // Arrange
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(100));
        BigDecimal multiplicador = BigDecimal.valueOf(2.5);
        
        // Act
        Quantidade resultado = quantidade.multiply(multiplicador);
        
        // Assert
        assertEquals(0, BigDecimal.valueOf(250).compareTo(resultado.value()));
    }
    
    @Test
    void deveImplementarEqualsCorretamente() {
        // Arrange
        Quantidade quantidade1 = new Quantidade(BigDecimal.valueOf(100));
        Quantidade quantidade2 = new Quantidade(BigDecimal.valueOf(100));
        Quantidade quantidade3 = new Quantidade(BigDecimal.valueOf(200));
        
        // Assert
        assertEquals(quantidade1, quantidade2);
        assertNotEquals(quantidade1, quantidade3);
        assertNotEquals(quantidade1, null);
        assertNotEquals(quantidade1, "string");
    }
    
    @Test
    void deveImplementarHashCodeCorretamente() {
        // Arrange
        Quantidade quantidade1 = new Quantidade(BigDecimal.valueOf(100));
        Quantidade quantidade2 = new Quantidade(BigDecimal.valueOf(100));
        
        // Assert
        assertEquals(quantidade1.hashCode(), quantidade2.hashCode());
    }
    
    @Test
    void deveImplementarToStringCorretamente() {
        // Arrange
        Quantidade quantidade = new Quantidade(BigDecimal.valueOf(100.5));
        
        // Act
        String toString = quantidade.toString();
        
        // Assert
        assertTrue(toString.contains("100.5"));
    }
}