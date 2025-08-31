package br.dev.rodrigopinheiro.B3DataManager.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object que representa um valor monetário.
 * Garante que valores monetários sejam sempre não negativos e com precisão adequada.
 */
public final class Dinheiro {
    
    private final BigDecimal value;
    
    public Dinheiro(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Valor monetário não pode ser nulo");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor monetário não pode ser negativo");
        }
        // Garantir precisão de 2 casas decimais para valores monetários
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }
    
    public Dinheiro(double value) {
        this(BigDecimal.valueOf(value));
    }
    
    public Dinheiro(String value) {
        this(new BigDecimal(value));
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public Dinheiro add(Dinheiro other) {
        return new Dinheiro(this.value.add(other.value));
    }
    
    public Dinheiro subtract(Dinheiro other) {
        return new Dinheiro(this.value.subtract(other.value));
    }
    
    public Dinheiro multiply(BigDecimal multiplicador) {
        return new Dinheiro(this.value.multiply(multiplicador));
    }
    
    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dinheiro dinheiro = (Dinheiro) o;
        return Objects.equals(value, dinheiro.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "R$ " + value.toString();
    }
}