package br.dev.rodrigopinheiro.B3DataManager.domain.exception.usuario;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class UsuarioNotFoundException extends B3DataManagerException {

    private final Long usuarioId;
    private final MessageSource messageSource;


    public UsuarioNotFoundException(Long usuarioId,  MessageSource messageSource) {
        super("usuario.not_found", messageSource);
        this.usuarioId = usuarioId;
        this.messageSource = messageSource;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
}
