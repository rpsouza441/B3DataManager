package br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario;

/**
 * Exceção lançada quando um usuário tenta realizar uma operação sem autorização adequada.
 */
public class UsuarioNaoAutorizadoException extends RuntimeException {
    
    public UsuarioNaoAutorizadoException(String message) {
        super(message);
    }
    
    public UsuarioNaoAutorizadoException(String message, Throwable cause) {
        super(message, cause);
    }
}