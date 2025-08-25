package br.dev.rodrigopinheiro.B3DataManager.domain.exception.api;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Exceção lançada quando ocorre um erro durante a comunicação com uma API externa.
 */
public class ApiClientException extends RuntimeException {

    public ApiClientException(MessageSource messageSource, String detalhe) {
        super(messageSource.getMessage("error.api.client", new Object[]{detalhe}, LocaleContextHolder.getLocale()));
    }
}
