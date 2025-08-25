package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.application.persistence.AggregatePersistenceService;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Transacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Usuario;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.excel.InvalidDataException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao.OperacaoNotFoundException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.operacao.InvalidFilterException;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.PortfolioSaldoService;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.TransacaoFactory;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.OperacaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
public class OperacaoService {

    private final OperacaoRepository operacaoRepository;
    private static final DateTimeFormatter DATABASE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final MessageSource messageSource;
    private final  UsuarioService usuarioService;

    private final TransacaoService transacaoService;
    private final AtivoFinanceiroService ativoFinanceiroService;


    public OperacaoService(OperacaoRepository operacaoRepository,
                           MessageSource messageSource,
                           UsuarioService usuarioService,
                           TransacaoService transacaoService, AtivoFinanceiroService ativoFinanceiroService) {
        this.operacaoRepository = operacaoRepository;
        this.messageSource = messageSource;
        this.usuarioService = usuarioService;
        this.transacaoService = transacaoService;
        this.ativoFinanceiroService = ativoFinanceiroService;
    }

    /**
     * Busca todas as operações com paginação.
     *
     * @param pageRequest Configuração de paginação.
     * @return Página de operações.
     */
    @Transactional(readOnly = true)
    public Page<Operacao> findAll(PageRequest pageRequest) {
        log.info("Buscando todas as operações com paginação: {}", pageRequest);
        return operacaoRepository.findAll(pageRequest);
    }

    /**
     * Busca operações aplicando filtros opcionais.
     *
     * @param entradaSaida Tipo de entrada ou saída.
     * @param startDate    Data inicial do filtro.
     * @param endDate      Data final do filtro.
     * @param movimentacao Tipo de movimentação.
     * @param produto      Produto relacionado.
     * @param instituicao  Instituição relacionada.
     * @param duplicado    Indica se as operações duplicadas devem ser incluídas.
     * @param dimensionado Indica se as operações dimensionadas devem ser incluídas.
     * @param pageable     Configuração de paginação.
     * @return Página de operações filtradas.
     * @throws InvalidFilterException Se os filtros forem inválidos.
     */
    public Page<Operacao> findWithFilters(
            String entradaSaida,
            LocalDate startDate,
            LocalDate endDate,
            String movimentacao,
            String produto,
            String instituicao,
            Boolean duplicado,
            Boolean dimensionado,
            Pageable pageable,
            Locale locale
    ) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            log.warn("Data inicial é posterior à data final: {} > {}", startDate, endDate);
            throw new InvalidFilterException("filter.invalid.date_range", messageSource);
        }

        String startDateString = startDate != null ? startDate.format(DATABASE_DATE_FORMATTER) : null;
        String endDateString = endDate != null ? endDate.format(DATABASE_DATE_FORMATTER) : null;

        log.info("Aplicando filtros: entradaSaida={}, startDate={}, endDate={}, movimentacao={}, produto={}, instituicao={}, duplicado={}, dimensionado={}",
                entradaSaida, startDateString, endDateString, movimentacao, produto, instituicao, duplicado, dimensionado);

        return operacaoRepository.findByFilters(
                entradaSaida,
                startDateString,
                endDateString,
                movimentacao,
                produto,
                instituicao,
                duplicado,
                dimensionado,
                pageable
        );
    }

    /**
     * Conta as operações com filtros opcionais.
     *
     * @param entradaSaida Tipo de entrada ou saída.
     * @param startDate    Data inicial do filtro.
     * @param endDate      Data final do filtro.
     * @param movimentacao Tipo de movimentação.
     * @param produto      Produto relacionado.
     * @param instituicao  Instituição relacionada.
     * @param duplicado    Indica se as operações duplicadas devem ser incluídas.
     * @param dimensionado Indica se as operações dimensionadas devem ser incluídas.
     * @return Quantidade de operações filtradas.
     */
    public long countByFilters(
            String entradaSaida,
            LocalDate startDate,
            LocalDate endDate,
            String movimentacao,
            String produto,
            String instituicao,
            Boolean duplicado,
            Boolean dimensionado
    ) {
        String startDateString = startDate != null ? startDate.format(DATABASE_DATE_FORMATTER) : null;
        String endDateString = endDate != null ? endDate.format(DATABASE_DATE_FORMATTER) : null;

        log.info("Contando operações com filtros: entradaSaida={}, startDate={}, endDate={}, movimentacao={}, produto={}, instituicao={}, duplicado={}, dimensionado={}",
                entradaSaida, startDateString, endDateString, movimentacao, produto, instituicao, duplicado, dimensionado);

        return operacaoRepository.countByFilters(
                entradaSaida,
                startDateString,
                endDateString,
                movimentacao,
                produto,
                instituicao,
                duplicado,
                dimensionado
        );
    }

    /**
     * Busca uma operação pelo ID.
     *
     * @param id     ID da operação.
     * @param locale Locale para mensagens internacionalizadas.
     * @return Operação encontrada.
     * @throws OperacaoNotFoundException Se a operação não for encontrada.
     */
    @Transactional(readOnly = true)
    public Operacao findById(Long id, Locale locale) {
        log.info("Buscando operação pelo ID: {}", id);
        return operacaoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Operação não encontrada com ID: {}", id);
                    throw new OperacaoNotFoundException(id, messageSource);
                });
    }

    /**
     * Salva uma nova operação ou atualiza uma existente.
     *
     * @param operacao Operação a ser salva.
     * @return Operação salva.
     */
    @Transactional
    public Operacao save(Operacao operacao) {
        log.info("Salvando operação: {}", operacao);
        return operacaoRepository.save(operacao);
    }

    /**
     * Exclui uma operação pelo ID.
     *
     * @param id     ID da operação.
     * @param locale Locale para mensagens internacionalizadas.
     * @throws OperacaoNotFoundException Se a operação não for encontrada.
     */
    @Transactional
    public void delete(Long id, Locale locale) {
        log.info("Excluindo operação com ID: {}", id);
        Operacao operacao = findById(id, locale); // Verifica se a operação existe
        operacaoRepository.delete(operacao);
        log.info("Operação excluída com sucesso: {}", id);
    }

    /**
     * Processa uma operação, validando e salvando no banco.
     *
     * @param operacao Entidade Operacao a ser processada.
     */
    @Transactional
    public Operacao processarOperacao(Operacao operacao) {
        log.info(messageSource.getMessage("operacao.processing.start", new Object[]{operacao}, Locale.getDefault()));

        // 1. Validar a operação
        validarOperacao(operacao);

        // 2. Verificar duplicidade
        verificarDuplicidade(operacao);

        // 3. Persiste a operação
        Operacao operacaoSalva = salvarOperacao(operacao);

        log.info(messageSource.getMessage("operacao.processing.finish", new Object[]{operacao}, Locale.getDefault()));
        return operacaoSalva;
    }

    /**
     * Valida os dados da operação.
     *
     * @param operacao Entidade Operacao a ser validada.
     */
    private void validarOperacao(Operacao operacao) {
        if (operacao.getUsuario() == null) {
            throw new InvalidDataException(
                    "operacao.validation.usuario.null",
                    messageSource
            );
        }

        if (operacao.getEntradaSaida() == null || operacao.getEntradaSaida().isEmpty()) {
            throw new InvalidDataException(
                    "operacao.validation.entrada_saida.null",
                    messageSource
            );
        }

        if (operacao.getData() == null ) {
            throw new InvalidDataException(
                    "operacao.validation.data.null",
                    messageSource
            );
        }

        if (operacao.getProduto() == null || operacao.getProduto().trim().isEmpty()) {
            throw new InvalidDataException(
                    "operacao.validation.produto.null",
                    messageSource
            );
        }

        if (operacao.getInstituicao() == null || operacao.getInstituicao().trim().isEmpty()) {
            throw new InvalidDataException(
                    "operacao.validation.instituicao.null",
                    messageSource
            );
        }
    }


    /**
     * Verifica se a operação é duplicada.
     *
     * @param operacao Entidade Operacao a ser verificada.
     */
    public void verificarDuplicidade(Operacao operacao) {
        Optional<Operacao> operacaoDuplicada = operacaoRepository.findFirstByDataAndMovimentacaoAndProdutoAndInstituicaoAndQuantidadeAndPrecoUnitarioAndValorOperacaoAndDuplicadoAndUsuario(
                operacao.getData(),
                operacao.getMovimentacao(),
                operacao.getProduto(),
                operacao.getInstituicao(),
                operacao.getQuantidade(),
                operacao.getPrecoUnitario(),
                operacao.getValorOperacao(),
                false,
                operacao.getUsuario()
        );

        if (operacaoDuplicada.isPresent()) {
            log.warn(messageSource.getMessage("operacao.duplicated", new Object[]{operacao}, Locale.getDefault()));
            operacao.setDuplicado(true);
            operacao.setIdOriginal(operacaoDuplicada.get().getId());
        } else {
            operacao.setDuplicado(false);
            operacao.setIdOriginal(null);
        }
    }

    /**
     * Persiste a operação no banco de dados.
     *
     * @param operacao Entidade Operacao a ser salva.
     */
    private Operacao salvarOperacao(Operacao operacao) {
        try {
            log.info(messageSource.getMessage("operacao.saved.success", new Object[]{operacao}, Locale.getDefault()));
            Operacao operacaoSaved = operacaoRepository.save(operacao);

            transacaoService.criarTransacao(operacaoSaved);

            return operacaoSaved;
        } catch (Exception e) {
            log.error(messageSource.getMessage("operacao.saved.error", new Object[]{operacao}, Locale.getDefault()), e);
            throw new RuntimeException(messageSource.getMessage("operacao.saved.error", null, Locale.getDefault()), e);
        }
    }

    /**
     * Busca o usuário por ID.
     *
     * @param userId ID do usuário.
     * @return Entidade Usuario.
     */
    public Usuario buscarUsuarioPorId(Long userId) {
        return usuarioService.buscarUsuarioPorId(userId);
    }
}
