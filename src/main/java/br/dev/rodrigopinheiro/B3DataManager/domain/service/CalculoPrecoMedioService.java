package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Renda;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.RendaFixa;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.RendaVariavel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class CalculoPrecoMedioService {

    /**
     * Calcula o custo médio (preço médio) para uma venda de ativos de renda variável utilizando o métod FIFO.
     * <p>
     * A lógica consiste em:
     * - Ordenar as operações de compra (rendas) pela data de compra (mais antiga primeiro).
     * - Percorrer a lista e deduzir, sequencialmente, a quantidade vendida das operações mais antigas.
     * - Somar os custos correspondentes e, ao final, dividir pelo total vendido para obter o preço médio.
     * </p>
     *
     * @param ativo         O ativo financeiro cujo custo médio será calculado.
     * @param quantidadeVenda A quantidade vendida.
     * @return O custo médio calculado como BigDecimal.
     */
    public BigDecimal calcularPrecoMedioVendaFifoRendaVariavel(AtivoFinanceiro ativo, double quantidadeVenda) {
        List<RendaVariavel> rendas = new ArrayList<>(ativo.getRendaVariaveis());
        if (rendas.isEmpty()) {
            log.warn("Nenhuma operação de renda variável encontrada para o ativo: {}", ativo.getNome());
            return BigDecimal.ZERO;
        }
        // Ordena as rendas pela data de compra, do mais antigo para o mais recente
        rendas.sort(Comparator.comparing(Renda::getDataCompra));

        BigDecimal totalCusto = BigDecimal.ZERO;
        double quantidadeRestante = quantidadeVenda;

        for (RendaVariavel renda : rendas) {
            double disponivel = renda.getQuantidade();
            if (quantidadeRestante <= disponivel) {
                totalCusto = totalCusto.add(renda.getPrecoUnitario().multiply(BigDecimal.valueOf(quantidadeRestante)));
                // Aqui, opcionalmente, atualize a quantidade disponível na operação de compra (se o estoque for gerenciado)
                quantidadeRestante = 0;
                break;
            } else {
                totalCusto = totalCusto.add(renda.getPrecoUnitario().multiply(BigDecimal.valueOf(disponivel)));
                quantidadeRestante -= disponivel;
                // Marcar esta operação como totalmente "quitada", se for o caso
            }
        }
        if (quantidadeRestante > 0) {
            throw new IllegalStateException("Quantidade vendida excede a disponível para o ativo " + ativo.getNome());
        }
        BigDecimal precoMedio = totalCusto.divide(BigDecimal.valueOf(quantidadeVenda), 4, RoundingMode.HALF_UP);
        log.info("Custo médio calculado (renda variável) para {}: {}", ativo.getNome(), precoMedio);
        return precoMedio;
    }

    /**
     * Calcula o lucro para uma venda de ativos de renda fixa utilizando o métod FIFO.
     * <p>
     * A lógica consiste em:
     * - Considerar todas as operações de compra de renda fixa do ativo que possuem o mesmo identificador (por exemplo, "CDB - CDB6248PGO1").
     * - Ordenar essas operações pela data de compra (mais antiga primeiro) e deduzir a quantidade vendida sequencialmente.
     * - Calcular o custo total das operações quitadas e, a partir disso, determinar o custo médio.
     * - O lucro é calculado subtraindo o custo médio aplicado à quantidade vendida do valor total da venda.
     * </p>
     *
     * @param ativo          O ativo financeiro.
     * @param quantidadeVenda A quantidade vendida.
     * @param valorVenda      O valor total da venda.
     * @return O lucro obtido na venda, calculado com base no custo médio FIFO.
     */
    public BigDecimal calcularLucroVendaFifoRendaFixa(AtivoFinanceiro ativo, double quantidadeVenda, BigDecimal valorVenda) {
        List<RendaFixa> rendas = new ArrayList<>(ativo.getRendaFixas());
        if (rendas.isEmpty()) {
            log.warn("Nenhuma operação de renda fixa encontrada para o ativo: {}", ativo.getNome());
            return BigDecimal.ZERO;
        }
        // Ordena as rendas fixas pela data de compra, do mais antigo para o mais recente
        rendas.sort(Comparator.comparing(Renda::getDataCompra));

        BigDecimal totalCusto = BigDecimal.ZERO;
        double quantidadeRestante = quantidadeVenda;

        for (RendaFixa renda : rendas) {
            double disponivel = renda.getQuantidade();
            if (quantidadeRestante <= disponivel) {
                totalCusto = totalCusto.add(renda.getPrecoUnitario().multiply(BigDecimal.valueOf(quantidadeRestante)));
                quantidadeRestante = 0;
                break;
            } else {
                totalCusto = totalCusto.add(renda.getPrecoUnitario().multiply(BigDecimal.valueOf(disponivel)));
                quantidadeRestante -= disponivel;
            }
        }
        if (quantidadeRestante > 0) {
            throw new IllegalStateException("Quantidade vendida excede a disponível para o ativo fixo " + ativo.getNome());
        }
        // Calcula o custo médio com base na quantidade vendida
        BigDecimal custoMedio = totalCusto.divide(BigDecimal.valueOf(quantidadeVenda), 4, RoundingMode.HALF_UP);
        BigDecimal lucro = valorVenda.subtract(custoMedio.multiply(BigDecimal.valueOf(quantidadeVenda)));
        log.info("Lucro calculado (renda fixa) para {}: {}", ativo.getNome(), lucro);
        return lucro;
    }
}
