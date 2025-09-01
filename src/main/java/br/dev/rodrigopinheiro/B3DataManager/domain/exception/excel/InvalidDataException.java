package br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel;

/**
 * Exceção lançada quando dados inválidos são encontrados durante processamento de Excel.
 * 
 * <p>Simplificada para não depender de MessageSource, seguindo princípios da arquitetura hexagonal.</p>
 */
public class InvalidDataException extends RuntimeException {

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem descritiva do erro
     */
    public InvalidDataException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem de erro e causa.
     *
     * @param message Mensagem descritiva do erro
     * @param cause Exceção original que causou o erro
     */
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
