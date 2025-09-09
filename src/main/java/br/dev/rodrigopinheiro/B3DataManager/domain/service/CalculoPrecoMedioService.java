package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Transacao;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Comparator;

/**
 * Domain Service para cálculos de preço médio
 * Trabalha apenas com domain models, sem dependências de infrastructure
 */
@Slf4j
public class CalculoPrecoMedioService {

    /**
     * Calcula o preço médio de compra utilizando o método FIFO.
     * <p>
     * A lógica consiste em:
     * - Ordenar as transações de compra pela data (mais antiga primeiro).
     * - Percorrer a lista e deduzir, sequencialmente, a quantidade vendida das operações mais antigas.
     * - Somar os custos correspondentes e, ao final, dividir pelo total vendido para obter o preço médio.
     * </p>
     *
     * @param transacoesCompra Lista de transações de compra do ativo
     * @param quantidadeVenda A quantidade vendida
     * @return O custo médio calculado como BigDecimal
     */
    public BigDecimal calcularPrecoMedioVendaFifo(List<Transacao> transacoesCompra, BigDecimal quantidadeVenda) {
        if (transacoesCompra.isEmpty()) {
            log.warn("Nenhuma transação de compra encontrada para calcular preço médio");
            return BigDecimal.ZERO;
        }
        
        // Ordena as transações pela data, do mais antigo para o mais recente
        transacoesCompra.sort(Comparator.comparing(Transacao::getDataOperacao));

        BigDecimal totalCusto = BigDecimal.ZERO;
        BigDecimal quantidadeRestante = quantidadeVenda;

        for (Transacao transacao : transacoesCompra) {
            BigDecimal disponivel = transacao.getQuantidade();
            if (quantidadeRestante.compareTo(disponivel) <= 0) {
                totalCusto = totalCusto.add(transacao.getPrecoUnitario().multiply(quantidadeRestante));
                quantidadeRestante = BigDecimal.ZERO;
                break;
            } else {
                totalCusto = totalCusto.add(transacao.getPrecoUnitario().multiply(disponivel));
                quantidadeRestante = quantidadeRestante.subtract(disponivel);
            }
        }
        
        if (quantidadeRestante.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Quantidade vendida excede a disponível nas transações de compra");
        }
        
        BigDecimal precoMedio = totalCusto.divide(quantidadeVenda, 4, RoundingMode.HALF_UP);
        log.info("Preço médio calculado (FIFO): {}", precoMedio);
        return precoMedio;
    }

    /**
     * Calcula o lucro de uma venda utilizando o método FIFO.
     * <p>
     * A lógica consiste em:
     * - Ordenar as transações de compra pela data (mais antiga primeiro)
     * - Calcular o custo médio baseado no FIFO
     * - O lucro é a diferença entre o valor da venda e o custo médio
     * </p>
     *
     * @param transacoesCompra Lista de transações de compra do ativo
     * @param quantidadeVenda A quantidade vendida
     * @param valorVenda O valor total da venda
     * @return O lucro obtido na venda, calculado com base no custo médio FIFO
     */
    public BigDecimal calcularLucroVendaFifo(List<Transacao> transacoesCompra, BigDecimal quantidadeVenda, BigDecimal valorVenda) {
        if (transacoesCompra.isEmpty()) {
            log.warn("Nenhuma transação de compra encontrada para calcular lucro");
            return BigDecimal.ZERO;
        }
        
        BigDecimal precoMedio = calcularPrecoMedioVendaFifo(transacoesCompra, quantidadeVenda);
        BigDecimal custoTotal = precoMedio.multiply(quantidadeVenda);
        BigDecimal lucro = valorVenda.subtract(custoTotal);
        
        log.info("Lucro calculado (FIFO): {}", lucro);
        return lucro;
    }
}
