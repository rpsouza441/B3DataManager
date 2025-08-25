package br.dev.rodrigopinheiro.B3DataManager.domain.exception.rendavariavel;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção personalizada para indicar erros relacionados à entidade de Renda Variável.
 */
public class InvalidRendaVariavelException extends B3DataManagerException {

    /**
     * Construtor para criar a exceção com base no MessageSource.
     *
     * @param messageKey    A chave da mensagem no arquivo de mensagens.
     * @param messageSource O MessageSource para localizar mensagens.
     * @param args          Argumentos opcionais para formatação da mensagem.
     */
    public InvalidRendaVariavelException(String messageKey, MessageSource messageSource, Object... args) {
        super(messageKey, messageSource, args);
    }
}
