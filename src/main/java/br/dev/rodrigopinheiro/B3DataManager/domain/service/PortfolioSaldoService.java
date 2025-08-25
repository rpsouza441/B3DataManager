package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Transacao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PortfolioSaldoService {

    private final CalculoPrecoMedioService calculoPrecoMedioService;

    public PortfolioSaldoService(CalculoPrecoMedioService calculoPrecoMedioService) {
        this.calculoPrecoMedioService = calculoPrecoMedioService;
    }

    /**
     * Atualiza os saldos do Portfolio com base na Transação.
     *
     * <p>Se a transação for de entrada, soma o valor total ao saldo aplicado e ao saldo total.
     * Se for de saída, calcula o custo médio (usando FIFO) para o ativo financeiro e, a partir dele,
     * determina o lucro da venda. Essa lógica difere para renda variável e renda fixa.</p>
     *
     * @param portfolio  O Portfolio a ser atualizado.
     * @param transacao  A Transação que impacta os saldos.
     */
    public void atualizarSaldos(Portfolio portfolio, Transacao transacao) {
        // Atualiza o saldo total do portfolio
        portfolio.setSaldoTotal(portfolio.getSaldoTotal().add(transacao.getValorTotal()));

        if ("ENTRADA".equalsIgnoreCase(transacao.getEntradaSaida())) {
            // Para entrada, o saldo aplicado é incrementado
            portfolio.setSaldoAplicado(portfolio.getSaldoAplicado().add(transacao.getValorTotal()));
        } else {
            // Para saída, calcula o lucro com base no custo médio (FIFO)
            AtivoFinanceiro ativo = transacao.getAtivoFinanceiro();
            BigDecimal lucro = BigDecimal.ZERO;
            // Verifica se o ativo possui operações de renda variável
            if (ativo.getRendaVariaveis() != null && !ativo.getRendaVariaveis().isEmpty()) {
                BigDecimal precoMedio = calculoPrecoMedioService.calcularPrecoMedioVendaFifoRendaVariavel(ativo, transacao.getQuantidade());
                lucro = transacao.getValorTotal().subtract(precoMedio.multiply(BigDecimal.valueOf(transacao.getQuantidade())));
            }
            // Caso contrário, para renda fixa, utiliza a lógica FIFO para calcular o lucro
            else if (ativo.getRendaFixas() != null && !ativo.getRendaFixas().isEmpty()) {
                // Aqui, o método calcularLucroVendaFifoRendaFixa já retorna o lucro baseado na lógica FIFO
                lucro = calculoPrecoMedioService.calcularLucroVendaFifoRendaFixa(ativo, transacao.getQuantidade(), transacao.getValorTotal());
            }
            portfolio.setLucroVenda(portfolio.getLucroVenda().add(lucro));
        }
        log.info("Portfolio atualizado: SaldoTotal={}, SaldoAplicado={}, LucroVenda={}",
                portfolio.getSaldoTotal(), portfolio.getSaldoAplicado(), portfolio.getLucroVenda());
    }
}
