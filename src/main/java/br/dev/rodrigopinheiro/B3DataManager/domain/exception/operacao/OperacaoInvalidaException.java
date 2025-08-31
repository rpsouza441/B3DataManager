package br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao;

/**
 * Exceção lançada quando uma operação não atende aos invariantes do domínio.
 */
public class OperacaoInvalidaException extends RuntimeException {
    
    public OperacaoInvalidaException(String message) {
        super(message);
    }
    
    public OperacaoInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}