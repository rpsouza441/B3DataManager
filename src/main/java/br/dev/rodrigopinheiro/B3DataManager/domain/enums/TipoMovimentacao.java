package br.dev.rodrigopinheiro.B3DataManager.domain.enums;


import java.text.Normalizer;
import java.util.Arrays;

public enum TipoMovimentacao {
    CREDITO,
    DEBITO,
    TRANSFERENCIA,
    SUBSCRICAO,
    ATUALIZACAO,
    BONIFICACAO_EM_ATIVOS,
    AMORTIZACAO;
    /**
     * Normaliza uma string e retorna o enum correspondente.
     *
     * @param value String a ser normalizada e convertida.
     * @return TipoMovimentacao correspondente.
     * @throws IllegalArgumentException Se nenhum valor corresponder.
     */
    public static TipoMovimentacao fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Valor inválido para TipoMovimentacao: " + value);
        }
        String normalizedValue = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // Remove acentos
                .toUpperCase()
                .replace(" ", "_"); // Substitui espaços por underline

        return Arrays.stream(values())
                .filter(tipo -> tipo.name().equalsIgnoreCase(normalizedValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum enum encontrado para: " + value));
    }
}
