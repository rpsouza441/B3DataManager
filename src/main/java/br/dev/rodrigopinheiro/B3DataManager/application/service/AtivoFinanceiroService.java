package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.ServiceException;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.AtivoFinanceiroRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AtivoFinanceiroService {

    private final AtivoFinanceiroRepository ativoFinanceiroRepository;
    private final PortfolioService portfolioService;

    public AtivoFinanceiroService(AtivoFinanceiroRepository ativoRepository,
                                  PortfolioService portfolioService) {
        this.ativoFinanceiroRepository = ativoRepository;
        this.portfolioService = portfolioService;
    }

    /**
     * Verifica se um ativo financeiro já existe para o usuário (através do seu Portfolio).
     * Caso contrário, cria um novo ativo associado ao Portfolio do usuário.
     *
     * @param usuarioId ID do usuário logado.
     * @param nomeAtivo Nome do ativo financeiro.
     * @return O ativo financeiro existente ou criado.
     */
    public AtivoFinanceiro verificarOuCriarAtivoFinanceiro(Long usuarioId, String nomeAtivo) {
        log.info("Verificando ou criando AtivoFinanceiro para usuário {} com nome '{}'", usuarioId, nomeAtivo);
        // Obtém (ou cria) o Portfolio do usuário
        Portfolio portfolio = portfolioService.obterOuCriarPortfolio(usuarioId);
        // Procura um ativo financeiro com o mesmo nome associado a este portfolio
        return ativoFinanceiroRepository.findByNomeAndPortfolio(nomeAtivo, portfolio)
                .orElseGet(() -> criarAtivoFinanceiro(nomeAtivo, portfolio));
    }

    /**
     * Cria um novo ativo financeiro e o associa ao Portfolio do usuário.
     *
     * @param nomeAtivo Nome do ativo financeiro.
     * @param portfolio Portfolio do usuário.
     * @return O ativo financeiro criado.
     */
    private AtivoFinanceiro criarAtivoFinanceiro(String nomeAtivo, Portfolio portfolio) {
        AtivoFinanceiro novoAtivo = new AtivoFinanceiro();
        novoAtivo.setNome(nomeAtivo);
        novoAtivo.setPortfolio(portfolio);
        novoAtivo.setDeletado(false);
        AtivoFinanceiro salvo = ativoFinanceiroRepository.save(novoAtivo);
        log.info("AtivoFinanceiro criado com sucesso: {}", salvo);
        return salvo;
    }

    /**
     * Busca um ativo financeiro pelo ID e valida se pertence ao usuário.
     *
     * @param ativoFinanceiroId ID do ativo financeiro.
     * @param usuarioId         ID do usuário logado.
     * @return O ativo financeiro associado ao usuário.
     * @throws ServiceException Se o ativo não for encontrado ou não pertencer ao usuário.
     */
    public AtivoFinanceiro buscarAtivoPorIdEUsuario(Long ativoFinanceiroId, Long usuarioId) {
        log.info("Buscando AtivoFinanceiro com ID {} para o usuário {}", ativoFinanceiroId, usuarioId);
        AtivoFinanceiro ativo = ativoFinanceiroRepository.findByIdAndDeletadoFalse(ativoFinanceiroId)
                .orElseThrow(() -> new ServiceException("Ativo financeiro não encontrado ou não pertence ao usuário."));
        // Verifica se o ativo pertence ao Portfolio cujo usuário é o mesmo
        if (!ativo.getPortfolio().getUsuario().getId().equals(usuarioId)) {
            throw new ServiceException("Ativo financeiro não encontrado ou não pertence ao usuário.");
        }
        return ativo;
    }

    /**
     * Recupera todos os AtivoFinanceiros não deletados.
     *
     * @return Lista de AtivoFinanceiros não deletados.
     */
    public List<AtivoFinanceiro> findAll() {
        log.info("Buscando todos os AtivoFinanceiros não deletados.");
        return ativoFinanceiroRepository.findByDeletadoFalse();
    }

    /**
     * Busca um ativo financeiro pelo ID, validando se não está deletado.
     *
     * @param id ID do ativo financeiro.
     * @return O ativo financeiro encontrado.
     */
    public Optional<AtivoFinanceiro> findById(Long id) {
        log.info("Buscando AtivoFinanceiro com ID {}", id);
        return ativoFinanceiroRepository.findByIdAndDeletadoFalse(id);
    }

    /**
     * Cria ou atualiza um AtivoFinanceiro.
     *
     * @param ativo O ativo financeiro a ser salvo ou atualizado.
     * @return O ativo salvo ou atualizado.
     */
    public AtivoFinanceiro save(AtivoFinanceiro ativo) {
        log.info("Salvando AtivoFinanceiro: {}", ativo);
        return ativoFinanceiroRepository.save(ativo);
    }

    /**
     * Marca um AtivoFinanceiro como deletado.
     *
     * @param id ID do AtivoFinanceiro a ser marcado como deletado.
     */
    public void deleteById(Long id) {
        log.info("Marcando AtivoFinanceiro com ID {} como deletado.", id);
        AtivoFinanceiro ativo = ativoFinanceiroRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Ativo financeiro não encontrado."));
        ativo.setDeletado(true);
        ativoFinanceiroRepository.save(ativo);
        log.info("AtivoFinanceiro marcado como deletado: {}", id);
    }

    /**
     * Lista paginada de AtivoFinanceiros.
     *
     * @param pageable Paginação.
     * @return Página de AtivoFinanceiros.
     */
    public Page<AtivoFinanceiro> list(Pageable pageable) {
        log.info("Buscando lista paginada de AtivoFinanceiros.");
        return ativoFinanceiroRepository.findAll(pageable);
    }

    /**
     * Lista paginada de AtivoFinanceiros com filtro.
     *
     * @param pageable Paginação.
     * @param filter   Filtro para os AtivoFinanceiros.
     * @return Página de AtivoFinanceiros filtrados.
     */
    public Page<AtivoFinanceiro> list(Pageable pageable, Specification<AtivoFinanceiro> filter) {
        log.info("Buscando lista paginada de AtivoFinanceiros com filtro.");
        return ativoFinanceiroRepository.findAll(filter, pageable);
    }

    // Caso precise de injeção de repositório para buscar dados adicionais, adicione aqui.
    // private final TransacaoRepository transacaoRepository;

    // Construtor com injeção de dependências, se necessário.
    // public AtivoFinanceiroService(TransacaoRepository transacaoRepository) {
    //     this.transacaoRepository = transacaoRepository;
    // }

    /**
     * Calcula o preço médio ponderado de um AtivoFinanceiro
     * com base nas entradas registradas nas instâncias de RendaVariavel.
     *
     * @param ativo O ativo financeiro cujas operações serão consideradas.
     * @return O preço médio ou BigDecimal.ZERO se não houver quantidade.
     */
    public BigDecimal calcularPrecoMedio(AtivoFinanceiro ativo) {
        // Supondo que o preço médio seja calculado a partir das entradas (renda variável)
        List<RendaVariavel> rendas = ativo.getRendaVariaveis();
        if (rendas == null || rendas.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalInvestido = BigDecimal.ZERO;
        double quantidadeTotal = 0.0;

        for (RendaVariavel renda : rendas) {
            // Para cada operação, o valor investido é dado por: preço unitário * quantidade
            BigDecimal totalOperacao = renda.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(renda.getQuantidade()));
            totalInvestido = totalInvestido.add(totalOperacao);
            quantidadeTotal += renda.getQuantidade();
        }

        if (quantidadeTotal == 0) {
            return BigDecimal.ZERO;
        }
        return totalInvestido.divide(BigDecimal.valueOf(quantidadeTotal), RoundingMode.HALF_UP);
    }


    /**
     * Busca no repositório um AtivoFinanceiro com base no ticker e o associa ao Portfolio informado.
     * Se o ativo não for encontrado, cria um novo objeto AtivoFinanceiro com os atributos mínimos
     * e o associa ao Portfolio.
     *
     * @param ticker    O ticker do ativo financeiro a ser buscado ou criado.
     * @param portfolio O portfolio ao qual o ativo financeiro deverá pertencer.
     * @return O ativo financeiro encontrado ou recém-criado, devidamente associado ao portfolio.
     */
    //TODO buscar o fixa e variavel
    public AtivoFinanceiro buscarOuCriarAtivoFinanceiro(String ticker, Portfolio portfolio) {
        // Busca no repositório um ativo financeiro com base no ticker.
        log.info("Buscando ticker {}", ticker);

        Optional<AtivoFinanceiro> optionalAtivo = ativoFinanceiroRepository.findByNomeAndPortfolio(ticker, portfolio);

        if (optionalAtivo.isPresent()) {
            // Se o ativo for encontrado, o recuperamos.
            AtivoFinanceiro ativoExistente = optionalAtivo.get();

            // Verifica se o ativo já está associado ao Portfolio esperado.
            // Se não estiver, associa-o ao portfolio informado.
            if (ativoExistente.getPortfolio() == null) {
                ativoExistente.setPortfolio(portfolio);
            }
            return ativoExistente;
        } else {
            // Se o ativo não for encontrado, cria um novo AtivoFinanceiro com os dados mínimos.
            AtivoFinanceiro novoAtivo = new AtivoFinanceiro();
            novoAtivo.setNome(ticker);
            novoAtivo.setPortfolio(portfolio);

            // Salva o novo ativo no repositório e o retorna.
            return novoAtivo;
        }
    }


}
