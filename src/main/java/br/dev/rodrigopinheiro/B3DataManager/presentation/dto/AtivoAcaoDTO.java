package br.dev.rodrigopinheiro.B3DataManager.presentation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.RendaVariavelEntity;

@Data
@Builder
public class AtivoAcaoDTO {
    private String nome;
    private double quantidade;
    private BigDecimal precoMedio;
    private BigDecimal precoAtual;   // null até API responder
    private BigDecimal variacao;     // null até API responder
    private BigDecimal total;        // total investido
    private BigDecimal porcentagem;  // % do portfólio
    private String tipoAcao;

    /**
     * Converte uma única RendaVariavel em um DTO de ação,
     * deixando precoAtual e variacao = null para placeholder.
     */
    public static AtivoAcaoDTO from(RendaVariavelEntity rv, BigDecimal totalQuantidadePortfolio) {
        double quantidadeTotal = rv.getQuantidade();
        BigDecimal totalInvestido = rv.getTotal().setScale(2, RoundingMode.HALF_UP);

        BigDecimal precoMedio = quantidadeTotal > 0
                ? totalInvestido.divide(
                BigDecimal.valueOf(quantidadeTotal),
                2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal porcentagem = totalQuantidadePortfolio.compareTo(BigDecimal.ZERO) > 0
                ? BigDecimal.valueOf(quantidadeTotal)
                .divide(totalQuantidadePortfolio, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return AtivoAcaoDTO.builder()
                .nome(rv.getAtivoFinanceiro().getNome())
                .quantidade(quantidadeTotal)
                .precoMedio(precoMedio)
                .precoAtual(null)
                .variacao(null)
                .total(totalInvestido)
                .porcentagem(porcentagem)
                .tipoAcao(rv.getTipoRendaVariavel())
                .build();
    }
}
