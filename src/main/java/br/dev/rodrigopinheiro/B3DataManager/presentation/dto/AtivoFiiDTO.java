package br.dev.rodrigopinheiro.B3DataManager.presentation.dto;

import br.dev.rodrigopinheiro.B3DataManager.domain.entity.RendaVariavel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Builder
@Data
@AllArgsConstructor
public class AtivoFiiDTO {
    private String nome;
    private double quantidade;
    private BigDecimal precoMedio;
    private BigDecimal precoAtual;
    private BigDecimal variacao;    // % de diferença entre o preço médio e o preço atual
    private BigDecimal total;       // saldo: total investido
    private BigDecimal porcentagem; // porcentagem do portfólio

    /**
     * Converte uma entidade RendaVariavel para um DTO AtivoFiiDTO,
     * calculando o total investido neste ativo e a porcentagem do portfólio.
     *
     * @param rv             Registro de RendaVariavel
     * @param totalInvestido Total investido em FIIs no portfólio do usuário
     * @return AtivoFiiDTO correspondente
     */
    public static AtivoFiiDTO from(RendaVariavel rv, BigDecimal totalInvestido) {
        BigDecimal totalDoAtivo = rv.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(rv.getQuantidade()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal porcentagem = BigDecimal.ZERO;
        if (totalInvestido.compareTo(BigDecimal.ZERO) > 0) {
            porcentagem = totalDoAtivo.divide(totalInvestido, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return AtivoFiiDTO.builder()
                .nome(rv.getAtivoFinanceiro().getNome())
                .quantidade(rv.getQuantidade())
                .precoMedio(rv.getPrecoUnitario().setScale(2, RoundingMode.HALF_UP))
                .precoAtual(null)  // Será atualizado posteriormente via API
                .variacao(null)    // Será calculado posteriormente via API
                .total(totalDoAtivo)
                .porcentagem(porcentagem)
                .build();
    }
}
