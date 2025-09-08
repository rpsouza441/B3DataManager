package br.dev.rodrigopinheiro.B3DataManager.presentation.dto;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.RendaVariavelEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record AtivoAcaoDTO(
        String nome,
        double quantidade,
        BigDecimal precoMedio,
        BigDecimal precoAtual,   // null até API responder
        BigDecimal variacao,     // null até API responder
        BigDecimal total,        // total investido
        BigDecimal porcentagem,  // % do portfólio
        String tipoAcao          // exposto como String; mapeado do enum
) {
    /**
     * Converte uma RendaVariavelEntity em AtivoAcaoDTO.
     * precoAtual e variacao ficam null (placeholder).
     */
    public static AtivoAcaoDTO from(RendaVariavelEntity rv, BigDecimal totalQuantidadePortfolio) {
        Objects.requireNonNull(rv, "RendaVariavelEntity não pode ser nula");
        // Trata nulos defensivamente
        totalQuantidadePortfolio = totalQuantidadePortfolio == null ? BigDecimal.ZERO : totalQuantidadePortfolio;

        double quantidadeTotal = rv.getQuantidade(); // mantém seu tipo atual (double)
        BigDecimal totalInvestido = rv.getTotal() == null
                ? BigDecimal.ZERO
                : rv.getTotal().setScale(2, RoundingMode.HALF_UP);

        BigDecimal precoMedio = quantidadeTotal > 0
                ? totalInvestido.divide(BigDecimal.valueOf(quantidadeTotal), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal porcentagem = totalQuantidadePortfolio.compareTo(BigDecimal.ZERO) > 0
                ? BigDecimal.valueOf(quantidadeTotal)
                .divide(totalQuantidadePortfolio, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Corrige o erro: enum -> String (usa name(); troque por label se preferir)
        String tipoAcaoStr = rv.getTipoRendaVariavel() == null
                ? TipoAtivoFinanceiroVariavel.DESCONHECIDO.name()
                : rv.getTipoRendaVariavel().name();

        return new AtivoAcaoDTO(
                rv.getAtivoFinanceiro().getNome(),
                quantidadeTotal,
                precoMedio,
                null,                // precoAtual placeholder
                null,                // variacao placeholder
                totalInvestido,
                porcentagem,
                tipoAcaoStr
        );
    }
}
