package br.dev.rodrigopinheiro.B3DataManager.application.command.operacao;

import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Dinheiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.Quantidade;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Command imutável que representa os dados necessários para registrar uma operação.
 * Usado como entrada para o RegisterOperacaoUseCase.
 * 
 * <p>Atualizado para usar Value Objects ao invés de tipos primitivos,
 * garantindo maior segurança de tipos e validações automáticas.</p>
 */
public record RegisterOperacaoCommand(
    String entradaSaida,
    LocalDate data,
    String movimentacao,
    String produto,
    String instituicao,
    Quantidade quantidade,
    Dinheiro precoUnitario,
    Dinheiro valorOperacao,
    Boolean duplicado,
    Boolean dimensionado,
    Long idOriginal,
    Boolean deletado,
    UsuarioId usuarioId
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