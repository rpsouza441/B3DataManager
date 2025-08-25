package br.dev.rodrigopinheiro.B3DataManager.domain.exception;

/**
 * Exceção genérica para erros relacionados a serviços da aplicação.
 * Usada para encapsular problemas específicos de regras de negócio
 * ou falhas durante a execução de operações no nível de serviço.
 */
public class ServiceException extends RuntimeException {

    /**
     * Construtor com mensagem de erro.
     *
     * @param message Mensagem explicativa sobre o erro ocorrido.
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa do erro.
     *
     * @param message Mensagem explicativa sobre o erro ocorrido.
     * @param cause   Causa original do erro (stacktrace encapsulado).
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
