package br.dev.rodrigopinheiro.B3DataManager.domain.exception.instituicao;

import br.dev.rodrigopinheiro.B3DataManager.domain.exception.B3DataManagerException;
import org.springframework.context.MessageSource;

/**
 * Exceção lançada quando uma instituição com o mesmo nome já existe.
 */
public class InstituicaoAlreadyExistsException extends B3DataManagerException {

    private final String nome;

    /**
     * Construtor para criar a exceção.
     *
     * @param nome          Nome da instituição que já existe.
     * @param messageSource O MessageSource para localizar mensagens.
     */
    public InstituicaoAlreadyExistsException(String nome, MessageSource messageSource) {
        super("instituicao.already_exists", messageSource, nome);
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
