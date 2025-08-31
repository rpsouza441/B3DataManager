package br.dev.rodrigopinheiro.B3DataManager.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object que representa uma quantidade de ativos.
 * Garante que quantidades sejam não-negativas (>= 0) e com precisão adequada.
 * Permite zero para operações como direitos não exercidos, atualizações, etc.
 */
public record Quantidade(BigDecimal value) {
    
    public Quantidade {
        if (value == null) {
            throw new IllegalArgumentException("Quantidade não pode ser nula");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        // Garantir precisão adequada para quantidades
        // Nota: Permitimos zero para operações como direitos não exercidos, atualizações, etc.
        value = value.setScale(8, RoundingMode.HALF_UP);
    }
    
    public Quantidade(double value) {
        this(BigDecimal.valueOf(value));
    }
    
    public Quantidade(String value) {
        this(new BigDecimal(value));
    }
    
    public Quantidade add(Quantidade other) {
        return new Quantidade(this.value.add(other.value));
    }
    
    public Quantidade subtract(Quantidade other) {
        return new Quantidade(this.value.subtract(other.value));
    }
    
    public Quantidade multiply(BigDecimal multiplicador) {
        return new Quantidade(this.value.multiply(multiplicador));
    }
}