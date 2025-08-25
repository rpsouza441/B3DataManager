package br.dev.rodrigopinheiro.B3DataManager.domain.exception;

import org.springframework.context.MessageSource;

public class PortfolioNotFoundException extends B3DataManagerException {

    private final Long usuarioId;
    private final MessageSource messageSource;

    public PortfolioNotFoundException(Long usuarioId, MessageSource messageSource) {
        super("portfolio.not_found", messageSource);
        this.usuarioId = usuarioId;
        this.messageSource = messageSource;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
}
