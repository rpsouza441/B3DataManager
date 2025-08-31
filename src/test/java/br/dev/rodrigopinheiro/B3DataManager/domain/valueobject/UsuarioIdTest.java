package br.dev.rodrigopinheiro.B3DataManager.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioIdTest {
    
    @Test
    void deveCriarUsuarioIdValido() {
        // Arrange & Act
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Assert
        assertEquals(1L, usuarioId.value());
    }
    
    @Test
    void deveRejeitarUsuarioIdNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new UsuarioId(null);
        });
        
        assertEquals("UsuarioId não pode ser nulo", exception.getMessage());
    }
    
    @Test
    void deveRejeitarUsuarioIdZero() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new UsuarioId(0L);
        });
        
        assertEquals("UsuarioId deve ser um valor positivo", exception.getMessage());
    }
    
    @Test
    void deveRejeitarUsuarioIdNegativo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new UsuarioId(-1L);
        });
        
        assertEquals("UsuarioId deve ser um valor positivo", exception.getMessage());
    }
    
    @Test
    void deveImplementarEqualsCorretamente() {
        // Arrange
        UsuarioId usuarioId1 = new UsuarioId(1L);
        UsuarioId usuarioId2 = new UsuarioId(1L);
        UsuarioId usuarioId3 = new UsuarioId(2L);
        
        // Assert
        assertEquals(usuarioId1, usuarioId2);
        assertNotEquals(usuarioId1, usuarioId3);
        assertNotEquals(usuarioId1, null);
        assertNotEquals(usuarioId1, "string");
    }
    
    @Test
    void deveImplementarHashCodeCorretamente() {
        // Arrange
        UsuarioId usuarioId1 = new UsuarioId(1L);
        UsuarioId usuarioId2 = new UsuarioId(1L);
        
        // Assert
        assertEquals(usuarioId1.hashCode(), usuarioId2.hashCode());
    }
    
    @Test
    void deveImplementarToStringCorretamente() {
        // Arrange
        UsuarioId usuarioId = new UsuarioId(1L);
        
        // Act
        String toString = usuarioId.toString();
        
        // Assert
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("UsuarioId"));
    }
}