package br.dev.rodrigopinheiro.B3DataManager.domain.valueobject;

/**
 * Value Object que encapsula o identificador de um usuário.
 * Garante que o ID seja válido e não nulo.
 */
public record UsuarioId(Long value) {
    
    public UsuarioId {
        if (value == null) {
            throw new IllegalArgumentException("UsuarioId não pode ser nulo");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("UsuarioId deve ser um valor positivo");
        }
    }
}