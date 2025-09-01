package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.application.service.AtivoFinanceiroService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.InstituicaoService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.PortfolioService;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.* ;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.OperacaoEntity;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoMovimentacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoTransacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.ativo.AtivoNotFoundException;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.instituicao.InstituicaoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Factory responsável por criar e orquestrar a construção do agregado Transacao a partir dos dados de Operacao.
 *
 * Observações:
 * 1. Todas as validações e lançamentos de exceções (incluindo a tradução de mensagens via MessageSource)
 *    são realizados na camada de aplicação – neste caso, nesta factory – e não nos métodos do domínio.
 *
 * 2. Ao realizar as validações aqui, evitamos que o domínio dependa de detalhes de infraestrutura, mantendo-o "puro"
 *    e focado apenas na lógica de negócio.
 *
 * 3. Caso ocorra alguma inconsistência (por exemplo, ativo financeiro nulo ou operação sem usuário),
 *    esta factory é responsável por lançar a exceção apropriada com mensagens localizadas,
 *    antes de invocar os métodos de associação do domínio.
 *
 * 4. Assim, os métodos do domínio (como associarAtivoFinanceiro e associarInstituicao) não precisam conhecer
 *    o MessageSource, pois a responsabilidade de validação e tradução das mensagens foi deslocada para a camada de aplicação.
 */
@Slf4j
@Service
public class TransacaoFactory {

    private final TipoMovimentacaoMapper movimentacaoResolver;
    private final TipoTransacaoMapper tipoTransacaoMapper;


    public TransacaoFactory(TipoMovimentacaoMapper movimentacaoResolver,
                            TipoTransacaoMapper tipoTransacaoMapper
                             ) {
        this.movimentacaoResolver = movimentacaoResolver;
        this.tipoTransacaoMapper = tipoTransacaoMapper;
    }

    /**
     * Cria uma Transacao a partir dos dados de uma Operacao.
     *
     * @param operacao A operação importada (ex.: do JSON exportado do banco).
     * @return A transação criada, com o ativo financeiro associado.
     */
    @Transactional
    public Transacao criarTransacao(OperacaoEntity operacao) {
        log.info("Iniciando criação da transação para a operação: {}", operacao);

        // 3. Extração e parsing dos dados da operação
        String entradaSaida = operacao.getEntradaSaida();
        double quantidade = operacao.getQuantidade();
        BigDecimal precoUnitario = operacao.getPrecoUnitario();
        BigDecimal valorTotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));

        // 4. Determinação dos tipos de movimentação e transação
        String tipoMovimentacaoStr = movimentacaoResolver.determinarTipoMovimentacao(operacao);
        TipoMovimentacao tipoMovimentacao = TipoMovimentacao.valueOf(tipoMovimentacaoStr);
        TipoTransacao tipoTransacao = tipoTransacaoMapper.mapear(operacao.getEntradaSaida(), operacao.getMovimentacao());

        // 5. Criação da transação (idealmente via construtor ou builder)
        Transacao transacao = new Transacao();
        transacao.setData(operacao.getData());
        transacao.setEntradaSaida(entradaSaida);
        transacao.setQuantidade(quantidade);
        transacao.setPrecoUnitario(precoUnitario);
        transacao.setValorTotal(valorTotal);
        transacao.setTipoTransacao(tipoTransacao);
        transacao.setTipoMovimentacao(tipoMovimentacao);
        transacao.setDeletado(false);

        // 9. Darf ainda não foi gerado; mantemos null para preenchimento futuro
        transacao.setDarf(null);

        // 10. adiciona operacao
        transacao.setOperacao(operacao);

        log.info("Transação criada com sucesso: {}", transacao);
        return transacao;
    }




}
