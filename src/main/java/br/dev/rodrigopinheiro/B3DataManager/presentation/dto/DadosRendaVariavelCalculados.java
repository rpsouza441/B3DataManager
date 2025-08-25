package br.dev.rodrigopinheiro.B3DataManager.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DadosRendaVariavelCalculados {
    private double somaQuantidade;
    private BigDecimal precoMedio;
    private BigDecimal precoAtual;
    private BigDecimal variacao;
    private BigDecimal totalDoAtivo;
}
