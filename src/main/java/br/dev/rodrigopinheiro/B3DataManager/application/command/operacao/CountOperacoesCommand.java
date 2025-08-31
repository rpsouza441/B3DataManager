package br.dev.rodrigopinheiro.B3DataManager.application.command.operacao;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Command para contar operações com filtros.
 * Inclui ownership obrigatório para segurança.
 */
public record CountOperacoesCommand(
    String entradaSaida,
    LocalDate startDate,
    LocalDate endDate,
    String movimentacao,
    String produto,
    String instituicao,
    Boolean duplicado,
    Boolean dimensionado,
    Long usuarioId
) {
    
    public CountOperacoesCommand {
        Objects.requireNonNull(usuarioId, "UsuarioId é obrigatório");
    }
}