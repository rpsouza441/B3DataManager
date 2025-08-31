package br.dev.rodrigopinheiro.B3DataManager.application.command.operacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Command imutável que representa os dados necessários para registrar uma operação.
 * Usado como entrada para o RegisterOperacaoUseCase.
 */
public record RegisterOperacaoCommand(
    String entradaSaida,
    LocalDate data,
    String movimentacao,
    String produto,
    String instituicao,
    BigDecimal quantidade,
    BigDecimal precoUnitario,
    BigDecimal valorOperacao,
    Boolean duplicado,
    Boolean dimensionado,
    Long idOriginal,
    Boolean deletado,
    Long usuarioId
) {
    
    public RegisterOperacaoCommand {
        Objects.requireNonNull(usuarioId, "UsuarioId é obrigatório");
        Objects.requireNonNull(data, "Data é obrigatória");
        Objects.requireNonNull(produto, "Produto é obrigatório");
        Objects.requireNonNull(quantidade, "Quantidade é obrigatória");
        Objects.requireNonNull(precoUnitario, "Preço unitário é obrigatório");
        Objects.requireNonNull(valorOperacao, "Valor da operação é obrigatório");
    }
}