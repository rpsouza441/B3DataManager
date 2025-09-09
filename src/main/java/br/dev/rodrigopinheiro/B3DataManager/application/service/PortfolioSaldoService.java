package br.dev.rodrigopinheiro.B3DataManager.application.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Portfolio;
import br.dev.rodrigopinheiro.B3DataManager.domain.model.Transacao;
import br.dev.rodrigopinheiro.B3DataManager.domain.service.CalculoPrecoMedioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Application Service para atualização de saldos do portfolio
 * Orquestra operações entre diferentes agregados
 */
@Slf4j
@Service
public class PortfolioSaldoService {

    private final CalculoPrecoMedioService calculoPrecoMedioService;

    public PortfolioSaldoService() {
        // CalculoPrecoMedioService é um domain service puro, sem dependências
        this.calculoPrecoMedioService = new CalculoPrecoMedioService();
    }

    /**
     * Atualiza os saldos do Portfolio com base na Transação.
     *
     * <p>Se a transação for de compra, soma o valor total ao saldo aplicado e ao saldo total.
     * Se for de venda, calcula o lucro com base no custo médio (usando FIFO).</p>
     *
     * @param portfolio O Portfolio a ser atualizado
     * @param transacao A Transação que impacta os saldos
     * @param transacoesCompraAtivo Lista de transações de compra do mesmo ativo (para cálculo FIFO)
     */
    public void atualizarSaldos(Portfolio portfolio, Transacao transacao, List<Transacao> transacoesCompraAtivo) {
        // Atualiza o saldo total do portfolio
        BigDecimal novoSaldoTotal = portfolio.getSaldoTotal().add(transacao.getValorTotal());
        portfolio.setSaldoTotal(novoSaldoTotal);

        if (transacao.isCompra()) {
            // Para compra, o saldo aplicado é incrementado
            BigDecimal novoSaldoAplicado = portfolio.getSaldoAplicado().add(transacao.getValorTotal());
            portfolio.setSaldoAplicado(novoSaldoAplicado);
        } else if (transacao.isVenda()) {
            // Para venda, calcula o lucro com base no custo médio (FIFO)
            BigDecimal lucro = calculoPrecoMedioService.calcularLucroVendaFifo(
                transacoesCompraAtivo, 
                transacao.getQuantidade(), 
                transacao.getValorTotal()
            );
            
            BigDecimal novoLucroVenda = portfolio.getLucroVenda().add(lucro);
            portfolio.setLucroVenda(novoLucroVenda);
        } else if (transacao.isRendimento()) {
            // Para rendimentos, adiciona ao lucro de rendimento
            BigDecimal novoLucroRendimento = portfolio.getLucroRendimento().add(transacao.getValorTotal());
            portfolio.setLucroRendimento(novoLucroRendimento);
        }
        
        log.info("Portfolio atualizado: SaldoTotal={}, SaldoAplicado={}, LucroVenda={}, LucroRendimento={}",
                portfolio.getSaldoTotal(), portfolio.getSaldoAplicado(), portfolio.getLucroVenda(), portfolio.getLucroRendimento());
    }
}
