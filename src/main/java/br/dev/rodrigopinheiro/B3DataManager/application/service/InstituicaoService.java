package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Instituicao;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Usuario;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.instituicao.InstituicaoAlreadyExistsException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.instituicao.InstituicaoNotFoundException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.instituicao.InvalidInstituicaoNameException;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.InstituicaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
public class InstituicaoService {

    private final InstituicaoRepository instituicaoRepository;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;


    public InstituicaoService(InstituicaoRepository instituicaoRepository, UsuarioService usuarioService, MessageSource messageSource) {
        this.instituicaoRepository = instituicaoRepository;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    /**
     * Cria uma nova instituição e a associa a um usuário.
     *
     * @param nome     Nome da instituição.
     * @param usuarioId ID do usuário a ser associado.
     * @return Instituição criada.
     */
    @Transactional
    public Instituicao criarInstituicao(String nome, Long usuarioId, Locale locale) {
        log.info("Iniciando criação de instituição com nome: {} e usuário ID: {}", nome, usuarioId);
        checkIfNullOrEmpty(nome, locale);

        // Verifica se já existe uma instituição com o mesmo nome
        if (instituicaoRepository.findByNome(nome).isPresent()) {
            log.warn("Tentativa de criar uma instituição com nome duplicado: {}", nome);
            throw new InstituicaoAlreadyExistsException(nome, messageSource);
        }

        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);

        // Criar a instituição e associá-la ao usuário
        Instituicao novaInstituicao = new Instituicao();
        novaInstituicao.setNome(nome);
        novaInstituicao.setUsuarios(List.of(usuario)); // Associa ao usuário


        Instituicao instituicaoCriada = instituicaoRepository.save(novaInstituicao);
        log.info("Instituição criada com sucesso: {}", instituicaoCriada);
        return instituicaoCriada;    }



    /**
     * Atualiza uma instituição existente.
     *
     * @param id       ID da instituição a ser atualizada.
     * @param nome     Novo nome da instituição (opcional).
     * @param usuarioId ID do usuário a ser associado.
     * @return Instituição atualizada.
     */
    @Transactional
    public Instituicao atualizarInstituicao(Long id, String nome, Long usuarioId, Locale locale) {
        log.info("Atualizando instituição com ID: {}", id);

        Instituicao instituicao = getInstituicaoById(id, locale);

        if (nome != null && !nome.isEmpty()) {
            log.debug("Atualizando nome da instituição para: {}", nome);
            instituicao.setNome(nome);
        }

        if (usuarioId != null) {
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
            if (!instituicao.getUsuarios().contains(usuario)) {
                log.debug("Adicionando usuário ID: {} à instituição ID: {}", usuarioId, id);
                instituicao.getUsuarios().add(usuario);
            }
        }

        Instituicao instituicaoAtualizada = instituicaoRepository.save(instituicao);
        log.info("Instituição atualizada com sucesso: {}", instituicaoAtualizada);
        return instituicaoAtualizada;
    }

    /**
     * Busca uma instituição pelo ID.
     *
     * @param id ID da instituição.
     * @return Instituição encontrada.
     */
    public Instituicao buscarPorId(Long id, Locale locale) {
        log.info("Buscando instituição com ID: {}", id);

        return getInstituicaoById(id, locale);
    }

    /**
     * Lista todas as instituições cadastradas.
     *
     * @return Lista de instituições.
     */
    public List<Instituicao> listarTodas() {
        log.info("Listando todas as instituições cadastradas.");
        return instituicaoRepository.findAll();
    }

    /**
     * Exclui logicamente uma instituição.
     *
     * @param id ID da instituição a ser excluída.
     */
    @Transactional
    public void excluirInstituicao(Long id, Locale locale) {
        log.info("Excluindo instituição com ID: {}", id);

        Instituicao instituicao = instituicaoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tentativa de excluir instituição não encontrada com ID: {}", id);
                    return new InstituicaoNotFoundException(id, messageSource);
                });

        instituicaoRepository.delete(instituicao);
        log.info("Instituição excluída com sucesso: {}", id);
    }

    /**
     * Verifica se já existe uma instituição pelo nome e, se não existir, cria uma nova associada ao usuário.
     *
     * @param nome     Nome da instituição.
     * @param usuarioId ID do usuário a ser associado.
     * @return Instituição existente ou criada.
     */
    @Transactional
    public Instituicao verificarOuCriarInstituicao(String nome, Long usuarioId, Locale locale) {
        log.info("Verificando ou criando instituição com nome: {} e usuário ID: {}", nome, usuarioId);

        checkIfNullOrEmpty(nome, locale);

        return instituicaoRepository.findByNome(nome)
                .orElseGet(() -> {
                    log.debug("Nenhuma instituição encontrada com o nome: {}, criando nova.", nome);
                    return criarInstituicao(nome, usuarioId, locale);
                });
    }

    public Instituicao buscarOuCriarInstituicao(String nome) {
        Optional<Instituicao> optionalInstituicao = instituicaoRepository.findByNome(nome);
        return optionalInstituicao.orElseGet(() -> {
            Instituicao novaInstituicao = new Instituicao();
            novaInstituicao.setNome(nome);
            // Defina outros atributos mínimos se necessário.
            return instituicaoRepository.save(novaInstituicao);
        });
    }

    private void checkIfNullOrEmpty(String nome, Locale locale) {
        if (nome == null || nome.isEmpty()) {
            log.error("O nome da instituição é obrigatório.");
            throw new InvalidInstituicaoNameException(messageSource);
        }
    }

    private Instituicao getInstituicaoById(Long id, Locale locale) {
        return instituicaoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Instituição não encontrada com ID: {}", id);
                    return new InstituicaoNotFoundException(id, messageSource);
                });
    }


}
