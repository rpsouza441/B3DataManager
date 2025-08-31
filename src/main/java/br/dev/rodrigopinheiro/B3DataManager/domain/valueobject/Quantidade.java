package br.dev.rodrigopinheiro.B3DataManager.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object que representa uma quantidade de ativos.
 * Garante que quantidades sejam sempre positivas e com precisão adequada.
 */
public record Quantidade(BigDecimal value) {
    
    public Quantidade {
        if (value == null) {
            throw new IllegalArgumentException("Quantidade não pode ser nula");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        // Garantir precisão adequada para quantidades
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