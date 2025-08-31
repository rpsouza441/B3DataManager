package br.dev.rodrigopinheiro.B3DataManager.application.command.operacao;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Command para listar operações com filtros e paginação.
 * Inclui ownership obrigatório para segurança.
 */
public record ListOperacoesCommand(
    String entradaSaida,
    LocalDate startDate,
    LocalDate endDate,
    String movimentacao,
    String produto,
    String instituicao,
    Boolean duplicado,
    Boolean dimensionado,
    int page,
    int size,
    Long usuarioId
) {
    
    public ListOperacoesCommand {
        Objects.requireNonNull(usuarioId, "UsuarioId é obrigatório");
        if (page < 0) {
            throw new IllegalArgumentException("Page deve ser >= 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size deve ser > 0");
        }
        if (size > 1000) {
            throw new IllegalArgumentException("Size não pode ser maior que 1000");
        }
    }
}