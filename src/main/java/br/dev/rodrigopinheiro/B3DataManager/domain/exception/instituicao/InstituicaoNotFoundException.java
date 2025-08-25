package br.dev.rodrigopinheiro.B3DataManager.domain.exception.instituicao;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção lançada quando uma instituição não é encontrada pelo ID.
 */
public class InstituicaoNotFoundException extends B3DataManagerException {

    private final Long id;

    /**
     * Construtor para criar a exceção.
     *
     * @param id            ID da instituição não encontrada.
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public InstituicaoNotFoundException(Long id, MessageSource messageSource) {
        super("instituicao.not_found", messageSource, id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
