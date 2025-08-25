package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.rendavariavel.InvalidRendaVariavelException;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.ApiMarketPriceClient;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.api.model.MarketPrice;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.RendaVariavelRepository;
import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.AtivoAcaoDTO;
import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.AtivoFiiDTO;
import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.DadosRendaVariavelCalculados;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar entidades de Renda Variável.
 */
@Service
@Slf4j
public class RendaVariavelService {

    private final RendaVariavelRepository rendaVariavelRepository;
    private final AtivoFinanceiroService ativoFinanceiroService;
    private final PortfolioService portfolioService;
    private final InstituicaoService instituicaoService;
    private final ApiMarketPriceClient apiMarketPriceClient;
    private final MessageSource messageSource;

    public RendaVariavelService(
            RendaVariavelRepository rendaVariavelRepository,
            AtivoFinanceiroService ativoFinanceiroService, PortfolioService portfolioService,
            InstituicaoService instituicaoService,
            ApiMarketPriceClient apiMarketPriceClient, MessageSource messageSource) {
        this.rendaVariavelRepository = rendaVariavelRepository;
        this.ativoFinanceiroService = ativoFinanceiroService;
        this.portfolioService = portfolioService;
        this.instituicaoService = instituicaoService;
        this.apiMarketPriceClient = apiMarketPriceClient;
        this.messageSource = messageSource;
    }


    public RendaVariavel save(RendaVariavel rendaVariavel) {
        // Inserir validações e regras de negócio específicas para renda variável
        return rendaVariavelRepository.save(rendaVariavel);
    }

    public Optional<RendaVariavel> findById(Long id) {
        return rendaVariavelRepository.findById(id);
    }

    public List<RendaVariavel> findAll() {
        return rendaVariavelRepository.findAll();
    }

    public void delete(Long id) {
        rendaVariavelRepository.deleteById(id);
    }


    /**
     * Conta a quantidade de registros de RendaVariavel do tipo FII para um determinado usuário.
     *
     * @param usuarioId ID do usuário.
     * @param locale    Locale para mensagens (mantido para consistência, se necessário em outras validações).
     * @return Número total de registros.
     */
    public Long countByTipoAtivoFiiAndUsuario(Long usuarioId, Locale locale) {
        log.info("Contando registros de RendaVariavel do tipo FII para o usuário com ID: {}", usuarioId);
        // "FII" é a string que identifica os fundos imobiliários no campo tipoRendaVariavel.
        return rendaVariavelRepository.countByTipoRendaVariavelAndAtivoFinanceiroUsuarioId(
                TipoAtivoFinanceiroVariavel.FII.name(),
                usuarioId);
    }

    /**
     * Conta a quantidade de registros de RendaVariavel do tipo AÇÃO para um determinado usuário.
     *
     * @param usuarioId ID do usuário.
     * @param locale    Locale (mantido para assinatura consistente).
     * @return Número total de registros de ações no portfólio do usuário.
     */
    public Long countByTipoAtivoAcoesAndUsuario(Long usuarioId, Locale locale) {
        log.info("Contando registros de Ações para o usuário com ID: {}", usuarioId);
        return rendaVariavelRepository
                .countByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(
                        List.of(
                                TipoAtivoFinanceiroVariavel.ACAO_ON.name(),
                                TipoAtivoFinanceiroVariavel.ACAO_PN.name(),
                                TipoAtivoFinanceiroVariavel.ACAO_UNIT.name()
                        ),
                        usuarioId
                );
    }


    /**
     * Salva uma entidade de Renda Variável associada a uma ação.
     *
     * @param usuarioId     ID do usuário logado.
     * @param rendaVariavel Entidade de renda variável a ser salva.
     * @return Entidade de renda variável salva.
     */
    @Transactional
    public RendaVariavel salvarAcao(Long usuarioId, RendaVariavel rendaVariavel, Locale locale) {
        log.info("Salvando ação para o usuário com ID {}: {}", usuarioId, rendaVariavel);

        RendaVariavel salvo = salvarRendaVariavel(usuarioId, rendaVariavel, locale);

        log.info("Ação salva com sucesso: {}", salvo);
        return salvo;
    }


    public long countByFilters(String tipo, String nome, LocalDate startDate, LocalDate endDate,
                               BigDecimal precoMedioMin, BigDecimal precoMedioMax, Long usuarioId) {
        log.info("Contando FIIs com filtros: tipo={}, nome={}, startDate={}, endDate={}, precoMedioMin={}, precoMedioMax={}, usuarioId={}",
                tipo, nome, startDate, endDate, precoMedioMin, precoMedioMax, usuarioId);
        return rendaVariavelRepository.countByFilters(
                tipo,
                nome,
                startDate,
                endDate,
                precoMedioMin,
                precoMedioMax,
                usuarioId
        );
    }

    /**
     * Novo: conta considerando múltiplos tipos de renda variável.
     */
    public long countByFiltersIn(List<String> tipos,
                                 String nome,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 BigDecimal precoMedioMin,
                                 BigDecimal precoMedioMax,
                                 Long usuarioId) {
        log.info("Contando com filtros (IN): tipos={}, nome={}, datas={}->{}, preços={}->{}, user={}",
                tipos, nome, startDate, endDate, precoMedioMin, precoMedioMax, usuarioId);
        return rendaVariavelRepository.countByFiltersIn(
                tipos, nome, startDate, endDate, precoMedioMin, precoMedioMax, usuarioId
        );
    }

    public Page<RendaVariavel> findWithFilters(String tipo, String nome, LocalDate startDate, LocalDate endDate,
                                               BigDecimal precoMedioMin, BigDecimal precoMedioMax,
                                               Pageable pageable, Long usuarioId, Locale locale) {
        log.info("Aplicando filtros para FIIs: tipo={}, nome={}, startDate={}, endDate={}, precoMedioMin={}, precoMedioMax={}, usuarioId={}",
                tipo, nome, startDate, endDate, precoMedioMin, precoMedioMax, usuarioId);
        return rendaVariavelRepository.findByFilters(
                tipo,
                nome,
                startDate,
                endDate,
                precoMedioMin,
                precoMedioMax,
                usuarioId,
                pageable
        );
    }

    /**
     * NOVO: busca filtrando por uma lista de tipos (p.ex. todas as categorias de ação).
     */
    public Page<RendaVariavel> findWithFiltersIn(List<String> tipos,
                                                 String nome,
                                                 LocalDate startDate,
                                                 LocalDate endDate,
                                                 BigDecimal precoMedioMin,
                                                 BigDecimal precoMedioMax,
                                                 Pageable pageable,
                                                 Long usuarioId,
                                                 Locale locale) {
        log.info("Aplicando filtros (IN) para tipos={}", tipos);
        return rendaVariavelRepository.findByFiltersIn(
                tipos, nome, startDate, endDate,
                precoMedioMin, precoMedioMax,
                usuarioId, pageable
        );
    }


    /**
     * Lista os FIIs (Fundos de Investimento Imobiliário) paginados para o usuário especificado.
     * <p>
     * Este métod realiza as seguintes operações:
     * <ul>
     *   <li>Busca as entidades de RendaVariavel do tipo FII para o usuário, utilizando paginação.</li>
     *   <li>Consulta agregada para obter o total da quantidade de FIIs no portfólio do usuário,
     *       usado para calcular a porcentagem de cada ativo no portfólio.</li>
     *   <li>Obtém os preços de mercado atuais para os FIIs da página, via chamada a uma API externa.</li>
     *   <li>Mapeia os preços de mercado por ticker (nome do ativo) para facilitar a recuperação do preço atual.</li>
     *   <li>Transforma cada entidade em um DTO (AtivoFiiDTO), realizando os cálculos de preço médio,
     *       variação, total investido e porcentagem do portfólio.</li>
     * </ul>
     *
     * @param pageable  objeto Pageable para paginação.
     * @param usuarioId ID do usuário para o qual os FIIs serão listados.
     * @param locale    Localização do usuário para seleção de mensagens.
     * @return Lista de AtivoFiiDTO com os dados formatados para a view.
     */
    public List<AtivoFiiDTO> listByTipoAtivoFiiAndUsuario(Pageable pageable, Long usuarioId, Locale locale) {
        log.info("Listando FIIs paginados para o usuário com ID {}", usuarioId);

        // --------------------------------------------------------------------
        // 1) PASSO 1: Carrega TODAS as RendaVariavel FII (sem paginação)
        //    para descobrir quanto dinheiro há investido em cada ativo.
        // --------------------------------------------------------------------
        List<RendaVariavel> todasRendasFii = rendaVariavelRepository
                .findByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(
                        List.of(TipoAtivoFinanceiroVariavel.FII.name()),
                        usuarioId
                );

        // Agrupa por AtivoFinanceiro
        Map<AtivoFinanceiro, List<RendaVariavel>> rendasPorAtivo = todasRendasFii.stream()
                .collect(Collectors.groupingBy(RendaVariavel::getAtivoFinanceiro));

        // Armazena aqui o resultado para cada ativo: precoMedio(2 dec), total(2 dec), etc.
        Map<AtivoFinanceiro, DadosRendaVariavelCalculados> dadosCalculadosMap = new HashMap<>();

        // Precisamos também do precoAtual, então colete tickers
        List<String> tickersTodos = rendasPorAtivo.keySet().stream()
                .map(AtivoFinanceiro::getNome)
                .distinct()
                .collect(Collectors.toList());
        // Buscar preços de mercado
        // Map<String, BigDecimal> precoPorTicker = carregarPrecoMercado(tickersTodos);

        // 1.1) Calcula precoMedio(2 decimais) e total(2 decimais) para cada ativo
        for (Map.Entry<AtivoFinanceiro, List<RendaVariavel>> entry : rendasPorAtivo.entrySet()) {
            AtivoFinanceiro ativo = entry.getKey();
            List<RendaVariavel> rendas = entry.getValue();

            double somaQuantidade = rendas.stream()
                    .mapToDouble(RendaVariavel::getQuantidade)
                    .sum();

            if (somaQuantidade <= 0) {
                continue;
            }

            // Soma (precoUnitario * quantidade) => somaValores
            BigDecimal somaValores = rendas.stream()
                    .map(rv -> rv.getPrecoUnitario().multiply(BigDecimal.valueOf(rv.getQuantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // precoMedio nao-arredondado
            BigDecimal precoMedioRaw = somaValores.divide(
                    BigDecimal.valueOf(somaQuantidade),
                    6, // mais casas para evitar erro
                    RoundingMode.HALF_UP
            );

            // Arredonda precoMedio para 2 casas
            BigDecimal precoMedio = precoMedioRaw.setScale(2, RoundingMode.HALF_UP);

            // total do ativo = precoMedio(2 dec) * somaQuantidade
            BigDecimal totalDoAtivo = precoMedio.multiply(BigDecimal.valueOf(somaQuantidade))
                    .setScale(2, RoundingMode.HALF_UP);

            // precoAtual da API (2 decimais)
//            BigDecimal precoAtual = precoPorTicker
//                    .getOrDefault(ativo.getNome().replace(".SA", ""), BigDecimal.ZERO)
//                    .setScale(2, RoundingMode.HALF_UP);

            // variação = ((precoAtual - precoMedio) / precoMedio)*100
//            BigDecimal variacao = BigDecimal.ZERO;
//            if (precoMedio.compareTo(BigDecimal.ZERO) > 0) {
//                variacao = precoAtual.subtract(precoMedio)
//                        .divide(precoMedio, 4, RoundingMode.HALF_UP)
//                        .multiply(BigDecimal.valueOf(100))
//                        .setScale(2, RoundingMode.HALF_UP);
//            }

            BigDecimal precoAtual = null;
            BigDecimal variacao = null;


            // Guarda no map
            dadosCalculadosMap.put(ativo,
                    new DadosRendaVariavelCalculados(somaQuantidade, precoMedio, precoAtual, variacao, totalDoAtivo));
        }

        // 1.2) Agora soma todos os totalDoAtivo(2 dec) para descobrir totalInvestidoEmFiis
        BigDecimal totalInvestidoEmFiis = dadosCalculadosMap.values().stream()
                .map(DadosRendaVariavelCalculados::getTotalDoAtivo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --------------------------------------------------------------------
        // 2) PASSO 2: Carrega apenas a página (fiisPage)
        //    e para cada ativo da página, pega do Map o que já calculamos
        // --------------------------------------------------------------------
        Page<RendaVariavel> fiisPage = rendaVariavelRepository
                .findByTipoRendaVariavelAndAtivoFinanceiroUsuarioId(
                        TipoAtivoFinanceiroVariavel.FII.name(),
                        usuarioId,
                        pageable
                );

        Map<AtivoFinanceiro, List<RendaVariavel>> fiisPaginadasPorAtivo = fiisPage.getContent().stream()
                .collect(Collectors.groupingBy(RendaVariavel::getAtivoFinanceiro));

        // --------------------------------------------------------------------
        // 3) PASSO 3: Monta o DTO apenas para os ativos dessa página
        // --------------------------------------------------------------------
        List<AtivoFiiDTO> ativosDTO = new ArrayList<>();

        for (Map.Entry<AtivoFinanceiro, List<RendaVariavel>> entry : fiisPaginadasPorAtivo.entrySet()) {
            AtivoFinanceiro ativo = entry.getKey();

            // Pega o que já calculamos no passo 1
            DadosRendaVariavelCalculados dados = dadosCalculadosMap.get(ativo);
            if (dados == null) {
                // Pode ocorrer se não encontrou ou se quantidade era zero
                continue;
            }

            // Porcentagem = totalDoAtivo / totalInvestidoEmFiis * 100
            BigDecimal porcentagem = BigDecimal.ZERO;
            if (totalInvestidoEmFiis.compareTo(BigDecimal.ZERO) > 0) {
                porcentagem = dados.getTotalDoAtivo()
                        .divide(totalInvestidoEmFiis, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            // Monta o DTO
            AtivoFiiDTO dto = AtivoFiiDTO.builder()
                    .nome(ativo.getNome())
                    .quantidade(dados.getSomaQuantidade())
                    .precoMedio(dados.getPrecoMedio())  // já em 2 decimais
                    .precoAtual(dados.getPrecoAtual())  // 2 decimais
                    .variacao(dados.getVariacao())      // 2 decimais
                    .total(dados.getTotalDoAtivo())     // 2 decimais
                    .porcentagem(porcentagem)           // 2 decimais
                    .build();

            ativosDTO.add(dto);
        }

        return ativosDTO;
    }

    public BigDecimal calcularTotalInvestidoEmFiis(Long usuarioId) {
        List<RendaVariavel> todasRendasFii = rendaVariavelRepository.findByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(
                List.of(TipoAtivoFinanceiroVariavel.FII.name()),
                usuarioId
        );
        return todasRendasFii.stream()
                .map(rv -> rv.getPrecoUnitario().multiply(BigDecimal.valueOf(rv.getQuantidade())).setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Soma o total investido (preçoUnitario * quantidade) em ativos de ações
     * no portfólio de um determinado usuário.
     *
     * @param usuarioId ID do usuário
     * @return BigDecimal com 2 casas decimais representando o total investido em ações
     */
    public BigDecimal calcularTotalInvestidoEmAcoes(Long usuarioId) {
        // Defina aqui os tipos de ações que você considera
        List<String> tiposAcoes = List.of(
                TipoAtivoFinanceiroVariavel.ACAO_ON.name(),
                TipoAtivoFinanceiroVariavel.ACAO_PN.name(),
                TipoAtivoFinanceiroVariavel.ACAO_UNIT.name(),
                TipoAtivoFinanceiroVariavel.ETF.name()
        );

        // Traga todas as transações de compra/venda para esses tipos
        List<RendaVariavel> todasAcoes = rendaVariavelRepository
                .findByTipoRendaVariavelInAndAtivoFinanceiroUsuarioId(tiposAcoes, usuarioId);

        // Agora faça a soma do (preçoUnitario * quantidade)
        return todasAcoes.stream()
                .map(rv ->
                        rv.getPrecoUnitario()
                                .multiply(BigDecimal.valueOf(rv.getQuantidade()))
                                .setScale(2, RoundingMode.HALF_UP)
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * Carrega os preços de mercado e retorna Map<TickerSemPontoSA, PreçoAtualArredondado>.
     */
    private Map<String, BigDecimal> carregarPrecoMercado(List<String> tickers) {
        if (tickers == null || tickers.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MarketPrice> marketPrices = apiMarketPriceClient.fetchMarketPrices(tickers);

//        return marketPrices.stream().collect(Collectors.toMap(
////                mp -> mp.getTicker().replace(".SA", ""),
////                mp -> mp.getPrice().setScale(2, RoundingMode.HALF_UP)
////        ));

        return marketPrices.stream()
                .filter(mp -> mp.getPrice() != null) // Remove entradas inválidas
                .collect(Collectors.toMap(
                        mp -> mp.getTicker().replace(".SA", ""),  // Remove ".SA" do ticker
                        mp -> mp.getPrice().setScale(2, RoundingMode.HALF_UP)
                ));
    }


    /**
     * Atualiza assíncronamente o preço atual e a variação dos ativos FII.
     *
     * @param ativos Lista de AtivoFiiDTO que contém os dados carregados inicialmente (com preço atual e variação padrão).
     * @return CompletableFuture com a lista atualizada de AtivoFiiDTO.
     */
    public CompletableFuture<List<AtivoFiiDTO>> atualizarPrecosAtuaisAsync(List<AtivoFiiDTO> ativos) {
        // Extrai os tickers, removendo a eventual extensão ".SA"
        List<String> tickers = ativos.stream()
                .map(dto -> dto.getNome().replace(".SA", ""))
                .distinct()
                .collect(Collectors.toList());

        // Dispara a chamada assíncrona para obter os preços da API externa com timeout de 20 segundos
        return CompletableFuture.supplyAsync(() -> carregarPrecoMercado(tickers))
                .orTimeout(30, TimeUnit.SECONDS)
                .thenApply(precosMercado -> {
                    // Atualiza cada DTO com o preço atual retornado e recalcula a variação
                    ativos.forEach(dto -> {
                        BigDecimal precoAtual = precosMercado.getOrDefault(dto.getNome().replace(".SA", ""), BigDecimal.ZERO)
                                .setScale(2, RoundingMode.HALF_UP);
                        dto.setPrecoAtual(precoAtual);

                        if (dto.getPrecoMedio() != null && dto.getPrecoMedio().compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal variacao = precoAtual.subtract(dto.getPrecoMedio())
                                    .divide(dto.getPrecoMedio(), 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100))
                                    .setScale(2, RoundingMode.HALF_UP);
                            dto.setVariacao(variacao);
                        }
                    });
                    log.info("Dados retornados");
                    return ativos;
                })
                .exceptionally(ex -> {
                    log.error("Erro ao atualizar preços: {}", ex.getMessage(), ex);
                    // Fallback: após timeout ou exceção, marca os ativos com valor sentinela (-1)
                    ativos.forEach(dto -> {
                        dto.setPrecoAtual(BigDecimal.valueOf(-1)); // -1 indicará falha ao carregar
                        dto.setVariacao(BigDecimal.valueOf(-1));
                    });
                    return ativos;
                });
    }

    /**
     * Atualiza assíncronamente o preço atual e a variação dos ativos AÇÃO.
     *
     * @param ativos Lista de AtivoAcaoDTO que contém os dados carregados inicialmente.
     * @return CompletableFuture com a lista atualizada de AtivoAcaoDTO.
     */
    public CompletableFuture<List<AtivoAcaoDTO>> atualizarPrecosAtuaisAsyncAcoes(List<AtivoAcaoDTO> ativos) {
        List<String> tickers = ativos.stream()
                .map(dto -> dto.getNome().replace(".SA", ""))
                .distinct()
                .collect(Collectors.toList());

        return CompletableFuture.supplyAsync(() -> carregarPrecoMercado(tickers))
                .orTimeout(30, TimeUnit.SECONDS)
                .thenApply(precosMercado -> {
                    ativos.forEach(dto -> {
                        BigDecimal precoAtual = precosMercado
                                .getOrDefault(dto.getNome().replace(".SA", ""), BigDecimal.ZERO)
                                .setScale(2, RoundingMode.HALF_UP);
                        dto.setPrecoAtual(precoAtual);

                        if (dto.getPrecoMedio() != null && dto.getPrecoMedio().compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal variacao = precoAtual.subtract(dto.getPrecoMedio())
                                    .divide(dto.getPrecoMedio(), 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100))
                                    .setScale(2, RoundingMode.HALF_UP);
                            dto.setVariacao(variacao);
                        }
                    });
                    log.info("Preços de ações atualizados via API.");
                    return ativos;
                })
                .exceptionally(ex -> {
                    log.error("Erro ao atualizar preços de ações: {}", ex.getMessage(), ex);
                    ativos.forEach(dto -> {
                        dto.setPrecoAtual(BigDecimal.valueOf(-1));   // sentinela de falha
                        dto.setVariacao(BigDecimal.valueOf(-1));
                    });
                    return ativos;
                });
    }


    @NotNull
    private RendaVariavel salvarRendaVariavel(Long usuarioId, RendaVariavel rendaVariavel, Locale locale) {
        // 1. Recupera (ou cria) o Portfolio do usuário
        Portfolio portfolio = portfolioService.obterOuCriarPortfolio(usuarioId);

        // 2. Verifica ou cria o AtivoFinanceiro associado ao Portfolio
        // Aqui, o métod interno do service de AtivoFinanceiro já obtém o Portfolio a partir do usuarioId.
        AtivoFinanceiro ativoFinanceiro = ativoFinanceiroService.verificarOuCriarAtivoFinanceiro(
                usuarioId,
                rendaVariavel.getAtivoFinanceiro().getNome()
        );
        rendaVariavel.setAtivoFinanceiro(ativoFinanceiro);

        // 3. Garantir que a Instituição esteja configurada corretamente, se necessário.
        instituicaoService.verificarOuCriarInstituicao(
                ativoFinanceiro.getNome(),
                usuarioId,
                locale
        );

        // 4. Validar os campos obrigatórios da Renda (herdados da classe base e os específicos)
        validarRendaVariavel(rendaVariavel, locale);

        // 5. Salvar a entidade RendaVariavel
        RendaVariavel salva = rendaVariavelRepository.save(rendaVariavel);
        log.info("RendaVariavel salva com sucesso: {}", salva);
        return salva;
    }


    private void validarRendaVariavel(RendaVariavel rendaVariavel, Locale locale) {
        if (rendaVariavel.getAtivoFinanceiro() == null || rendaVariavel.getAtivoFinanceiro().getId() == null) {
            throw new InvalidRendaVariavelException("ativo_financeiro.null", messageSource);
        }

        if (rendaVariavel.getPrecoUnitario() == null || rendaVariavel.getPrecoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRendaVariavelException("preco_unitario.invalid", messageSource);
        }

        if (rendaVariavel.getQuantidade() <= 0) {
            throw new InvalidRendaVariavelException("quantidade.invalid", messageSource);
        }

        if (rendaVariavel.getTipoRendaVariavel() == null || rendaVariavel.getTipoRendaVariavel().isEmpty()) {
            throw new InvalidRendaVariavelException("tipo_renda_variavel.invalid", messageSource);
        }
    }

}
